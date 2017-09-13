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
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.RemoteResourceProvider;
import org.testifyproject.ResourceInstance;
import org.testifyproject.ResourceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.FileSystemUtil;
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
    private FileSystemUtil fileSystemUtil;

    public DefaultRemoteResourceProvider() {
        this(ReflectionUtil.INSTANCE, FileSystemUtil.INSTANCE);
    }

    DefaultRemoteResourceProvider(ReflectionUtil reflectionUtil,
            FileSystemUtil fileSystemUtil) {
        this.reflectionUtil = reflectionUtil;
        this.fileSystemUtil = fileSystemUtil;
    }

    @Override
    public void start(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        Collection<RemoteResource> remoteResources = testDescriptor.getRemoteResources();

        //get remote resource annotations from the test class and for each
        //remote resource create a new instance, configure and start a
        //resource instance and addConfigHandler it to the service instance.
        remoteResources.parallelStream().forEach(remoteResource -> {
            Class<? extends RemoteResourceProvider> value = remoteResource.value();

            RemoteResourceProvider remoteResourceProvider = reflectionUtil.newInstance(value);
            String configKey = remoteResource.configKey();
            PropertiesReader configReader = testContext.getPropertiesReader(configKey);
            Object configuration = remoteResourceProvider.configure(testContext, remoteResource, configReader);
            configuration = testConfigurer.configure(testContext, configuration);

            try {
                //start the resource
                RemoteResourceInstance<Object> remoteResourceInstance
                        = remoteResourceProvider.start(testContext, remoteResource, configuration);

                //determine if there are data files and load the data into te resource
                String[] dataFilePatterns = remoteResource.dataFiles();

                if (dataFilePatterns.length != 0) {
                    //load the data using the resource provider load method
                    Set<Path> dataFiles = fileSystemUtil.findClasspathFiles(dataFilePatterns);
                    remoteResourceProvider.load(testContext, remoteResource, remoteResourceInstance, dataFiles);

                    //if there is data provider defined then create an instance of
                    //it and load data using the data provider as well
                    Class<? extends DataProvider> dataProviderType = remoteResource.dataProvider();

                    if (!DataProvider.class.equals(dataProviderType)) {
                        DataProvider dataProvider = reflectionUtil.newInstance(dataProviderType);
                        dataProvider.load(testContext, dataFiles, remoteResourceInstance);
                    }
                }

                //add resource properties to the test context with its fqn as its key
                testContext.addProperty(remoteResourceInstance.getFqn(), remoteResourceInstance.getProperties());

                //track the resource so it can be stopped later
                ResourceInstance resourceInstance = DefaultResourceInstance.of(
                        remoteResource,
                        remoteResourceProvider,
                        remoteResourceInstance);

                testContext.addCollectionElement(TestContextProperties.REMOTE_RESOURCE_INSTANCES, resourceInstance);
            } catch (Exception e) {
                throw ExceptionUtil.INSTANCE.propagate("Could not start '{}' remote resource", e, value);
            }
        });
    }

    @Override
    public void stop(TestContext testContext) {
        Collection<ResourceInstance<RemoteResource, RemoteResourceProvider, RemoteResourceInstance>> resourceInstances
                = testContext.findCollection(TestContextProperties.REMOTE_RESOURCE_INSTANCES);

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
