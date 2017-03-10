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
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import org.testifyproject.ContainerInstance;
import org.testifyproject.ContainerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.RequiresContainer;
import org.testifyproject.container.docker.callback.PullCallback;
import org.testifyproject.container.docker.callback.WaitCallback;
import org.testifyproject.failsafe.Failsafe;
import org.testifyproject.failsafe.RetryPolicy;
import org.testifyproject.github.dockerjava.api.DockerClient;
import org.testifyproject.github.dockerjava.api.command.CreateContainerCmd;
import org.testifyproject.github.dockerjava.api.command.CreateContainerResponse;
import org.testifyproject.github.dockerjava.api.command.InspectContainerResponse;
import org.testifyproject.github.dockerjava.api.model.ContainerNetwork;
import org.testifyproject.github.dockerjava.api.model.NetworkSettings;
import org.testifyproject.github.dockerjava.core.DockerClientBuilder;
import org.testifyproject.github.dockerjava.core.DockerClientConfig;
import org.testifyproject.github.dockerjava.core.DockerClientConfig.DockerClientConfigBuilder;
import static org.testifyproject.github.dockerjava.core.DockerClientConfig.createDefaultConfigBuilder;
import org.testifyproject.tools.Discoverable;

/**
 * A Docker implementation of {@link ContainerProvider SPI Contract}.
 *
 * @author saden
 */
@Discoverable
public class DockerContainerProvider implements ContainerProvider<RequiresContainer, DockerClientConfigBuilder> {

    public static final String DEFAULT_DAEMON_URI = "tcp://127.0.0.1:2375";

    private DockerClientConfig clientConfig;
    private DockerClient client;
    private CreateContainerResponse containerResponse;
    private TestContext testContext;
    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public DockerClientConfigBuilder configure(TestContext testContext) {
        return createDefaultConfigBuilder().withDockerHost(DEFAULT_DAEMON_URI);
    }

    @Override
    public ContainerInstance start(TestContext testContext,
            RequiresContainer requiresContainer,
            DockerClientConfigBuilder configBuilder) {
        try {
            this.testContext = testContext;

            clientConfig = configBuilder.build();
            testContext.info("Connecting to {}", clientConfig.getDockerHost());
            client = DockerClientBuilder.getInstance(clientConfig).build();

            CountDownLatch latch = new CountDownLatch(1);
            if (requiresContainer.pull()) {
                //TODO: check value first and only pull if it doesn't exist locally
                PullCallback callback = new PullCallback(testContext, requiresContainer, latch);
                client.pullImageCmd(requiresContainer.value())
                        .withTag(requiresContainer.version())
                        .exec(callback);
            } else {
                latch.countDown();
            }

            latch.await(requiresContainer.timeout(), requiresContainer.unit());
            String image = requiresContainer.value() + ":" + requiresContainer.version();

            CreateContainerCmd cmd = client.createContainerCmd(image);
            cmd.withPublishAllPorts(true);

            if (!requiresContainer.cmd().isEmpty()) {
                cmd.withCmd(requiresContainer.cmd());
            }

            if (!requiresContainer.name().isEmpty()) {
                cmd.withName(requiresContainer.name());
            }

            containerResponse = cmd.exec();
            String containerId = containerResponse.getId();
            client.startContainerCmd(containerId).exec();

            InspectContainerResponse inspectResponse = client.inspectContainerCmd(containerId).exec();
            NetworkSettings networkSettings = inspectResponse.getNetworkSettings();

            ContainerNetwork containerNetwork = networkSettings.getNetworks()
                    .values()
                    .stream()
                    .findFirst()
                    .get();

            String host = containerNetwork.getIpAddress();
            List<Integer> ports = networkSettings
                    .getPorts()
                    .getBindings()
                    .entrySet()
                    .parallelStream()
                    .map(p -> p.getKey().getPort())
                    .collect(collectingAndThen(toList(), Collections::unmodifiableList));

            if (requiresContainer.await()) {
                RetryPolicy retryPolicy = new RetryPolicy()
                        .retryOn(IOException.class)
                        .withBackoff(requiresContainer.delay(),
                                requiresContainer.maxDelay(),
                                requiresContainer.unit())
                        .withMaxRetries(requiresContainer.maxRetries())
                        .withMaxDuration(requiresContainer.maxDuration(), requiresContainer.unit());

                ports.parallelStream().forEach(port -> Failsafe.with(retryPolicy).run(() -> {
                    testContext.info("Waiting for '{}:{}' to be reachable", host, port);
                    new Socket(host, port).close();
                }));
            }

            started.compareAndSet(false, true);

            //Last ditch effort to stop the container
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (started.compareAndSet(true, false)) {
                        removeContainer(containerId);
                    }
                }
            });

            return new DockerContainerInstance(inspectResponse.getName(), host, ports);
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            removeContainer(containerResponse.getId());
        }
    }

    private void removeContainer(String containerId) {
        testContext.info("Stopping and Removing Docker Container {}", containerId);
        client.stopContainerCmd(containerId).exec();
        try {
            client.waitContainerCmd(containerId)
                    .exec(new WaitCallback(testContext, containerId))
                    .awaitCompletion();
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }

        RetryPolicy retryPolicy = new RetryPolicy()
                .retryOn(Throwable.class)
                .withBackoff(1000, 8000, TimeUnit.MILLISECONDS)
                .withMaxRetries(3)
                .withMaxDuration(8000, TimeUnit.MILLISECONDS);

        Failsafe.with(retryPolicy)
                .onRetry(throwable -> {
                    testContext.debug("Trying to remove Docker Container {}", containerId, throwable);
                })
                .onSuccess(result -> {
                    testContext.info("Docker Container '{}' Removed", containerId);
                    client.removeVolumeCmd(containerId).exec();
                })
                .onFailure(throwable -> {
                    testContext.error("Docker Container '{}' could not be removed", containerId, throwable);
                })
                .run(() -> {
                    client.removeContainerCmd(containerId).exec();
                });
    }

}
