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

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import org.testifyproject.DataProvider;
import org.testifyproject.ResourceInfo;
import org.testifyproject.ResourceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.VirtualResourceInfo;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.FileSystemUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.trait.PropertiesReader;

/**
 * An implementation of {@link ResourceProvider} that manages the starting and stopping of
 * {@link VirtualResourceProvider} implementations required by the test class.
 *
 * @author saden
 * @see org.testifyproject.VirtualResourceProvider
 * @see org.testifyproject.annotation.VirtualResource
 */
@Discoverable
public class DefaultVirtualResourceProvider implements ResourceProvider {

    private ReflectionUtil reflectionUtil;
    private FileSystemUtil fileSystemUtil;
    private ServiceLocatorUtil serviceLocatorUtil;

    public DefaultVirtualResourceProvider() {
        this(
                ReflectionUtil.INSTANCE,
                FileSystemUtil.INSTANCE,
                ServiceLocatorUtil.INSTANCE
        );
    }

    DefaultVirtualResourceProvider(ReflectionUtil reflectionUtil,
            FileSystemUtil fileSystemUtil,
            ServiceLocatorUtil serviceLocatorUtil) {
        this.reflectionUtil = reflectionUtil;
        this.fileSystemUtil = fileSystemUtil;
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        Collection<VirtualResource> virtualResources = testDescriptor
                .getVirtualResources();

        //get container resource annotations from the test class and for each
        //container resource create a new instance, configure and start a
        //container instance and addConfigHandler it to the service instance.
        virtualResources.parallelStream().forEach(virtualResource -> {
            Class<? extends VirtualResourceProvider> provider = virtualResource.provider();

            VirtualResourceProvider virtualResourceProvider;

            if (VirtualResourceProvider.class.equals(provider)) {
                virtualResourceProvider = serviceLocatorUtil.getOne(
                        VirtualResourceProvider.class);
            } else {
                virtualResourceProvider = reflectionUtil.newInstance(provider);
            }

            String configKey = virtualResource.configKey();
            PropertiesReader configReader = testContext.getPropertiesReader(configKey);
            Object configuration = virtualResourceProvider.configure(testContext,
                    virtualResource,
                    configReader);
            configuration = testConfigurer.configure(testContext, configuration);

            try {
                VirtualResourceInstance<Object> virtualResourceInstance =
                        virtualResourceProvider.start(testContext, virtualResource,
                                configuration);

                //determine if there are data files and load the data into te resource
                String[] dataFilePatterns = virtualResource.dataFiles();

                if (dataFilePatterns.length != 0) {
                    //load the data using the resource provider load method
                    Set<Path> dataFiles = fileSystemUtil.findClasspathFiles(
                            dataFilePatterns);
                    virtualResourceProvider.load(testContext, virtualResource,
                            virtualResourceInstance, dataFiles);

                    //if there is data provider defined then create an instance of
                    //it and load data using the data provider as well
                    Class<? extends DataProvider> dataProviderType = virtualResource
                            .dataProvider();

                    if (!DataProvider.class.equals(dataProviderType)) {
                        DataProvider dataProvider = reflectionUtil.newInstance(
                                dataProviderType);
                        dataProvider.load(testContext, dataFiles, virtualResourceInstance);
                    }
                }

                //add resource properties to the test context with its fqn as its key
                testContext.addProperty(virtualResourceInstance.getFqn(),
                        virtualResourceInstance
                                .getProperties());

                //track the resource so it can be stopped later
                ResourceInfo resourceInstance = DefaultVirtualResourceInfo.of(
                        virtualResource,
                        virtualResourceProvider,
                        virtualResourceInstance);

                testContext.addCollectionElement(
                        TestContextProperties.VIRTUAL_RESOURCE_INSTANCES,
                        resourceInstance);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could not start '{}' virtual resource",
                        e, virtualResource.value());
            }
        });
    }

    @Override
    public void stop(TestContext testContext) {
        Collection<VirtualResourceInfo> resourceInstances = testContext
                .getVirtualResources();

        resourceInstances.forEach(resourceInstance -> {
            try {
                VirtualResourceProvider provider = resourceInstance.getProvider();
                VirtualResource virtualResource = resourceInstance.getAnnotation();
                VirtualResourceInstance instance = resourceInstance.getValue();

                provider.stop(testContext, virtualResource, instance);
            } catch (Exception e) {
                LoggingUtil.INSTANCE.error("Could not stop '{}' virtual resource",
                        resourceInstance.getAnnotation().value(), e);
            }
        });
    }

}
