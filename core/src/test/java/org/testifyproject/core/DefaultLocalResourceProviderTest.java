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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.DataProvider;
import org.testifyproject.LocalResourceInfo;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ResourceInfo;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.util.FileSystemUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.fixture.resource.TestDataProvider;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.trait.PropertiesReader;

/**
 *
 * @author saden
 */
public class DefaultLocalResourceProviderTest {

    DefaultLocalResourceProvider sut;
    ReflectionUtil reflectionUtil;
    FileSystemUtil fileSystemUtil;

    @Before
    public void init() {
        reflectionUtil = mock(ReflectionUtil.class);
        fileSystemUtil = mock(FileSystemUtil.class);

        sut = spy(new DefaultLocalResourceProvider(reflectionUtil, fileSystemUtil));
    }

    @Test
    public void verifyDefaultConstructor() {
        DefaultLocalResourceProvider result = new DefaultLocalResourceProvider();

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextCallToStartShouldThrowException() {
        TestContext testContext = null;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        sut.start(testContext);
    }

    @Test
    public void callToStartWithoutLocalResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<LocalResource> virtualResources = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);

        sut.start(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getLocalResources();
        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithLocalResourcesShouldStartResources() throws Exception {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        LocalResource localResource = mock(LocalResource.class);
        List<LocalResource> virtualResources = ImmutableList.of(localResource);
        LocalResourceProvider localResourceProvider = mock(LocalResourceProvider.class);
        Object configuration = mock(Object.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        String[] dataFilePatterns = {"test.class"};
        Set<Path> dataFiles = mock(Set.class);
        Class dataProviderType = TestDataProvider.class;
        DataProvider dataProvider = mock(TestDataProvider.class);
        String fqn = "fqn";
        Map<String, Object> properties = mock(Map.class);

        Class value = LocalResourceProvider.class;
        String configKey = "test";
        PropertiesReader configReader = mock(PropertiesReader.class);

        ResourceInfo<LocalResource, LocalResourceProvider, LocalResourceInstance> resourceInstance =
                DefaultLocalResourceInfo.of(localResource, localResourceProvider,
                        localResourceInstance);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);
        given(localResource.value()).willReturn(value);
        given(reflectionUtil.newInstance(value)).willReturn(localResourceProvider);
        given(localResource.configKey()).willReturn(configKey);
        given(testContext.getPropertiesReader(configKey)).willReturn(configReader);
        given(localResourceProvider.configure(testContext, localResource, configReader))
                .willReturn(
                        configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(
                configuration);
        given(localResourceProvider.start(testContext, localResource, configuration))
                .willReturn(
                        localResourceInstance);
        given(localResource.dataFiles()).willReturn(dataFilePatterns);
        given(fileSystemUtil.findClasspathFiles(dataFilePatterns)).willReturn(dataFiles);
        given(localResource.dataProvider()).willReturn(dataProviderType);
        given(reflectionUtil.newInstance(dataProviderType)).willReturn(dataProvider);
        given(localResourceInstance.getFqn()).willReturn(fqn);
        given(localResourceInstance.getProperties()).willReturn(properties);

        sut.start(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();
        verify(reflectionUtil).newInstance(value);
        verify(localResource).configKey();
        verify(testContext).getPropertiesReader(configKey);
        verify(localResourceProvider).configure(testContext, localResource, configReader);
        verify(testConfigurer).configure(testContext, configuration);
        verify(localResourceProvider).start(testContext, localResource, configuration);
        verify(localResource).dataFiles();
        verify(fileSystemUtil).findClasspathFiles(dataFilePatterns);
        verify(localResourceProvider).load(testContext, localResource,
                localResourceInstance,
                dataFiles);
        verify(localResource).dataProvider();
        verify(reflectionUtil).newInstance(dataProviderType);
        verify(dataProvider).load(testContext, dataFiles, localResourceInstance);
        verify(localResourceInstance).getFqn();
        verify(localResourceInstance).getProperties();
        verify(testContext).addProperty(fqn, properties);
        verify(testContext).addCollectionElement(eq(
                TestContextProperties.LOCAL_RESOURCE_INSTANCES),
                eq(resourceInstance));

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() throws
            Exception {
        TestContext testContext = mock(TestContext.class);
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceProvider localResourceProvider = mock(LocalResourceProvider.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        LocalResourceInfo resourceInstance = DefaultLocalResourceInfo.of(
                localResource,
                localResourceProvider,
                localResourceInstance);

        List<LocalResourceInfo> resourceInstances = ImmutableList.of(resourceInstance);

        given(testContext.getLocalResources()).willReturn(resourceInstances);

        sut.stop(testContext);

        verify(localResourceProvider).stop(testContext, localResource,
                localResourceInstance);
        verifyNoMoreInteractions(localResourceProvider);
    }

}
