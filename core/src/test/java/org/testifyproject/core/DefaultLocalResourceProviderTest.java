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
import java.util.Optional;
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
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultLocalResourceProviderTest {

    DefaultLocalResourceProvider sut;
    ReflectionUtil reflectionUtil;
    Map<LocalResource, LocalResourceProvider> resourceProviders;

    @Before
    public void init() {
        reflectionUtil = mock(ReflectionUtil.class);
        resourceProviders = mock(Map.class, delegatesTo(new LinkedHashMap<>()));

        sut = new DefaultLocalResourceProvider(reflectionUtil, resourceProviders);
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

        sut.start(testContext, serviceInstance);
    }

    @Test
    public void callToStartWithoutLocalResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<LocalResource> virtualResources = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getLocalResources();
        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithNoConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        LocalResource localResource = mock(LocalResource.class);
        List<LocalResource> virtualResources = ImmutableList.of(localResource);
        Class resourceProviderType = LocalResourceProvider.class;
        LocalResourceProvider localResourceProvider = mock(LocalResourceProvider.class);
        Object configuration = mock(Object.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        Instance<Object> serverInstance = mock(Instance.class);
        String resourceName = "";
        Class resourceContract = Class.class;
        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance> clientInstanceResult = Optional.of(clientInstance);
        String clientName = "";
        Class clientContract = Class.class;
        String name = "";
        Class<LocalResourceInstance> localResourceInstanceType = LocalResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);
        given(localResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(localResourceProvider);
        given(localResourceProvider.configure(testContext)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(localResourceProvider.start(testContext, localResource, configuration)).willReturn(localResourceInstance);
        given(localResourceInstance.getResource()).willReturn(serverInstance);
        given(localResource.resourceName()).willReturn(resourceName);
        given(localResource.resourceContract()).willReturn(resourceContract);
        willDoNothing().given(serviceInstance).replace(serverInstance, resourceName, resourceContract);
        given(localResourceInstance.getClient()).willReturn(clientInstanceResult);
        given(localResource.clientName()).willReturn(clientName);
        given(localResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(localResourceInstance, null, localResourceInstanceType);
        given(localResource.name()).willReturn(name);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(serviceInstance).inject(localResourceProvider);
        verify(localResourceProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, configuration);
        verify(localResourceProvider).start(testContext, localResource, configuration);
        verify(localResourceInstance).getResource();
        verify(localResource).resourceName();
        verify(serviceInstance).replace(serverInstance, resourceName, resourceContract);
        verify(localResourceInstance).getClient();
        verify(localResource).clientName();
        verify(localResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(localResource).name();
        verify(serviceInstance).addConstant(localResourceInstance, null, localResourceInstanceType);
        verify(resourceProviders).put(localResource, localResourceProvider);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        LocalResource localResource = mock(LocalResource.class);
        List<LocalResource> virtualResources = ImmutableList.of(localResource);
        Class resourceProviderType = LocalResourceProvider.class;
        LocalResourceProvider localResourceProvider = mock(LocalResourceProvider.class);
        Object configuration = mock(Object.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        Instance<Object> serverInstance = mock(Instance.class);
        String resourceName = "resourceName";
        Class resourceContract = Object.class;
        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance> clientInstanceResult = Optional.of(clientInstance);
        String clientName = "clientName";
        Class clientContract = Object.class;
        String name = "resourceName";
        Class<LocalResourceInstance> localResourceInstanceType = LocalResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);
        given(localResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(localResourceProvider);
        given(localResourceProvider.configure(testContext)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(localResourceProvider.start(testContext, localResource, configuration)).willReturn(localResourceInstance);
        given(localResourceInstance.getResource()).willReturn(serverInstance);
        given(localResource.resourceName()).willReturn(resourceName);
        given(localResource.resourceContract()).willReturn(resourceContract);
        willDoNothing().given(serviceInstance).replace(serverInstance, resourceName, resourceContract);
        given(localResourceInstance.getClient()).willReturn(clientInstanceResult);
        given(localResource.clientName()).willReturn(clientName);
        given(localResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(localResourceInstance, name, localResourceInstanceType);
        given(localResource.name()).willReturn(name);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(serviceInstance).inject(localResourceProvider);
        verify(localResourceProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, configuration);
        verify(localResourceProvider).start(testContext, localResource, configuration);
        verify(localResourceInstance).getResource();
        verify(localResource).resourceName();
        verify(localResource).resourceContract();
        verify(serviceInstance).replace(serverInstance, resourceName, resourceContract);
        verify(localResourceInstance).getClient();
        verify(localResource).clientName();
        verify(localResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(localResource).name();
        verify(serviceInstance).addConstant(localResourceInstance, name, localResourceInstanceType);
        verify(resourceProviders).put(localResource, localResourceProvider);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() {
        TestContext testContext = mock(TestContext.class);
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceProvider localResourceProvider = mock(LocalResourceProvider.class);
        resourceProviders.put(localResource, localResourceProvider);

        sut.stop(testContext);

        verify(localResourceProvider).stop(testContext, localResource);
        verifyNoMoreInteractions(localResourceProvider);
    }

}
