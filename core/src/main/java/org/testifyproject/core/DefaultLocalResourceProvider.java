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
import org.testifyproject.LocalResourceInfo;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ResourceInfo;
import org.testifyproject.ResourceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.FileSystemUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.trait.PropertiesReader;

/**
 * An implementation of {@link ResourceProvider} that manages the starting and stopping of
 * {@link LocalResourceProvider} implementations required by the test class.
 *
 * @author saden
 * @see org.testifyproject.ResourceProvider
 * @see org.testifyproject.annotation.LocalResource
 */
@Discoverable
public class DefaultLocalResourceProvider implements ResourceProvider {

    private ReflectionUtil reflectionUtil;
    private FileSystemUtil fileSystemUtil;

    public DefaultLocalResourceProvider() {
        this(ReflectionUtil.INSTANCE, FileSystemUtil.INSTANCE);
    }

    DefaultLocalResourceProvider(ReflectionUtil reflectionUtil,
            FileSystemUtil fileSystemUtil) {
        this.reflectionUtil = reflectionUtil;
        this.fileSystemUtil = fileSystemUtil;
    }

    @Override
    public void start(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        Collection<LocalResource> localResources = testDescriptor.getLocalResources();

        //get local resource annotations from the test class and for each
        //local resource create a new instance, configure and start a
        //resource instance and addConfigHandler it to the service instance.
        localResources.parallelStream().forEach(localResource -> {
            Class<? extends LocalResourceProvider> value = localResource.value();

            LocalResourceProvider localResourceProvider = reflectionUtil
                    .newInstance(value);
            String configKey = localResource.configKey();
            PropertiesReader configReader = testContext.getPropertiesReader(configKey);
            Object configuration = localResourceProvider.configure(testContext,
                    localResource,
                    configReader);
            configuration = testConfigurer.configure(testContext, configuration);

            try {
                //start the resource
                LocalResourceInstance<Object, Object> localResourceInstance =
                        localResourceProvider.start(testContext, localResource,
                                configuration);

                //determine if there are data files and load the data into te resource
                String[] dataFilePatterns = localResource.dataFiles();

                if (dataFilePatterns.length != 0) {
                    //load the data using the resource provider load method
                    Set<Path> dataFiles = fileSystemUtil.findClasspathFiles(
                            dataFilePatterns);
                    localResourceProvider
                            .load(testContext, localResource, localResourceInstance,
                                    dataFiles);

                    //if there is data provider defined then create an instance of
                    //it and load data using the data provider as well
                    Class<? extends DataProvider> dataProviderType = localResource
                            .dataProvider();

                    if (!DataProvider.class.equals(dataProviderType)) {
                        DataProvider dataProvider = reflectionUtil.newInstance(
                                dataProviderType);
                        dataProvider.load(testContext, dataFiles, localResourceInstance);
                    }
                }

                //add resource properties to the test context with its fqn as its key
                testContext.addProperty(localResourceInstance.getFqn(),
                        localResourceInstance
                                .getProperties());

                //track the resource so it can be stopped later
                ResourceInfo resourceInstance = DefaultLocalResourceInfo.of(
                        localResource,
                        localResourceProvider,
                        localResourceInstance);

                testContext.addCollectionElement(
                        TestContextProperties.LOCAL_RESOURCE_INSTANCES,
                        resourceInstance);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could not start '{}' local resource", e,
                        value);
            }
        });
    }

    @Override
    public void stop(TestContext testContext) {
        Collection<LocalResourceInfo> resourceInstances = testContext.getLocalResources();

        resourceInstances.forEach(resourceInstance -> {
            try {
                LocalResourceProvider provider = resourceInstance.getProvider();
                LocalResource localResource = resourceInstance.getAnnotation();
                LocalResourceInstance instance = resourceInstance.getValue();

                provider.stop(testContext, localResource, instance);
            } catch (Exception e) {
                LoggingUtil.INSTANCE.error("Could not stop '{}' local resource",
                        resourceInstance.getAnnotation().value(), e);
            }
        });
    }

}
