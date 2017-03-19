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
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.testifyproject.Instance;
import org.testifyproject.RequiresProvider;
import org.testifyproject.ResourceInstance;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.annotation.RequiresResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of {@link RequiresProvider} that manages the starting and
 * stopping of {@link ResourceProvider} implementations required by the test
 * class.
 *
 * @author saden
 * @see org.testifyproject.RequiresProvider
 * @see org.testifyproject.annotation.RequiresResource
 */
@Discoverable
public class DefaultRequiresResourceProvider implements RequiresProvider {

    private ReflectionUtil reflectionUtil = ReflectionUtil.INSTANCE;
    private Queue<ResourceProvider> resourceProviders = new ConcurrentLinkedQueue<>();

    public DefaultRequiresResourceProvider() {
    }

    public DefaultRequiresResourceProvider(ReflectionUtil reflectionUtil, Queue<ResourceProvider> resourceProviders) {
        this.reflectionUtil = reflectionUtil;
        this.resourceProviders = resourceProviders;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestReifier testReifier = testContext.getTestReifier();

        Collection<RequiresResource> requiresResources = testDescriptor.getRequiresResources();

        //get required resource annotations from the test class and for each
        //required resource create a new instance, configure and start a
        //resource instance and addConfigHandler it to the service instance.
        requiresResources.parallelStream().forEach(requiresResource -> {
            Class<? extends ResourceProvider> resourceProviderType = requiresResource.value();

            ResourceProvider resourceProvider = reflectionUtil.newInstance(resourceProviderType);
            serviceInstance.inject(resourceProvider);
            Object configuration = resourceProvider.configure(testContext);
            configuration = testReifier.configure(testContext, configuration);

            ResourceInstance<?, ?> resourceInstance = resourceProvider.start(testContext, configuration);

            Instance<?> serverInstance = resourceInstance.getServer();

            serviceInstance.replace(serverInstance,
                    requiresResource.serverName(),
                    requiresResource.serverContract());

            Optional<? extends Instance<?>> clientInstanceResult = resourceInstance.getClient();

            if (clientInstanceResult.isPresent()) {
                Instance<?> clientInstance = clientInstanceResult.get();

                serviceInstance.replace(clientInstance,
                        requiresResource.clientName(),
                        requiresResource.clientContract());
            }

            String resourceName = requiresResource.name();
            Class<ResourceInstance> resourceContract = ResourceInstance.class;

            if (resourceName.isEmpty()) {
                serviceInstance.addConstant(resourceInstance, null, resourceContract);
            } else {
                serviceInstance.addConstant(resourceInstance, resourceName, resourceContract);
            }

            resourceProviders.add(resourceProvider);
        });
    }

    @Override
    public void stop() {
        resourceProviders.parallelStream().forEach(ResourceProvider::stop);
    }

}
