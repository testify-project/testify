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
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.fixture.resource.ValidVirtualResourceProvider;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.trait.PropertiesReader;

/**
 *
 * @author saden
 */
public class DefaultVirtualResourceProviderTest {

    DefaultVirtualResourceProvider sut;
    ServiceLocatorUtil serviceLocatorUtil;
    ReflectionUtil reflectionUtil;
    Map<VirtualResource, VirtualResourceProvider> virtualResourceProviders;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        reflectionUtil = mock(ReflectionUtil.class);
        virtualResourceProviders = mock(Map.class, delegatesTo(new LinkedHashMap<>()));

        sut = spy(new DefaultVirtualResourceProvider(serviceLocatorUtil, reflectionUtil, virtualResourceProviders));
    }

    @Test
    public void verifyDefaultConstructor() {
        DefaultVirtualResourceProvider result = new DefaultVirtualResourceProvider();

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextCallToStartShouldThrowException() {
        TestContext testContext = null;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        sut.start(testContext, serviceInstance);
    }

    @Test
    public void callToStartWithoutVirtualResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<VirtualResource> virtualResources = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getVirtualResources();
        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithDefaultProviderShouldStartResources() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);

        VirtualResource virtualResource = mock(VirtualResource.class);
        List<VirtualResource> virtualResources = ImmutableList.of(virtualResource);

        Class provider = VirtualResourceProvider.class;
        VirtualResourceProvider virtualResourceProvider = mock(VirtualResourceProvider.class);
        Object configuration = mock(Object.class);
        VirtualResourceInstance<Object> virtualResourceInstance = mock(VirtualResourceInstance.class);
        String fqn = "fqn";
        Map<String, Object> properties = mock(Map.class);

        String configKey = "test";
        PropertiesReader configReader = mock(PropertiesReader.class);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(virtualResource.provider()).willReturn(provider);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(serviceLocatorUtil.getOne(provider)).willReturn(virtualResourceProvider);
        given(virtualResource.configKey()).willReturn(configKey);
        given(testContext.getPropertiesReader(configKey)).willReturn(configReader);
        given(virtualResourceProvider.configure(testContext, virtualResource, configReader)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(virtualResourceProvider.start(testContext, virtualResource, configuration)).willReturn(virtualResourceInstance);
        given(virtualResourceInstance.getFqn()).willReturn(fqn);
        given(virtualResourceInstance.getProperties()).willReturn(properties);
        willDoNothing().given(sut).processInstance(virtualResource, virtualResourceInstance, serviceInstance);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getVirtualResources();
        verify(virtualResource).provider();
        verify(serviceLocatorUtil).getOne(provider);
        verify(serviceInstance).inject(virtualResourceProvider);
        verify(virtualResource).configKey();
        verify(testContext).getPropertiesReader(configKey);
        verify(virtualResourceProvider).configure(testContext, virtualResource, configReader);
        verify(testConfigurer).configure(testContext, configuration);
        verify(virtualResourceProvider).start(testContext, virtualResource, configuration);
        verify(virtualResourceInstance).getFqn();
        verify(virtualResourceInstance).getProperties();
        verify(testContext).addProperty(fqn, properties);
        verify(sut).processInstance(virtualResource, virtualResourceInstance, serviceInstance);

        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithCustomProviderShouldStartResources() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);

        VirtualResource virtualResource = mock(VirtualResource.class);
        List<VirtualResource> virtualResources = ImmutableList.of(virtualResource);

        Class provider = ValidVirtualResourceProvider.class;
        VirtualResourceProvider virtualResourceProvider = mock(VirtualResourceProvider.class);
        Object configuration = mock(Object.class);
        VirtualResourceInstance<Object> virtualResourceInstance = mock(VirtualResourceInstance.class);
        String fqn = "fqn";
        Map<String, Object> properties = mock(Map.class);

        String configKey = "test";
        PropertiesReader configReader = mock(PropertiesReader.class);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(virtualResource.provider()).willReturn(provider);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(reflectionUtil.newInstance(provider)).willReturn(virtualResourceProvider);
        given(virtualResource.configKey()).willReturn(configKey);
        given(testContext.getPropertiesReader(configKey)).willReturn(configReader);
        given(virtualResourceProvider.configure(testContext, virtualResource, configReader)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(virtualResourceProvider.start(testContext, virtualResource, configuration)).willReturn(virtualResourceInstance);
        given(virtualResourceInstance.getFqn()).willReturn(fqn);
        given(virtualResourceInstance.getProperties()).willReturn(properties);
        willDoNothing().given(sut).processInstance(virtualResource, virtualResourceInstance, serviceInstance);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getVirtualResources();
        verify(virtualResource).provider();
        verify(reflectionUtil).newInstance(provider);
        verify(serviceInstance).inject(virtualResourceProvider);
        verify(virtualResource).configKey();
        verify(testContext).getPropertiesReader(configKey);
        verify(virtualResourceProvider).configure(testContext, virtualResource, configReader);
        verify(testConfigurer).configure(testContext, configuration);
        verify(virtualResourceProvider).start(testContext, virtualResource, configuration);
        verify(virtualResourceInstance).getFqn();
        verify(virtualResourceInstance).getProperties();
        verify(testContext).addProperty(fqn, properties);
        verify(sut).processInstance(virtualResource, virtualResourceInstance, serviceInstance);

        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor, serviceInstance);
    }

    @Test
    public void callToProcessIntanceWithNoConfigurationShouldStart() throws Exception {
        VirtualResource virtualResource = mock(VirtualResource.class);
        VirtualResourceInstance<Object> virtualResourceInstance = mock(VirtualResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String value = "value";
        String name = "";
        Class<VirtualResourceInstance> resourceInstanceContract = VirtualResourceInstance.class;
        String resourceInstanceName = "resource://" + value;

        given(virtualResource.value()).willReturn(value);
        given(virtualResource.name()).willReturn(name);
        willDoNothing().given(serviceInstance).addConstant(virtualResourceInstance, resourceInstanceName, resourceInstanceContract);
        willDoNothing().given(sut).processResource(resourceInstanceName, virtualResource, virtualResourceInstance, serviceInstance);

        sut.processInstance(virtualResource, virtualResourceInstance, serviceInstance);

        verify(virtualResource).value();
        verify(virtualResource).name();
        verify(serviceInstance).addConstant(virtualResourceInstance, resourceInstanceName, resourceInstanceContract);
        verify(sut).processResource(resourceInstanceName, virtualResource, virtualResourceInstance, serviceInstance);

        verifyNoMoreInteractions(virtualResource, virtualResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessInstanceWithConfigurationShouldStart() throws Exception {
        VirtualResource virtualResource = mock(VirtualResource.class);
        VirtualResourceInstance<Object> virtualResourceInstance = mock(VirtualResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String value = "value";
        String name = "name";
        Class<VirtualResourceInstance> resourceInstanceContract = VirtualResourceInstance.class;
        String resourceInstanceName = "resource://" + name;

        given(virtualResource.value()).willReturn(value);
        given(virtualResource.name()).willReturn(name);
        willDoNothing().given(serviceInstance).addConstant(virtualResourceInstance, resourceInstanceName, resourceInstanceContract);
        willDoNothing().given(sut).processResource(resourceInstanceName, virtualResource, virtualResourceInstance, serviceInstance);

        sut.processInstance(virtualResource, virtualResourceInstance, serviceInstance);

        verify(virtualResource).value();
        verify(virtualResource).name();
        verify(serviceInstance).addConstant(virtualResourceInstance, resourceInstanceName, resourceInstanceContract);
        verify(sut).processResource(resourceInstanceName, virtualResource, virtualResourceInstance, serviceInstance);

        verifyNoMoreInteractions(virtualResource, virtualResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessResourceWithNoConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        VirtualResource virtualResource = mock(VirtualResource.class);
        VirtualResourceInstance<Object> virtualResourceInstance = mock(VirtualResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String virtualResourceName = "";
        String resourceName = resourceInstanceName + "/resource";
        Class resourceContract = Class.class;
        Instance<Object> resourceInstance = mock(Instance.class);

        given(virtualResource.resourceName()).willReturn(virtualResourceName);
        given(virtualResource.resourceContract()).willReturn(resourceContract);
        given(virtualResourceInstance.getResource()).willReturn(resourceInstance);
        willDoNothing().given(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        sut.processResource(resourceInstanceName, virtualResource, virtualResourceInstance, serviceInstance);

        verify(virtualResourceInstance).getResource();
        verify(virtualResource).resourceName();
        verify(virtualResource).resourceContract();
        verify(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        verifyNoMoreInteractions(virtualResource, virtualResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessResourceWithConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        VirtualResource virtualResource = mock(VirtualResource.class);
        VirtualResourceInstance<Object> virtualResourceInstance = mock(VirtualResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String virtualResourceName = "virtualResource";
        String resourceName = resourceInstanceName + "/" + virtualResourceName;
        Class resourceContract = Class.class;
        Instance<Object> resourceInstance = mock(Instance.class);

        given(virtualResource.resourceName()).willReturn(virtualResourceName);
        given(virtualResource.resourceContract()).willReturn(resourceContract);
        given(virtualResourceInstance.getResource()).willReturn(resourceInstance);
        willDoNothing().given(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        sut.processResource(resourceInstanceName, virtualResource, virtualResourceInstance, serviceInstance);

        verify(virtualResourceInstance).getResource();
        verify(virtualResource).resourceName();
        verify(virtualResource).resourceContract();
        verify(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        verifyNoMoreInteractions(virtualResource, virtualResourceInstance, serviceInstance);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() {
        TestContext testContext = mock(TestContext.class);
        VirtualResource virtualResource = mock(VirtualResource.class);
        VirtualResourceProvider virtualResourceProvider = mock(VirtualResourceProvider.class);
        virtualResourceProviders.put(virtualResource, virtualResourceProvider);

        sut.stop(testContext);

        verify(virtualResourceProvider).stop(testContext, virtualResource);
        verifyNoMoreInteractions(virtualResourceProvider);
    }

}
