/*
 * Copyright 2016-2017 Testify Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.testifyproject.container.docker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import org.testifyproject.TestContext;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.VirtualResourceInstanceBuilder;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.failsafe.Failsafe;
import org.testifyproject.failsafe.RetryPolicy;
import org.testifyproject.google.common.collect.ImmutableMap;
import org.testifyproject.guava.common.net.InetAddresses;
import org.testifyproject.spotify.docker.client.AnsiProgressHandler;
import org.testifyproject.spotify.docker.client.DefaultDockerClient;
import org.testifyproject.spotify.docker.client.exceptions.DockerException;
import org.testifyproject.spotify.docker.client.messages.ContainerConfig;
import org.testifyproject.spotify.docker.client.messages.ContainerCreation;
import org.testifyproject.spotify.docker.client.messages.ContainerInfo;
import org.testifyproject.spotify.docker.client.messages.HostConfig;
import org.testifyproject.spotify.docker.client.messages.PortBinding;
import org.testifyproject.tools.Discoverable;

/**
 * A Docker implementation of {@link VirtualResourceProvider SPI Contract}.
 *
 * @author saden
 */
@Discoverable
public class DockerVirtualResourceProvider
        implements VirtualResourceProvider<VirtualResource, DefaultDockerClient.Builder> {

    public static final String DEFAULT_URI = "http://127.0.0.1:2375";
    public static final String DEFAULT_VERSION = "latest";
    private DefaultDockerClient client;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private ContainerInfo containerInfo;

    @Override
    public DefaultDockerClient.Builder configure(TestContext testContext) {
        return DefaultDockerClient.builder().uri(DEFAULT_URI);
    }

    @Override
    public VirtualResourceInstance start(TestContext testContext, VirtualResource virtualResource, DefaultDockerClient.Builder clientBuilder) {
        try {
            LoggingUtil.INSTANCE.info("Connecting to {}", clientBuilder.uri());
            client = clientBuilder.build();

            String imageName = virtualResource.value();
            String version = virtualResource.version();
            String imageTag = getImageTag(version);

            String image = imageName + ":" + imageTag;
            boolean imagePulled = isImagePulled(image, imageTag);

            if (virtualResource.pull() && !imagePulled) {
                pullImage(virtualResource, image, imageName, imageTag);
            }

            ContainerConfig.Builder containerConfigBuilder = ContainerConfig.builder()
                    .image(image);

            if (!virtualResource.cmd().isEmpty()) {
                containerConfigBuilder.cmd(virtualResource.cmd());
            }

            String containerName = virtualResource.name().isEmpty() ? null : virtualResource.name();

            HostConfig hostConfig = HostConfig.builder()
                    .publishAllPorts(true)
                    .build();

            ContainerConfig containerConfig = containerConfigBuilder
                    .hostConfig(hostConfig)
                    .build();

            ContainerCreation containerCreation = client.createContainer(containerConfig, containerName);
            String containerId = containerCreation.id();
            client.startContainer(containerId);
            started.compareAndSet(false, true);

            containerInfo = client.inspectContainer(containerId);
            InetAddress host = InetAddresses.forString(containerInfo.networkSettings().ipAddress());

            VirtualResourceInstanceBuilder instanceBuilder = VirtualResourceInstanceBuilder.builder()
                    .name(containerName)
                    .address(host);

            ImmutableMap<String, List<PortBinding>> ports = containerInfo.networkSettings().ports();

            if (ports != null) {
                Map<Integer, Integer> mappedPorts = ports.entrySet().stream()
                        .collect(collectingAndThen(toMap(
                                k -> Integer.valueOf(k.getKey().split("/")[0]),
                                v -> Integer.valueOf(v.getValue().get(0).hostPort())),
                                Collections::unmodifiableMap));

                instanceBuilder.mappedPorts(mappedPorts);

                if (virtualResource.await()) {
                    await(virtualResource, mappedPorts, host);
                }
            }

            //Last ditch effort to stop the container
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (started.compareAndSet(true, false)) {
                        DockerVirtualResourceProvider.this.stop(testContext, virtualResource);
                    }
                }
            });

            return instanceBuilder.build();
        } catch (InterruptedException | DockerException e) {
            throw ExceptionUtil.INSTANCE.propagate(e);
        }
    }

    @Override
    public void stop(TestContext testContext, VirtualResource virtualResource) {
        try {
            if (started.compareAndSet(true, false)) {
                String containerId = containerInfo.id();
                LoggingUtil.INSTANCE.info("Stopping and Removing Docker Container {}", containerId);

                RetryPolicy retryPolicy = new RetryPolicy()
                        .retryOn(Throwable.class)
                        .withBackoff(virtualResource.delay(), virtualResource.maxDelay(), virtualResource.unit())
                        .withMaxRetries(virtualResource.maxRetries())
                        .withMaxDuration(virtualResource.maxDuration(), TimeUnit.MILLISECONDS);

                killContainer(containerId, retryPolicy);
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     * Get the image tag based on the given version. If version is not specified
     * then use {@link #DEFAULT_VERSION}
     *
     * @param version the version defined in the VirtualResource annotation.
     * @return the docker image tag
     */
    String getImageTag(String version) {
        String imageTag;
        //if version is not specified then use latest as the image tag
        if (version.isEmpty()) {
            imageTag = DEFAULT_VERSION;
        } else {
            imageTag = version;
        }

        return imageTag;
    }

    /**
     * Determine if the image is already pulled.
     * @param image the image name
     * @param imageTag the image tag
     * @return true if the image is already pulled, false otherwise
     */
    boolean isImagePulled(String image, String imageTag) {
        boolean imagePulled = false;
        //determine if the image has already been pulled
        try {
            client.inspectImage(image);

            //if the tag is not the latest then that means we can look to see if
            //the image has been pulled. if it is then that means we always go and
            //pull the latest image by setting leaving imagePulled as false
            if (!DEFAULT_VERSION.equals(imageTag)) {
                imagePulled = true;
            }
        } catch (InterruptedException | DockerException e) {
            LoggingUtil.INSTANCE.info("Image '{}' not found", image);
        }

        return imagePulled;
    }

    /**
     * Pull the given virtual resource.
     *
     * @param virtualResource the virtual resource
     * @param image the image
     * @param imageName the image name
     * @param imageTag the image tag
     */
    void pullImage(VirtualResource virtualResource, String image, String imageName, String imageTag) {
        RetryPolicy retryPolicy = new RetryPolicy()
                .retryOn(Throwable.class)
                .withBackoff(virtualResource.delay(), virtualResource.maxDelay(), virtualResource.unit())
                .withMaxRetries(virtualResource.maxRetries());

        Failsafe.with(retryPolicy)
                .onRetry(throwable -> LoggingUtil.INSTANCE.warn("Retrying pull request of image '{}'", image, throwable))
                .onFailure(throwable -> LoggingUtil.INSTANCE.error("Image image '{}' could not be pulled: ", image, throwable))
                .run(() -> client.pull(imageName, new AnsiProgressHandler()));
    }

    /**
     * Wait for all the container exposed ports to be available.
     *
     * @param virtualResource the virtual resource
     * @param mappedPorts the container mapped ports
     * @param host the container address
     */
    void await(VirtualResource virtualResource, Map<Integer, Integer> mappedPorts, InetAddress host) {
        RetryPolicy retryPolicy = new RetryPolicy()
                .retryOn(IOException.class)
                .withBackoff(virtualResource.delay(),
                        virtualResource.maxDelay(),
                        virtualResource.unit())
                .withMaxRetries(virtualResource.maxRetries())
                .withMaxDuration(virtualResource.maxDuration(), virtualResource.unit());

        mappedPorts.entrySet().forEach(entry -> Failsafe.with(retryPolicy).run(() -> {
            LoggingUtil.INSTANCE.info("Waiting for '{}:{}' to be reachable", host.getHostAddress(), entry.getKey());
            new Socket(host, entry.getKey()).close();
        }));
    }

    /**
     * Kill the given container using the given retry policy.
     *
     * @param retryPolicy the retry policy in the event of failure
     * @param containerId the container id
     */
    void killContainer(String containerId, RetryPolicy retryPolicy) {
        Failsafe.with(retryPolicy)
                .onRetry(throwable -> LoggingUtil.INSTANCE.info("Trying to kill Docker Container '{}'", containerId))
                .onSuccess(result -> {
                    LoggingUtil.INSTANCE.info("Docker Container '{}' killed", containerId);
                    removeContainer(containerId, retryPolicy);
                })
                .onFailure(throwable -> LoggingUtil.INSTANCE.error("Docker Container '{}' could not be killed", containerId, throwable))
                .run(() -> client.killContainer(containerId));
    }

    /**
     * Remove the given container using the given retry policy.
     *
     * @param retryPolicy the retry policy in the event of failure
     * @param containerId the container id
     */
    void removeContainer(String containerId, RetryPolicy retryPolicy) {
        Failsafe.with(retryPolicy)
                .onRetry(throwable -> LoggingUtil.INSTANCE.info("Trying to remove Docker Container '{}'", containerId))
                .onSuccess(result -> LoggingUtil.INSTANCE.info("Docker Container '{}' removed", containerId))
                .onFailure(throwable -> LoggingUtil.INSTANCE.error("Docker Container '{}' could not be removed", containerId, throwable))
                .run(() -> client.removeContainer(containerId));
    }

}
