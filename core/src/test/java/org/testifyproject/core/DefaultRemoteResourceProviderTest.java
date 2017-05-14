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

        sut = new DefaultRemoteResourceProvider(reflectionUtil, resourceProviders);
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
    public void callToStartWithNoConfigurationShouldStart() throws Exception {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RemoteResource remoteResource = mock(RemoteResource.class);
        List<RemoteResource> virtualResources = ImmutableList.of(remoteResource);
        Class resourceProviderType = RemoteResourceProvider.class;
        RemoteResourceProvider remoteResourceProvider = mock(RemoteResourceProvider.class);
        Object configuration = mock(Object.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        RemoteResourceInstance remoteResourceInstance = mock(RemoteResourceInstance.class);
        Instance<Object> clientInstance = mock(Instance.class);
        String clientName = "";
        Class clientContract = Class.class;
        String name = "";
        Class<RemoteResourceInstance> remoteResourceInstanceType = RemoteResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getRemoteResources()).willReturn(virtualResources);
        given(remoteResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(remoteResourceProvider);
        given(remoteResourceProvider.configure(testContext)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(remoteResourceProvider.start(testContext, remoteResource, configuration)).willReturn(remoteResourceInstance);
        given(remoteResourceInstance.getClient()).willReturn(clientInstance);
        given(remoteResource.clientName()).willReturn(clientName);
        given(remoteResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(remoteResourceInstance, null, remoteResourceInstanceType);
        given(remoteResource.name()).willReturn(name);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getRemoteResources();
        verify(remoteResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(serviceInstance).inject(remoteResourceProvider);
        verify(remoteResourceProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, configuration);
        verify(remoteResourceProvider).start(testContext, remoteResource, configuration);
        verify(remoteResourceInstance).getClient();
        verify(remoteResource).clientName();
        verify(remoteResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(remoteResource).name();
        verify(serviceInstance).addConstant(remoteResourceInstance, null, remoteResourceInstanceType);
        verify(resourceProviders).put(remoteResource, remoteResourceProvider);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithConfigurationShouldStart() throws Exception {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RemoteResource remoteResource = mock(RemoteResource.class);
        List<RemoteResource> virtualResources = ImmutableList.of(remoteResource);
        Class resourceProviderType = RemoteResourceProvider.class;
        RemoteResourceProvider remoteResourceProvider = mock(RemoteResourceProvider.class);
        Object configuration = mock(Object.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        RemoteResourceInstance remoteResourceInstance = mock(RemoteResourceInstance.class);
        Instance<Object> clientInstance = mock(Instance.class);
        String clientName = "clientName";
        Class clientContract = Object.class;
        String name = "resourceName";
        Class<RemoteResourceInstance> remoteResourceInstanceType = RemoteResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getRemoteResources()).willReturn(virtualResources);
        given(remoteResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(remoteResourceProvider);
        given(remoteResourceProvider.configure(testContext)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(remoteResourceProvider.start(testContext, remoteResource, configuration)).willReturn(remoteResourceInstance);
        given(remoteResourceInstance.getClient()).willReturn(clientInstance);
        given(remoteResource.clientName()).willReturn(clientName);
        given(remoteResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(remoteResourceInstance, name, remoteResourceInstanceType);
        given(remoteResource.name()).willReturn(name);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getRemoteResources();
        verify(remoteResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(serviceInstance).inject(remoteResourceProvider);
        verify(remoteResourceProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, configuration);
        verify(remoteResourceProvider).start(testContext, remoteResource, configuration);
        verify(remoteResourceInstance).getClient();
        verify(remoteResource).clientName();
        verify(remoteResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(remoteResource).name();
        verify(serviceInstance).addConstant(remoteResourceInstance, name, remoteResourceInstanceType);
        verify(resourceProviders).put(remoteResource, remoteResourceProvider);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
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
