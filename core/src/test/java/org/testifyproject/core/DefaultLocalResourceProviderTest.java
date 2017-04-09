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
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultLocalResourceProviderTest {

    DefaultLocalResourceProvider cut;
    ReflectionUtil reflectionUtil;
    Queue<LocalResourceProvider> resourceProviders;

    @Before
    public void init() {
        reflectionUtil = mock(ReflectionUtil.class);
        resourceProviders = mock(Queue.class, delegatesTo(new ConcurrentLinkedQueue()));

        cut = new DefaultLocalResourceProvider(reflectionUtil, resourceProviders);
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

        cut.start(testContext, serviceInstance);
    }

    @Test
    public void callToStartWithoutLocalResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<LocalResource> virtualResources = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
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
        LocalResourceProvider resourceProvider = mock(LocalResourceProvider.class);
        Object configuration = mock(Object.class);
        TestReifier testReifier = mock(TestReifier.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        Instance<Object> serverInstance = mock(Instance.class);
        String serverName = "";
        Class serverContract = Class.class;
        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance> clientInstanceResult = Optional.of(clientInstance);
        String clientName = "";
        Class clientContract = Class.class;
        String resourceName = "";
        Class<LocalResourceInstance> resourceContract = LocalResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);
        given(localResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(resourceProvider);
        given(resourceProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(resourceProvider.start(testContext, configuration)).willReturn(localResourceInstance);
        given(localResourceInstance.getResource()).willReturn(serverInstance);
        given(localResource.resourceName()).willReturn(serverName);
        given(localResource.resourceContract()).willReturn(serverContract);
        willDoNothing().given(serviceInstance).replace(serverInstance, serverName, serverContract);
        given(localResourceInstance.getClient()).willReturn(clientInstanceResult);
        given(localResource.clientName()).willReturn(clientName);
        given(localResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(localResourceInstance, null, resourceContract);
        given(localResource.name()).willReturn(resourceName);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(serviceInstance).inject(resourceProvider);
        verify(resourceProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(resourceProvider).start(testContext, configuration);
        verify(localResourceInstance).getResource();
        verify(localResource).resourceName();
        verify(localResource).resourceName();
        verify(serviceInstance).replace(serverInstance, serverName, serverContract);
        verify(localResourceInstance).getClient();
        verify(localResource).clientName();
        verify(localResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(localResource).name();
        verify(serviceInstance).addConstant(localResourceInstance, null, resourceContract);
        verify(resourceProviders).add(resourceProvider);

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
        LocalResourceProvider resourceProvider = mock(LocalResourceProvider.class);
        Object configuration = mock(Object.class);
        TestReifier testReifier = mock(TestReifier.class);
        LocalResourceInstance localResourceInstance = mock(LocalResourceInstance.class);
        Instance<Object> serverInstance = mock(Instance.class);
        String serverName = "serverName";
        Class serverContract = Object.class;
        Instance<Object> clientInstance = mock(Instance.class);
        Optional<Instance> clientInstanceResult = Optional.of(clientInstance);
        String clientName = "clientName";
        Class clientContract = Object.class;
        String resourceName = "resourceName";
        Class<LocalResourceInstance> resourceContract = LocalResourceInstance.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(testDescriptor.getLocalResources()).willReturn(virtualResources);
        given(localResource.value()).willReturn(resourceProviderType);
        given(reflectionUtil.newInstance(resourceProviderType)).willReturn(resourceProvider);
        given(resourceProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(resourceProvider.start(testContext, configuration)).willReturn(localResourceInstance);
        given(localResourceInstance.getResource()).willReturn(serverInstance);
        given(localResource.resourceName()).willReturn(serverName);
        given(localResource.resourceContract()).willReturn(serverContract);
        willDoNothing().given(serviceInstance).replace(serverInstance, serverName, serverContract);
        given(localResourceInstance.getClient()).willReturn(clientInstanceResult);
        given(localResource.clientName()).willReturn(clientName);
        given(localResource.clientContract()).willReturn(clientContract);
        willDoNothing().given(serviceInstance).replace(clientInstance, clientName, clientContract);
        willDoNothing().given(serviceInstance).addConstant(localResourceInstance, resourceName, resourceContract);
        given(localResource.name()).willReturn(resourceName);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getLocalResources();
        verify(localResource).value();
        verify(reflectionUtil).newInstance(resourceProviderType);
        verify(serviceInstance).inject(resourceProvider);
        verify(resourceProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(resourceProvider).start(testContext, configuration);
        verify(localResourceInstance).getResource();
        verify(localResource).resourceName();
        verify(localResource).resourceContract();
        verify(serviceInstance).replace(serverInstance, serverName, serverContract);
        verify(localResourceInstance).getClient();
        verify(localResource).clientName();
        verify(localResource).clientContract();
        verify(serviceInstance).replace(clientInstance, clientName, clientContract);
        verify(localResource).name();
        verify(serviceInstance).addConstant(localResourceInstance, resourceName, resourceContract);
        verify(resourceProviders).add(resourceProvider);

        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStopWithNoElementsStopShouldStopVirtualResourceProvider() {
        cut.stop();

        verify(resourceProviders).parallelStream();
        verifyNoMoreInteractions(resourceProviders);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() {
        LocalResourceProvider resourceProvider = mock(LocalResourceProvider.class);
        resourceProviders.add(resourceProvider);

        cut.stop();

        verify(resourceProviders).parallelStream();
        verify(resourceProvider).stop();
        verifyNoMoreInteractions(resourceProvider);
    }

}
