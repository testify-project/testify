/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.core.impl;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.testify.ContainerInstance;
import org.testify.ContainerProvider;
import org.testify.RequiresProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.annotation.RequiresContainer;
import org.testify.core.util.ReflectionUtil;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.tools.Discoverable;

/**
 * An implementation of {@link RequiresProvider} that manages the starting and
 * stopping of {@link ContainerProvider} implementations required by the test
 * class.
 *
 * @author saden
 * @see org.testify.ContainerProvider
 * @see org.testify.annotation.RequiresContainer
 */
@Discoverable
public class DefaultRequiresContainerProvider implements RequiresProvider {

    private ServiceLocatorUtil serviceLocatorUtil = ServiceLocatorUtil.INSTANCE;
    private ReflectionUtil reflectionUtil = ReflectionUtil.INSTANCE;
    private Queue<ContainerProvider> containerProviders = new ConcurrentLinkedQueue<>();

    public DefaultRequiresContainerProvider() {
    }

    public DefaultRequiresContainerProvider(ServiceLocatorUtil serviceLocatorUtil,
            ReflectionUtil reflectionUtil,
            Queue<ContainerProvider> containerProviders) {
        this.serviceLocatorUtil = serviceLocatorUtil;
        this.reflectionUtil = reflectionUtil;
        this.containerProviders = containerProviders;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestReifier testReifier = testContext.getTestReifier();

        Collection<RequiresContainer> requiresContainers = testDescriptor.getRequiresContainers();

        //get required container annotations from the test class and for each
        //required container create a new instance, configure and start a
        //container instance and addConfigHandler it to the service instance.
        requiresContainers.parallelStream().forEach(requiresContainer -> {
            String serviceName = requiresContainer.name();
            Class<? extends ContainerProvider> providerType = requiresContainer.provider();

            if (serviceName.isEmpty()) {
                serviceName = requiresContainer.value();
            }

            ContainerProvider containerProvider;

            if (ContainerProvider.class.equals(providerType)) {
                containerProvider = serviceLocatorUtil.getOne(ContainerProvider.class);
            } else {
                containerProvider = reflectionUtil.newInstance(providerType);
            }

            Object configuration = containerProvider.configure(testContext);
            configuration = testReifier.configure(testContext, configuration);

            ContainerInstance containerInstance
                    = containerProvider.start(testContext, requiresContainer, configuration);

            serviceInstance.addConstant(containerInstance, serviceName, ContainerInstance.class);

            containerProviders.add(containerProvider);
        });
    }

    @Override
    public void stop() {
        containerProviders.parallelStream().forEach(containerProvider -> {
            containerProvider.stop();
        });
    }

}
