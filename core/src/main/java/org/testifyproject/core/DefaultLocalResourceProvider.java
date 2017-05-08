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
import java.util.Optional;
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of {@link ResourceProvider} that manages the starting and
 * stopping of {@link LocalResourceProvider} implementations required by the
 * test class.
 *
 * @author saden
 * @see org.testifyproject.ResourceProvider
 * @see org.testifyproject.annotation.LocalResource
 */
@Discoverable
public class DefaultLocalResourceProvider implements ResourceProvider {

    private ReflectionUtil reflectionUtil;
    private Map<LocalResource, LocalResourceProvider> localResourceProviders;

    public DefaultLocalResourceProvider() {
        this(ReflectionUtil.INSTANCE, new LinkedHashMap<>());
    }

    DefaultLocalResourceProvider(ReflectionUtil reflectionUtil, Map<LocalResource, LocalResourceProvider> localResourceProviders) {
        this.reflectionUtil = reflectionUtil;
        this.localResourceProviders = localResourceProviders;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        Collection<LocalResource> localResources = testDescriptor.getLocalResources();

        //get local resource annotations from the test class and for each
        //local resource create a new instance, configure and start a
        //resource instance and addConfigHandler it to the service instance.
        localResources.parallelStream().forEach(localResource -> {
            Class<? extends LocalResourceProvider> resourceProviderType = localResource.value();

            LocalResourceProvider localResourceProvider = reflectionUtil.newInstance(resourceProviderType);
            serviceInstance.inject(localResourceProvider);
            Object configuration = localResourceProvider.configure(testContext);
            configuration = testConfigurer.configure(testContext, configuration);

            try {
                LocalResourceInstance<?, ?> localResourceInstance
                        = localResourceProvider.start(testContext, localResource, configuration);

                processResource(localResourceInstance, localResource, serviceInstance);
                processClient(localResourceInstance, localResource, serviceInstance);

                String resourceName = localResource.name();
                Class<LocalResourceInstance> resourceContract = LocalResourceInstance.class;

                if (resourceName.isEmpty()) {
                    serviceInstance.addConstant(localResourceInstance, null, resourceContract);
                } else {
                    serviceInstance.addConstant(localResourceInstance, resourceName, resourceContract);
                }

                localResourceProviders.put(localResource, localResourceProvider);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not start '{}' resource", e, resourceProviderType);
            }
        });
    }

    void processClient(LocalResourceInstance localResourceInstance,
            LocalResource localResource,
            ServiceInstance serviceInstance) {
        Optional<? extends Instance<?>> clientInstanceResult = localResourceInstance.getClient();

        if (clientInstanceResult.isPresent()) {
            Instance<?> clientInstance = clientInstanceResult.get();

            serviceInstance.replace(clientInstance,
                    localResource.clientName(),
                    localResource.clientContract());
        }
    }

    void processResource(LocalResourceInstance localResourceInstance,
            LocalResource localResource,
            ServiceInstance serviceInstance) {
        Instance<?> resource = localResourceInstance.getResource();

        serviceInstance.replace(resource,
                localResource.resourceName(),
                localResource.resourceContract());
    }

    @Override
    public void stop(TestContext testContext) {
        localResourceProviders.forEach((localResource, localResourceProvider) -> {
            try {
                localResourceProvider.stop(testContext, localResource);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not stop '{}' resource", e, localResource.value());
            }
        });
    }

}
