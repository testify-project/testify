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

import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.testify.ContainerInstance;
import org.testify.ContainerProvider;
import org.testify.RequiresProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import org.testify.core.util.ServiceLocatorUtil;
import static org.testify.guava.common.base.Preconditions.checkState;
import org.testify.tools.Discoverable;

/**
 * A class for start, initializing and destroy required containers.
 *
 * @author saden
 */
@Discoverable
public class RequiresContainerProvider implements RequiresProvider {

    private Set<ContainerProvider> containerProviders;

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        //get required container annotations from the test class and for each
        //required container create a new instance, configure and start a
        //container instance and addConfigHandler it to the service instance.
        this.containerProviders = testContext
                .getTestDescriptor()
                .getRequiresContainers()
                .parallelStream()
                .map(requiresContainer -> {
                    try {
                        ContainerProvider containerProvider;
                        if (requiresContainer.provider().equals(void.class)) {
                            containerProvider = ServiceLocatorUtil.INSTANCE.getOne(ContainerProvider.class);
                        } else {
                            Class<?> requiresProviderType = requiresContainer.provider();
                            checkState(ContainerProvider.class.isAssignableFrom(requiresProviderType),
                                    "The required container type '%s' does not implement ContainerProvider SPI contract. ",
                                    requiresProviderType.getSimpleName());
                            containerProvider = (ContainerProvider) requiresProviderType.newInstance();
                        }

                        Object configuration = containerProvider.configure(testContext);
                        configuration = testContext.getTestReifier().configure(testContext, configuration);

                        ContainerInstance containerInstance = containerProvider.start(
                                testContext, requiresContainer, configuration
                        );

                        String name = requiresContainer.name();

                        //use image name of the qualifier name is not specified
                        if (name.isEmpty()) {
                            name = requiresContainer.value();
                        }

                        Class<?> contract = ContainerInstance.class;

                        serviceInstance.addConstant(containerInstance, name, contract);

                        return containerProvider;
                    } catch (InstantiationException | IllegalAccessException e) {
                        checkState(
                                false,
                                "required container provider '%s' could not be instanticated.",
                                requiresContainer.provider().getSimpleName());
                        return null;
                    }
                })
                .collect(toSet());

    }

    @Override
    public void stop() {
        containerProviders.parallelStream()
                .forEach(ContainerProvider::stop);
    }

}
