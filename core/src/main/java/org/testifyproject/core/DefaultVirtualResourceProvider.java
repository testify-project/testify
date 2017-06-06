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
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.trait.PropertiesReader;

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
    private Map<VirtualResource, VirtualResourceProvider> virtualResourceProviders;

    public DefaultVirtualResourceProvider() {
        this(ServiceLocatorUtil.INSTANCE, ReflectionUtil.INSTANCE, new LinkedHashMap<>());
    }

    DefaultVirtualResourceProvider(ServiceLocatorUtil serviceLocatorUtil,
            ReflectionUtil reflectionUtil,
            Map<VirtualResource, VirtualResourceProvider> virtualResourceProviders) {
        this.serviceLocatorUtil = serviceLocatorUtil;
        this.reflectionUtil = reflectionUtil;
        this.virtualResourceProviders = virtualResourceProviders;
    }

    @Override
    public void start(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        Collection<VirtualResource> virtualResources = testDescriptor.getVirtualResources();

        //get container resource annotations from the test class and for each
        //container resource create a new instance, configure and start a
        //container instance and addConfigHandler it to the service instance.
        virtualResources.parallelStream().forEach(virtualResource -> {
            Class<? extends VirtualResourceProvider> provider = virtualResource.provider();

            VirtualResourceProvider virtualResourceProvider;

            if (VirtualResourceProvider.class.equals(provider)) {
                virtualResourceProvider = serviceLocatorUtil.getOne(VirtualResourceProvider.class);
            } else {
                virtualResourceProvider = reflectionUtil.newInstance(provider);
            }

            serviceInstance.inject(virtualResourceProvider);
            String configKey = virtualResource.configKey();
            PropertiesReader configReader = testContext.getPropertiesReader(configKey);
            Object configuration = virtualResourceProvider.configure(testContext, virtualResource, configReader);
            configuration = testConfigurer.configure(testContext, configuration);

            VirtualResourceInstance<Object> virtualResourceInstance
                    = virtualResourceProvider.start(testContext, virtualResource, configuration);

            processInstance(virtualResource, virtualResourceInstance, serviceInstance);

            virtualResourceProviders.put(virtualResource, virtualResourceProvider);
        });
    }

    void processInstance(VirtualResource virtualResource,
            VirtualResourceInstance<Object> virtualResourceInstance,
            ServiceInstance serviceInstance) {
        String value = virtualResource.value();
        String name = virtualResource.name();

        String resourceInstanceName;
        Class<VirtualResourceInstance> resourceInstanceContract = VirtualResourceInstance.class;

        if (name.isEmpty()) {
            resourceInstanceName = "resource://" + value;
        } else {
            resourceInstanceName = "resource://" + name;
        }

        serviceInstance.addConstant(virtualResourceInstance, resourceInstanceName, resourceInstanceContract);

        processResource(resourceInstanceName, virtualResource, virtualResourceInstance, serviceInstance);
    }

    void processResource(String resourceInstanceName,
            VirtualResource virtualResource,
            VirtualResourceInstance<Object> virtualResourceInstance,
            ServiceInstance serviceInstance) {
        String resourceName = virtualResource.resourceName();
        Class<?> resourceContract = virtualResource.resourceContract();
        Instance resourceInstance = virtualResourceInstance.getResource();

        if (resourceName.isEmpty()) {
            resourceName = resourceInstanceName + "/resource";
        } else {
            resourceName = resourceInstanceName + "/" + resourceName;
        }

        serviceInstance.replace(resourceInstance, resourceName, resourceContract);
    }

    @Override
    public void stop(TestContext testContex) {
        virtualResourceProviders.forEach((virtualResource, virtualResourceProvider)
                -> virtualResourceProvider.stop(testContex, virtualResource)
        );
    }

}
