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
import org.testifyproject.trait.PropertiesReader;

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
            Class<? extends LocalResourceProvider> value = localResource.value();

            LocalResourceProvider localResourceProvider = reflectionUtil.newInstance(value);
            serviceInstance.inject(localResourceProvider);
            String configKey = localResource.configKey();
            PropertiesReader configReader = testContext.getPropertiesReader(configKey);
            Object configuration = localResourceProvider.configure(testContext, localResource, configReader);
            configuration = testConfigurer.configure(testContext, configuration);

            try {
                //start the resource
                LocalResourceInstance<Object, Object> localResourceInstance
                        = localResourceProvider.start(testContext, localResource, configuration);

                //add resource properties to the test context with its fqn as its key
                testContext.addProperty(localResourceInstance.getFqn(), localResourceInstance.getProperties());

                //process the resource instance
                processInstance(localResource, localResourceInstance, value, serviceInstance);

                //track the resource so it can be stopped later
                localResourceProviders.put(localResource, localResourceProvider);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not start '{}' resource", e, value);
            }
        });
    }

    void processInstance(LocalResource localResource,
            LocalResourceInstance<Object, Object> localResourceInstance,
            Class<? extends LocalResourceProvider> value,
            ServiceInstance serviceInstance) {
        String name = localResource.name();

        String resourceInstanceName;
        Class<LocalResourceInstance> resourceInstanceContract = LocalResourceInstance.class;

        if (name.isEmpty()) {
            resourceInstanceName = "resource://" + value.getSimpleName();
        } else {
            resourceInstanceName = "resource://" + name;
        }

        serviceInstance.addConstant(localResourceInstance, resourceInstanceName, resourceInstanceContract);

        processResource(resourceInstanceName, localResource, localResourceInstance, serviceInstance);
        processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);
    }

    void processResource(String resourceInstanceName,
            LocalResource localResource,
            LocalResourceInstance<Object, Object> localResourceInstance,
            ServiceInstance serviceInstance) {
        String resourceName = localResource.resourceName();
        Class<?> resourceContract = localResource.resourceContract();

        if (resourceName.isEmpty()) {
            resourceName = resourceInstanceName + "/resource";
        } else {
            resourceName = resourceInstanceName + "/" + resourceName;
        }

        Instance resourceInstance = localResourceInstance.getResource();
        serviceInstance.replace(resourceInstance, resourceName, resourceContract);
    }

    void processClient(String resourceInstanceName,
            LocalResource localResource,
            LocalResourceInstance<Object, Object> localResourceInstance,
            ServiceInstance serviceInstance) {
        localResourceInstance.getClient().ifPresent(clientInstance -> {
            String clientName = localResource.clientName();
            Class<?> clientContract = localResource.clientContract();

            if (clientName.isEmpty()) {
                clientName = resourceInstanceName + "/client";
            } else {
                clientName = resourceInstanceName + "/" + clientName;
            }

            serviceInstance.replace(clientInstance, clientName, clientContract);
        });
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
