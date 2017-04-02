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
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.VirtualResourceInstance;

/**
 * An implementation of {@link ResourceProvider} that manages the starting and
 * stopping of {@link VirtualResourceProvider} implementations required by the
 * test class.
 *
 * @author saden
 * @see org.testifyproject.VirtualResourceProvider
 * @see org.testifyproject.annotation.VirtualResource
 */
@Discoverable
public class DefaultVirtualResourceProvider implements ResourceProvider {

    private ServiceLocatorUtil serviceLocatorUtil;
    private ReflectionUtil reflectionUtil;
    private Queue<VirtualResourceProvider> virtualResourceProviders;

    public DefaultVirtualResourceProvider() {
        this(ServiceLocatorUtil.INSTANCE, ReflectionUtil.INSTANCE, new ConcurrentLinkedQueue<>());
    }

    DefaultVirtualResourceProvider(ServiceLocatorUtil serviceLocatorUtil,
            ReflectionUtil reflectionUtil,
            Queue<VirtualResourceProvider> virtualResourceProviders) {
        this.serviceLocatorUtil = serviceLocatorUtil;
        this.reflectionUtil = reflectionUtil;
        this.virtualResourceProviders = virtualResourceProviders;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestReifier testReifier = testContext.getTestReifier();

        Collection<VirtualResource> virtualResources = testDescriptor.getVirtualResources();

        //get container resource annotations from the test class and for each
        //container resource create a new instance, configure and start a
        //container instance and addConfigHandler it to the service instance.
        virtualResources.parallelStream().forEach(virtualResource -> {
            String serviceName = virtualResource.name();
            Class<? extends VirtualResourceProvider> containerProviderType = virtualResource.provider();

            if (serviceName.isEmpty()) {
                serviceName = virtualResource.value();
            }

            VirtualResourceProvider containerProvider;

            if (VirtualResourceProvider.class.equals(containerProviderType)) {
                containerProvider = serviceLocatorUtil.getOne(VirtualResourceProvider.class);
            } else {
                containerProvider = reflectionUtil.newInstance(containerProviderType);
            }

            serviceInstance.inject(containerProvider);
            Object configuration = containerProvider.configure(testContext);
            configuration = testReifier.configure(testContext, configuration);

            VirtualResourceInstance virtualResourceInstance
                    = containerProvider.start(testContext, virtualResource, configuration);

            serviceInstance.addConstant(virtualResourceInstance, serviceName, VirtualResourceInstance.class);

            virtualResourceProviders.add(containerProvider);
        });
    }

    @Override
    public void stop() {
        virtualResourceProviders.parallelStream().forEach(containerProvider -> {
            containerProvider.stop();
        });
    }

}
