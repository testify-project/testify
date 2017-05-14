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
package org.testifyproject.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.RemoteResourceProvider;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of {@link ResourceProvider} that manages the starting and
 * stopping of {@link RemoteResourceProvider} implementations required by the
 * test class.
 *
 * @author saden
 * @see org.testifyproject.ResourceProvider
 * @see org.testifyproject.annotation.RemoteResource
 */
@Discoverable
public class DefaultRemoteResourceProvider implements ResourceProvider {

    private ReflectionUtil reflectionUtil;
    private Map<RemoteResource, RemoteResourceProvider> remoteResourceProviders;

    public DefaultRemoteResourceProvider() {
        this(ReflectionUtil.INSTANCE, new LinkedHashMap<>());
    }

    DefaultRemoteResourceProvider(ReflectionUtil reflectionUtil,
            Map<RemoteResource, RemoteResourceProvider> remoteResourceProviders) {
        this.reflectionUtil = reflectionUtil;
        this.remoteResourceProviders = remoteResourceProviders;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        Collection<RemoteResource> remoteResources = testDescriptor.getRemoteResources();

        //get remote resource annotations from the test class and for each
        //remote resource create a new instance, configure and start a
        //resource instance and addConfigHandler it to the service instance.
        remoteResources.parallelStream().forEach(remoteResource -> {
            Class<? extends RemoteResourceProvider> resourceProviderType = remoteResource.value();

            RemoteResourceProvider remoteResourceProvider = reflectionUtil.newInstance(resourceProviderType);
            serviceInstance.inject(remoteResourceProvider);
            Object configuration = remoteResourceProvider.configure(testContext);
            configuration = testConfigurer.configure(testContext, configuration);

            try {
                RemoteResourceInstance<?> remoteResourceInstance
                        = remoteResourceProvider.start(testContext, remoteResource, configuration);

                processClient(remoteResourceInstance, remoteResource, serviceInstance);
                addResource(remoteResourceInstance, remoteResource, serviceInstance);

                remoteResourceProviders.put(remoteResource, remoteResourceProvider);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not start '{}' resource", e, resourceProviderType);
            }
        });
    }

    void addResource(RemoteResourceInstance<?> remoteResourceInstance,
            RemoteResource remoteResource,
            ServiceInstance serviceInstance) {
        String resourceName = remoteResource.name();
        Class<RemoteResourceInstance> resourceContract = RemoteResourceInstance.class;

        if (resourceName.isEmpty()) {
            serviceInstance.addConstant(remoteResourceInstance, null, resourceContract);
        } else {
            serviceInstance.addConstant(remoteResourceInstance, resourceName, resourceContract);
        }
    }

    void processClient(RemoteResourceInstance remoteResourceInstance,
            RemoteResource remoteResource,
            ServiceInstance serviceInstance) {
        Instance clientInstance = remoteResourceInstance.getClient();

        serviceInstance.replace(clientInstance,
                remoteResource.clientName(),
                remoteResource.clientContract());
    }

    @Override
    public void stop(TestContext testContext) {
        remoteResourceProviders.forEach((remoteResource, remoteResourceProvider) -> {
            try {
                remoteResourceProvider.stop(testContext, remoteResource);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not stop '{}' resource", e, remoteResource.value());
            }
        });
    }

}
