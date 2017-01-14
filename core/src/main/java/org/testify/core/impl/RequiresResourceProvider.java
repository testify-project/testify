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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.testify.Instance;
import org.testify.RequiresProvider;
import org.testify.ResourceInstance;
import org.testify.ResourceProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import org.testify.annotation.RequiresResource;
import static org.testify.guava.common.base.Preconditions.checkState;
import org.testify.tools.Discoverable;

/**
 * A class for start, initializing and destroy required resource for testings.
 *
 * @author saden
 */
@Discoverable
public class RequiresResourceProvider implements RequiresProvider {

    private Set<ResourceProvider> resourceProviders;

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        List<RequiresResource> requiresResources = testContext.getTestDescriptor().getRequiresResources();
        //get required resource annotations from the test class and for each
        //required resource create a new instance, configure and start a
        //resource instance and addConfigHandler it to the service instance.
        this.resourceProviders = requiresResources
                .parallelStream()
                .map(annotation -> {
                    try {
                        Class<?> requiresProviderType = annotation.value();
                        checkState(ResourceProvider.class.isAssignableFrom(requiresProviderType),
                                "The required resource for testing type '%s' does not implement "
                                + "RequiresResourceProvider SPI contract. ",
                                requiresProviderType.getSimpleName());

                        ResourceProvider provider = (ResourceProvider) requiresProviderType.newInstance();
                        Object configuration = provider.configure(testContext);
                        configuration = testContext.getTestReifier().configure(configuration);
                        ResourceInstance<?, ?> resourceInstance = provider.start(testContext, configuration);

                        Instance<?> serverInstance = resourceInstance.getServer();
                        addResource(serviceInstance,
                                serverInstance,
                                annotation.serverName(),
                                annotation.serverContract());

                        if (resourceInstance.getClient().isPresent()) {
                            Instance<?> clientInstance = resourceInstance.getClient().get();
                            addResource(serviceInstance,
                                    clientInstance,
                                    annotation.clientName(),
                                    annotation.clientContract());
                        }

                        String name = annotation.name();

                        if (name.isEmpty()) {
                            serviceInstance.addConstant(resourceInstance, null, ResourceInstance.class);
                        } else {
                            serviceInstance.addConstant(resourceInstance, name, ResourceInstance.class);
                        }

                        return provider;
                    } catch (InstantiationException | IllegalAccessException e) {
                        checkState(
                                false,
                                "required resource for testing provider '%s' could not be instanticated.",
                                annotation.value().getSimpleName());
                        return null;
                    }
                })
                .collect(toSet());
    }

    @Override
    public void stop() {
        resourceProviders.parallelStream().forEach(ResourceProvider::stop);
    }

    private void addResource(
            ServiceInstance serviceInstance,
            Instance resourceInstance,
            String name, Class<?> contract) {
        Object instance = resourceInstance.getInstance();
        Optional<String> optName = resourceInstance.getName();
        Optional<Class<?>> optContract = resourceInstance.getContract();

        String serviceName = null;
        if (!name.isEmpty()) {
            serviceName = name;
        } else if (optName.isPresent()) {
            serviceName = optName.get();
        }

        Class<?> serviceContract = null;
        if (!Class.class.equals(contract)) {
            serviceContract = contract;
        } else if (optContract.isPresent()) {
            serviceContract = optContract.get();
        }

        serviceInstance.replace(instance, serviceName, serviceContract);
    }
}
