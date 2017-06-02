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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.RemoteResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultRemoteResourceProviderTest {

    DefaultRemoteResourceProvider sut;
    ReflectionUtil reflectionUtil;
    Map<RemoteResource, RemoteResourceProvider> resourceProviders;

    @Before
    public void init() {
        reflectionUtil = mock(ReflectionUtil.class);
        resourceProviders = mock(Map.class, delegatesTo(new LinkedHashMap<>()));

        sut = spy(new DefaultRemoteResourceProvider(reflectionUtil, resourceProviders));
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

        sut.start(testContext, serviceInstance);
    }

    @Test
    public void callToStartWithoutRemoteResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<RemoteResource> virtualResources = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getRemoteResources()).willReturn(virtualResources);

        sut.start(testContext, serviceInstance);

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
        RemoteResourceInstance<Object> remoteResourceInstance = mock(RemoteResourceInstance.class);

        Class value = RemoteResourceProvider.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getRemoteResources()).willReturn(virtualResources);
        given(remoteResource.value()).willReturn(value);
        given(reflectionUtil.newInstance(value)).willReturn(remoteResourceProvider);
        given(remoteResourceProvider.configure(testContext)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(remoteResourceProvider.start(testContext, remoteResource, configuration)).willReturn(remoteResourceInstance);
        willDoNothing().given(sut).processInstance(remoteResource, remoteResourceInstance, value, serviceInstance);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getRemoteResources();
        verify(remoteResource).value();
        verify(reflectionUtil).newInstance(value);
        verify(serviceInstance).inject(remoteResourceProvider);
        verify(remoteResourceProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, configuration);
        verify(remoteResourceProvider).start(testContext, remoteResource, configuration);
        verify(resourceProviders).put(remoteResource, remoteResourceProvider);
        verify(sut).processInstance(remoteResource, remoteResourceInstance, value, serviceInstance);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToProcessIntanceWithNoConfigurationShouldStart() throws Exception {
        RemoteResource remoteResource = mock(RemoteResource.class);
        RemoteResourceInstance remoteResourceInstance = mock(RemoteResourceInstance.class);
        Class value = RemoteResourceProvider.class;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String name = "";
        Class<RemoteResourceInstance> resourceInstanceContract = RemoteResourceInstance.class;
        String resourceInstanceName = "resource://" + value.getSimpleName();

        given(remoteResource.name()).willReturn(name);
        willDoNothing().given(serviceInstance).addConstant(remoteResourceInstance, resourceInstanceName, resourceInstanceContract);
        willDoNothing().given(sut).processResource(resourceInstanceName, remoteResource, remoteResourceInstance, serviceInstance);

        sut.processInstance(remoteResource, remoteResourceInstance, value, serviceInstance);

        verify(remoteResource).name();
        verify(serviceInstance).addConstant(remoteResourceInstance, resourceInstanceName, resourceInstanceContract);
        verify(sut).processResource(resourceInstanceName, remoteResource, remoteResourceInstance, serviceInstance);

        verifyNoMoreInteractions(remoteResource, remoteResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessInstanceWithConfigurationShouldStart() throws Exception {
        RemoteResource remoteResource = mock(RemoteResource.class);
        RemoteResourceInstance remoteResourceInstance = mock(RemoteResourceInstance.class);
        Class value = RemoteResourceProvider.class;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String name = "name";
        Class<RemoteResourceInstance> resourceInstanceContract = RemoteResourceInstance.class;
        String resourceInstanceName = "resource://" + name;

        given(remoteResource.name()).willReturn(name);
        willDoNothing().given(serviceInstance).addConstant(remoteResourceInstance, resourceInstanceName, resourceInstanceContract);
        willDoNothing().given(sut).processResource(resourceInstanceName, remoteResource, remoteResourceInstance, serviceInstance);

        sut.processInstance(remoteResource, remoteResourceInstance, value, serviceInstance);

        verify(remoteResource).name();
        verify(serviceInstance).addConstant(remoteResourceInstance, resourceInstanceName, resourceInstanceContract);
        verify(sut).processResource(resourceInstanceName, remoteResource, remoteResourceInstance, serviceInstance);

        verifyNoMoreInteractions(remoteResource, remoteResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessResourceWithNoConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        RemoteResource remoteResource = mock(RemoteResource.class);
        RemoteResourceInstance remoteResourceInstance = mock(RemoteResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String remoteResourceName = "";
        String resourceName = resourceInstanceName + "/resource";
        Class resourceContract = Class.class;
        Instance<Object> resourceInstance = mock(Instance.class);

        given(remoteResource.resourceName()).willReturn(remoteResourceName);
        given(remoteResource.resourceContract()).willReturn(resourceContract);
        given(remoteResourceInstance.getResource()).willReturn(resourceInstance);
        willDoNothing().given(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        sut.processResource(resourceInstanceName, remoteResource, remoteResourceInstance, serviceInstance);

        verify(remoteResourceInstance).getResource();
        verify(remoteResource).resourceName();
        verify(remoteResource).resourceContract();
        verify(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        verifyNoMoreInteractions(remoteResource, remoteResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessResourceWithConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        RemoteResource remoteResource = mock(RemoteResource.class);
        RemoteResourceInstance remoteResourceInstance = mock(RemoteResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String remoteResourceName = "remoteResource";
        String resourceName = resourceInstanceName + "/" + remoteResourceName;
        Class resourceContract = Class.class;
        Instance<Object> resourceInstance = mock(Instance.class);

        given(remoteResource.resourceName()).willReturn(remoteResourceName);
        given(remoteResource.resourceContract()).willReturn(resourceContract);
        given(remoteResourceInstance.getResource()).willReturn(resourceInstance);
        willDoNothing().given(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        sut.processResource(resourceInstanceName, remoteResource, remoteResourceInstance, serviceInstance);

        verify(remoteResourceInstance).getResource();
        verify(remoteResource).resourceName();
        verify(remoteResource).resourceContract();
        verify(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        verifyNoMoreInteractions(remoteResource, remoteResourceInstance, serviceInstance);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() throws Exception {
        TestContext testContext = mock(TestContext.class);
        RemoteResource remoteResource = mock(RemoteResource.class);
        RemoteResourceProvider remoteResourceProvider = mock(RemoteResourceProvider.class);
        resourceProviders.put(remoteResource, remoteResourceProvider);

        sut.stop(testContext);

        verify(remoteResourceProvider).stop(testContext, remoteResource);
        verifyNoMoreInteractions(remoteResourceProvider);
    }

}
