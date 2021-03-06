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
import org.testifyproject.RemoteResourceInfo;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.RemoteResourceProvider;
import org.testifyproject.ResourceInfo;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.core.util.FileSystemUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.fixture.resource.TestDataProvider;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.trait.PropertiesReader;

/**
 *
 * @author saden
 */
public class DefaultRemoteResourceProviderTest {

    DefaultRemoteResourceProvider sut;
    ReflectionUtil reflectionUtil;
    FileSystemUtil fileSystemUtil;

    @Before
    public void init() {
        reflectionUtil = mock(ReflectionUtil.class);
        fileSystemUtil = mock(FileSystemUtil.class);

        sut = spy(new DefaultRemoteResourceProvider(reflectionUtil, fileSystemUtil));
    }

    @Test
    public void verifyDefaultConstructor() {
        DefaultRemoteResourceProvider result = new DefaultRemoteResourceProvider();

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextCallToStartShouldThrowException() {
        TestContext testContext = null;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        sut.start(testContext);
    }

    @Test
    public void callToStartWithoutRemoteResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<RemoteResource> virtualResources = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getRemoteResources()).willReturn(virtualResources);

        sut.start(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getRemoteResources();
        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithRemoteResourcesShouldStartResources() throws Exception {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RemoteResource remoteResource = mock(RemoteResource.class);
        List<RemoteResource> virtualResources = ImmutableList.of(remoteResource);
        RemoteResourceProvider remoteResourceProvider = mock(RemoteResourceProvider.class);
        Object configuration = mock(Object.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        RemoteResourceInstance<Object> remoteResourceInstance = mock(
                RemoteResourceInstance.class);
        String[] dataFilePatterns = {"test.class"};
        Set<Path> dataFiles = mock(Set.class);
        Class dataProviderType = TestDataProvider.class;
        DataProvider dataProvider = mock(TestDataProvider.class);
        String fqn = "fqn";
        Map<String, Object> properties = mock(Map.class);

        Class value = RemoteResourceProvider.class;
        String configKey = "test";
        PropertiesReader configReader = mock(PropertiesReader.class);

        ResourceInfo<RemoteResource, RemoteResourceProvider, RemoteResourceInstance> resourceInstance =
                DefaultRemoteResourceInfo.of(remoteResource, remoteResourceProvider,
                        remoteResourceInstance);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getRemoteResources()).willReturn(virtualResources);
        given(remoteResource.value()).willReturn(value);
        given(reflectionUtil.newInstance(value)).willReturn(remoteResourceProvider);
        given(remoteResource.configKey()).willReturn(configKey);
        given(testContext.getPropertiesReader(configKey)).willReturn(configReader);
        given(remoteResourceProvider.configure(testContext, remoteResource, configReader))
                .willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(
                configuration);
        given(remoteResourceProvider.start(testContext, remoteResource, configuration))
                .willReturn(
                        remoteResourceInstance);
        given(remoteResource.dataFiles()).willReturn(dataFilePatterns);
        given(fileSystemUtil.findClasspathFiles(dataFilePatterns)).willReturn(dataFiles);
        given(remoteResource.dataProvider()).willReturn(dataProviderType);
        given(reflectionUtil.newInstance(dataProviderType)).willReturn(dataProvider);
        given(remoteResourceInstance.getFqn()).willReturn(fqn);
        given(remoteResourceInstance.getProperties()).willReturn(properties);

        sut.start(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getRemoteResources();
        verify(remoteResource).value();
        verify(reflectionUtil).newInstance(value);
        verify(remoteResource).configKey();
        verify(testContext).getPropertiesReader(configKey);
        verify(remoteResourceProvider)
                .configure(testContext, remoteResource, configReader);
        verify(testConfigurer).configure(testContext, configuration);
        verify(remoteResourceProvider).start(testContext, remoteResource, configuration);
        verify(remoteResource).dataFiles();
        verify(fileSystemUtil).findClasspathFiles(dataFilePatterns);
        verify(remoteResourceProvider).load(testContext, remoteResource,
                remoteResourceInstance,
                dataFiles);
        verify(remoteResource).dataProvider();
        verify(reflectionUtil).newInstance(dataProviderType);
        verify(dataProvider).load(testContext, dataFiles, remoteResourceInstance);
        verify(remoteResourceInstance).getFqn();
        verify(remoteResourceInstance).getProperties();
        verify(testContext).addProperty(fqn, properties);
        verify(testContext)
                .addCollectionElement(eq(TestContextProperties.REMOTE_RESOURCE_INSTANCES),
                        eq(
                                resourceInstance));

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() throws
            Exception {
        TestContext testContext = mock(TestContext.class);
        RemoteResource remoteResource = mock(RemoteResource.class);
        RemoteResourceProvider remoteResourceProvider = mock(RemoteResourceProvider.class);
        RemoteResourceInstance remoteResourceInstance = mock(RemoteResourceInstance.class);
        RemoteResourceInfo resourceInstance =
                DefaultRemoteResourceInfo.of(
                        remoteResource,
                        remoteResourceProvider,
                        remoteResourceInstance);

        List<RemoteResourceInfo> resourceInstances = ImmutableList.of(resourceInstance);

        given(testContext.getRemoteResources()).willReturn(resourceInstances);

        sut.stop(testContext);

        verify(remoteResourceProvider).stop(testContext, remoteResource,
                remoteResourceInstance);
        verifyNoMoreInteractions(remoteResourceProvider);
    }

}
