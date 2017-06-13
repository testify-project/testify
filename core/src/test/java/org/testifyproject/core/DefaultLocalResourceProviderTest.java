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
import static org.mockito.Mockito.spy;
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
import org.testifyproject.trait.PropertiesReader;

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

        sut = spy(new DefaultLocalResourceProvider(reflectionUtil, resourceProviders));
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
        String fqn = "fqn";
        Map<String, Object> properties = mock(Map.class);

        Class value = LocalResourceProvider.class;
        String configKey = "test";
        PropertiesReader configReader = mock(PropertiesReader.class);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);
        given(localResource.value()).willReturn(value);
        given(reflectionUtil.newInstance(value)).willReturn(localResourceProvider);
        given(localResource.configKey()).willReturn(configKey);
        given(testContext.getPropertiesReader(configKey)).willReturn(configReader);
        given(localResourceProvider.configure(testContext, localResource, configReader)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(localResourceProvider.start(testContext, localResource, configuration)).willReturn(localResourceInstance);
        given(localResourceInstance.getFqn()).willReturn(fqn);
        given(localResourceInstance.getProperties()).willReturn(properties);
        willDoNothing().given(sut).processInstance(localResource, localResourceInstance, value, serviceInstance);

        sut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();
        verify(reflectionUtil).newInstance(value);
        verify(serviceInstance).inject(localResourceProvider);
        verify(localResource).configKey();
        verify(testContext).getPropertiesReader(configKey);
        verify(localResourceProvider).configure(testContext, localResource, configReader);
        verify(testConfigurer).configure(testContext, configuration);
        verify(localResourceProvider).start(testContext, localResource, configuration);
        verify(localResourceInstance).getFqn();
        verify(localResourceInstance).getProperties();
        verify(testContext).addProperty(fqn, properties);
        verify(resourceProviders).put(localResource, localResourceProvider);
        verify(sut).processInstance(localResource, localResourceInstance, value, serviceInstance);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToProcessIntanceWithNoConfigurationShouldStart() throws Exception {
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        Class value = LocalResourceProvider.class;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String name = "";
        Class<LocalResourceInstance> resourceInstanceContract = LocalResourceInstance.class;
        String resourceInstanceName = "resource://" + value.getSimpleName();

        given(localResource.name()).willReturn(name);
        willDoNothing().given(sut).processResource(resourceInstanceName, localResource, localResourceInstance, serviceInstance);
        willDoNothing().given(sut).processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        sut.processInstance(localResource, localResourceInstance, value, serviceInstance);

        verify(localResource).name();
        verify(serviceInstance).addConstant(localResourceInstance, resourceInstanceName, resourceInstanceContract);
        verify(sut).processResource(resourceInstanceName, localResource, localResourceInstance, serviceInstance);
        verify(sut).processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        verifyNoMoreInteractions(localResource, localResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessInstanceWithConfigurationShouldStart() throws Exception {
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        Class value = LocalResourceProvider.class;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String name = "name";
        Class<LocalResourceInstance> resourceInstanceContract = LocalResourceInstance.class;
        String resourceInstanceName = "resource://" + name;

        given(localResource.name()).willReturn(name);
        willDoNothing().given(sut).processResource(resourceInstanceName, localResource, localResourceInstance, serviceInstance);
        willDoNothing().given(sut).processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        sut.processInstance(localResource, localResourceInstance, value, serviceInstance);

        verify(localResource).name();
        verify(serviceInstance).addConstant(localResourceInstance, resourceInstanceName, resourceInstanceContract);
        verify(sut).processResource(resourceInstanceName, localResource, localResourceInstance, serviceInstance);
        verify(sut).processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        verifyNoMoreInteractions(localResource, localResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessResourceWithNoConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String localResourceName = "";
        String resourceName = resourceInstanceName + "/resource";
        Class resourceContract = Object.class;
        Instance<Object> resourceInstance = mock(Instance.class);

        given(localResource.resourceName()).willReturn(localResourceName);
        given(localResource.resourceContract()).willReturn(resourceContract);
        given(localResourceInstance.getResource()).willReturn(resourceInstance);

        sut.processResource(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        verify(localResourceInstance).getResource();
        verify(localResource).resourceName();
        verify(localResource).resourceContract();
        verify(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        verifyNoMoreInteractions(localResource, localResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessResourceWithConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        String localResourceName = "localResource";
        String resourceName = resourceInstanceName + "/" + localResourceName;
        Class resourceContract = Object.class;
        Instance<Object> resourceInstance = mock(Instance.class);

        given(localResource.resourceName()).willReturn(localResourceName);
        given(localResource.resourceContract()).willReturn(resourceContract);
        given(localResourceInstance.getResource()).willReturn(resourceInstance);

        sut.processResource(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        verify(localResourceInstance).getResource();
        verify(localResource).resourceName();
        verify(localResource).resourceContract();
        verify(serviceInstance).replace(resourceInstance, resourceName, resourceContract);

        verifyNoMoreInteractions(localResource, localResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessClientWithNoClientShouldDoNothing() throws Exception {
        String resourceInstanceName = "resource://test";
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        Optional<Instance> foundClient = Optional.empty();

        given(localResourceInstance.getClient()).willReturn(foundClient);

        sut.processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        verify(localResourceInstance).getClient();

        verifyNoMoreInteractions(localResource, localResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessClientWithNoConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance<Object>> foundClient = Optional.of(clientInstance);

        String localClientName = "";
        String clientName = resourceInstanceName + "/client";
        Class clientContract = Object.class;

        given(localResourceInstance.getClient()).willReturn(foundClient);
        given(localResource.clientName()).willReturn(localClientName);
        given(localResource.clientContract()).willReturn(clientContract);

        sut.processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        verify(localResourceInstance).getClient();
        verify(localResource).clientName();
        verify(localResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);

        verifyNoMoreInteractions(localResource, localResourceInstance, serviceInstance);
    }

    @Test
    public void callToProcessClientWithConfigurationShouldStart() throws Exception {
        String resourceInstanceName = "resource://test";
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceInstance<Object, Object> localResourceInstance = mock(LocalResourceInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance<Object>> foundClient = Optional.of(clientInstance);

        String localClientName = "clientName";
        String clientName = resourceInstanceName + "/" + localClientName;
        Class clientContract = Object.class;

        given(localResourceInstance.getClient()).willReturn(foundClient);
        given(localResource.clientName()).willReturn(localClientName);
        given(localResource.clientContract()).willReturn(clientContract);

        sut.processClient(resourceInstanceName, localResource, localResourceInstance, serviceInstance);

        verify(localResourceInstance).getClient();
        verify(localResource).clientName();
        verify(localResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);

        verifyNoMoreInteractions(localResource, localResourceInstance, serviceInstance);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() throws Exception {
        TestContext testContext = mock(TestContext.class);
        LocalResource localResource = mock(LocalResource.class);
        LocalResourceProvider localResourceProvider = mock(LocalResourceProvider.class);
        resourceProviders.put(localResource, localResourceProvider);

        sut.stop(testContext);

        verify(localResourceProvider).stop(testContext, localResource);
        verifyNoMoreInteractions(localResourceProvider);
    }

}
