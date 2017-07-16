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

import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.RemoteResourceProvider;
import org.testifyproject.ResourceInstance;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.trait.PropertiesReader;

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
    private List<ResourceInstance<RemoteResource, RemoteResourceProvider, RemoteResourceInstance>> resourceInstances;

    public DefaultRemoteResourceProvider() {
        this(ReflectionUtil.INSTANCE, new LinkedList<>());
    }

    DefaultRemoteResourceProvider(ReflectionUtil reflectionUtil,
            List<ResourceInstance<RemoteResource, RemoteResourceProvider, RemoteResourceInstance>> resourceInstances) {
        this.reflectionUtil = reflectionUtil;
        this.resourceInstances = resourceInstances;
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
            Class<? extends RemoteResourceProvider> value = remoteResource.value();

            RemoteResourceProvider remoteResourceProvider = reflectionUtil.newInstance(value);
            serviceInstance.inject(remoteResourceProvider);
            String configKey = remoteResource.configKey();
            PropertiesReader configReader = testContext.getPropertiesReader(configKey);
            Object configuration = remoteResourceProvider.configure(testContext, remoteResource, configReader);
            configuration = testConfigurer.configure(testContext, configuration);

            try {
                //start the resource
                RemoteResourceInstance<Object> remoteResourceInstance
                        = remoteResourceProvider.start(testContext, remoteResource, configuration);

                //add resource properties to the test context with its fqn as its key
                testContext.addProperty(remoteResourceInstance.getFqn(), remoteResourceInstance.getProperties());

                //process the resource instance
                processInstance(remoteResource, remoteResourceInstance, value, serviceInstance);

                //track the resource so it can be stopped later
                ResourceInstance resourceInstance = DefaultResourceInstance.of(
                        remoteResource,
                        remoteResourceProvider,
                        remoteResourceInstance);

                resourceInstances.add(resourceInstance);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not start '{}' remote resource", e, value);
            }
        });
    }

    void processInstance(RemoteResource remoteResource,
            RemoteResourceInstance<Object> remoteResourceInstance,
            Class<? extends RemoteResourceProvider> value,
            ServiceInstance serviceInstance) {

        String name = remoteResource.name();
        String resourceInstanceName;
        Class<RemoteResourceInstance> resourceInstanceContract = RemoteResourceInstance.class;

        if (name.isEmpty()) {
            resourceInstanceName = Paths.get("resource:/", remoteResourceInstance.getFqn()).normalize().toString();
        } else {
            resourceInstanceName = Paths.get("resource:/", name).normalize().toString();
        }

        serviceInstance.addConstant(remoteResourceInstance, resourceInstanceName, resourceInstanceContract);

        processResource(resourceInstanceName, remoteResource, remoteResourceInstance, serviceInstance);
    }

    void processResource(String resourceInstanceName,
            RemoteResource remoteResource,
            RemoteResourceInstance<Object> remoteResourceInstance,
            ServiceInstance serviceInstance) {
        String resourceName = remoteResource.resourceName();
        Class<?> resourceContract = remoteResource.resourceContract();
        Instance resourceInstance = remoteResourceInstance.getResource();

        if (resourceName.isEmpty()) {
            resourceName = Paths.get(resourceInstanceName, "resource").toString();
        } else {
            resourceName = Paths.get(resourceInstanceName, resourceName).normalize().toString();
        }

        serviceInstance.replace(resourceInstance, resourceName, resourceContract);
    }

    @Override
    public void stop(TestContext testContext) {
        resourceInstances.forEach(resourceInstance -> {
            try {
                RemoteResourceProvider provider = resourceInstance.getProvider();
                RemoteResource remoteResource = resourceInstance.getAnnotation();
                RemoteResourceInstance instance = resourceInstance.getValue();

                provider.stop(testContext, remoteResource, instance);
            } catch (Exception e) {
                LoggingUtil.INSTANCE.error("Could not stop '{}' remote resource",
                        resourceInstance.getAnnotation().value(), e);
            }
        });
    }

}
