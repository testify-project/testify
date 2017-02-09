/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.core;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testify.Instance;
import org.testify.ResourceInstance;
import org.testify.ResourceProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.annotation.RequiresResource;
import org.testify.core.util.ReflectionUtil;
import org.testify.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultRequiresResourceProviderTest {

    DefaultRequiresResourceProvider cut;
    ReflectionUtil reflectionUtil;
    Queue<ResourceProvider> resourceProviders;

    @Before
    public void init() {
        reflectionUtil = mock(ReflectionUtil.class);
        resourceProviders = mock(Queue.class, delegatesTo(new ConcurrentLinkedQueue()));

        cut = new DefaultRequiresResourceProvider(reflectionUtil, resourceProviders);
    }

    @Test
    public void verifyDefaultConstructor() {
        DefaultRequiresResourceProvider result = new DefaultRequiresResourceProvider();

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextCallToStartShouldThrowException() {
        TestContext testContext = null;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        cut.start(testContext, serviceInstance);
    }

    @Test
    public void callToStartWithoutRequiresResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<RequiresResource> requiresContainers = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getRequiresResources()).willReturn(requiresContainers);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getRequiresResources();
        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithNoConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RequiresResource requiresResource = mock(RequiresResource.class);
        List<RequiresResource> requiresContainers = ImmutableList.of(requiresResource);
        Class resourceProviderType = ResourceProvider.class;
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        Object configuration = mock(Object.class);
        TestReifier testReifier = mock(TestReifier.class);
        ResourceInstance resourceInstance = mock(ResourceInstance.class);
        Instance<Object> serverInstance = mock(Instance.class);
        String serverName = "";
        Class serverContract = Class.class;
        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance> clientInstanceResult = Optional.of(clientInstance);
        String clientName = "";
        Class clientContract = Class.class;
        String resourceName = "";
        Class<ResourceInstance> resourceContract = ResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(testDescriptor.getRequiresResources()).willReturn(requiresContainers);
        given(requiresResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(resourceProvider);
        given(resourceProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(resourceProvider.start(testContext, configuration)).willReturn(resourceInstance);
        given(resourceInstance.getServer()).willReturn(serverInstance);
        given(requiresResource.serverName()).willReturn(serverName);
        given(requiresResource.serverContract()).willReturn(serverContract);
        willDoNothing().given(serviceInstance).replace(serverInstance, serverName, serverContract);
        given(resourceInstance.getClient()).willReturn(clientInstanceResult);
        given(requiresResource.clientName()).willReturn(clientName);
        given(requiresResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(resourceInstance, null, resourceContract);
        given(requiresResource.name()).willReturn(resourceName);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getRequiresResources();
        verify(requiresResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(resourceProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(resourceProvider).start(testContext, configuration);
        verify(resourceInstance).getServer();
        verify(requiresResource).serverName();
        verify(requiresResource).serverContract();
        verify(serviceInstance).replace(serverInstance, serverName, serverContract);
        verify(resourceInstance).getClient();
        verify(requiresResource).clientName();
        verify(requiresResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(requiresResource).name();
        verify(serviceInstance).addConstant(resourceInstance, null, resourceContract);
        verify(resourceProviders).add(resourceProvider);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RequiresResource requiresResource = mock(RequiresResource.class);
        List<RequiresResource> requiresContainers = ImmutableList.of(requiresResource);
        Class resourceProviderType = ResourceProvider.class;
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        Object configuration = mock(Object.class);
        TestReifier testReifier = mock(TestReifier.class);
        ResourceInstance resourceInstance = mock(ResourceInstance.class);
        Instance<Object> serverInstance = mock(Instance.class);
        String serverName = "serverName";
        Class serverContract = Object.class;
        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance> clientInstanceResult = Optional.of(clientInstance);
        String clientName = "clientName";
        Class clientContract = Object.class;
        String resourceName = "resourceName";
        Class<ResourceInstance> resourceContract = ResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(testDescriptor.getRequiresResources()).willReturn(requiresContainers);
        given(requiresResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(resourceProvider);
        given(resourceProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(resourceProvider.start(testContext, configuration)).willReturn(resourceInstance);
        given(resourceInstance.getServer()).willReturn(serverInstance);
        given(requiresResource.serverName()).willReturn(serverName);
        given(requiresResource.serverContract()).willReturn(serverContract);
        willDoNothing().given(serviceInstance).replace(serverInstance, serverName, serverContract);
        given(resourceInstance.getClient()).willReturn(clientInstanceResult);
        given(requiresResource.clientName()).willReturn(clientName);
        given(requiresResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(resourceInstance, resourceName, resourceContract);
        given(requiresResource.name()).willReturn(resourceName);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getRequiresResources();
        verify(requiresResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(resourceProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(resourceProvider).start(testContext, configuration);
        verify(resourceInstance).getServer();
        verify(requiresResource).serverName();
        verify(requiresResource).serverContract();
        verify(serviceInstance).replace(serverInstance, serverName, serverContract);
        verify(resourceInstance).getClient();
        verify(requiresResource).clientName();
        verify(requiresResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(requiresResource).name();
        verify(serviceInstance).addConstant(resourceInstance, resourceName, resourceContract);
        verify(resourceProviders).add(resourceProvider);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStopWithNoElementsStopShouldStopContainerProvider() {
        cut.stop();

        verify(resourceProviders).parallelStream();
        verifyNoMoreInteractions(resourceProviders);
    }

    @Test
    public void callToStopWithElementsStopShouldStopContainerProvider() {
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        resourceProviders.add(resourceProvider);

        cut.stop();

        verify(resourceProviders).parallelStream();
        verify(resourceProvider).stop();
        verifyNoMoreInteractions(resourceProvider);
    }

}
