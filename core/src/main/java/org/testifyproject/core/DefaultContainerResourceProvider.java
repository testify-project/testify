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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.testifyproject.ContainerInstance;
import org.testifyproject.ContainerResourceProvider;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.annotation.ContainerResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of {@link ResourceProvider} that manages the starting and
 * stopping of {@link ContainerResourceProvider} implementations required by the
 * test class.
 *
 * @author saden
 * @see org.testifyproject.ContainerResourceProvider
 * @see org.testifyproject.annotation.ContainerResource
 */
@Discoverable
public class DefaultContainerResourceProvider implements ResourceProvider {

    private ServiceLocatorUtil serviceLocatorUtil;
    private ReflectionUtil reflectionUtil;
    private Queue<ContainerResourceProvider> containerResourceProviders;

    public DefaultContainerResourceProvider() {
        this(ServiceLocatorUtil.INSTANCE, ReflectionUtil.INSTANCE, new ConcurrentLinkedQueue<>());
    }

    DefaultContainerResourceProvider(ServiceLocatorUtil serviceLocatorUtil,
            ReflectionUtil reflectionUtil,
            Queue<ContainerResourceProvider> containerResourceProviders) {
        this.serviceLocatorUtil = serviceLocatorUtil;
        this.reflectionUtil = reflectionUtil;
        this.containerResourceProviders = containerResourceProviders;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestReifier testReifier = testContext.getTestReifier();

        Collection<ContainerResource> containerResources = testDescriptor.getContainerResources();

        //get container resource annotations from the test class and for each
        //container resource create a new instance, configure and start a
        //container instance and addConfigHandler it to the service instance.
        containerResources.parallelStream().forEach(containerResource -> {
            String serviceName = containerResource.name();
            Class<? extends ContainerResourceProvider> containerProviderType = containerResource.provider();

            if (serviceName.isEmpty()) {
                serviceName = containerResource.value();
            }

            ContainerResourceProvider containerProvider;

            if (ContainerResourceProvider.class.equals(containerProviderType)) {
                containerProvider = serviceLocatorUtil.getOne(ContainerResourceProvider.class);
            } else {
                containerProvider = reflectionUtil.newInstance(containerProviderType);
            }

            serviceInstance.inject(containerProvider);
            Object configuration = containerProvider.configure(testContext);
            configuration = testReifier.configure(testContext, configuration);

            ContainerInstance containerInstance
                    = containerProvider.start(testContext, containerResource, configuration);

            serviceInstance.addConstant(containerInstance, serviceName, ContainerInstance.class);

            containerResourceProviders.add(containerProvider);
        });
    }

    @Override
    public void stop() {
        containerResourceProviders.parallelStream().forEach(containerProvider -> {
            containerProvider.stop();
        });
    }

}
