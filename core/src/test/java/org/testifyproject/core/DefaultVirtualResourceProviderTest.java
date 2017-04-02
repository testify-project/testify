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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.fixture.container.TestContainerProvider;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.VirtualResourceInstance;

/**
 *
 * @author saden
 */
public class DefaultVirtualResourceProviderTest {

    DefaultVirtualResourceProvider cut;
    ServiceLocatorUtil serviceLocatorUtil;
    ReflectionUtil reflectionUtil;
    Queue<VirtualResourceProvider> containerProviders;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        reflectionUtil = mock(ReflectionUtil.class);
        containerProviders = mock(Queue.class, delegatesTo(new ConcurrentLinkedQueue<>()));

        cut = new DefaultVirtualResourceProvider(serviceLocatorUtil, reflectionUtil, containerProviders);
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

        cut.start(testContext, serviceInstance);
    }

    @Test
    public void callToStartWithoutVirtualResourcesShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<VirtualResource> virtualResources = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getVirtualResources();
        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithNoConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        VirtualResource virtualResource = mock(VirtualResource.class);
        List<VirtualResource> virtualResources = ImmutableList.of(virtualResource);
        Class containerProviderType = VirtualResourceProvider.class;
        VirtualResourceProvider containerProvider = mock(VirtualResourceProvider.class);
        Object configuration = mock(Object.class);
        TestReifier testReifier = mock(TestReifier.class);
        Class<VirtualResourceInstance> virtualResourceInstanceType = VirtualResourceInstance.class;
        VirtualResourceInstance virtualResourceInstance = mock(virtualResourceInstanceType);
        String serviceName = "";
        String imageName = "imageName";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(virtualResource.name()).willReturn(serviceName);
        given(virtualResource.provider()).willReturn(containerProviderType);
        given(virtualResource.value()).willReturn(imageName);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(serviceLocatorUtil.getOne(containerProviderType)).willReturn(containerProvider);
        given(containerProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(containerProvider.start(testContext, virtualResource, configuration)).willReturn(virtualResourceInstance);
        willDoNothing().given(serviceInstance).addConstant(virtualResourceInstance, serviceName, virtualResourceInstanceType);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(virtualResource).name();
        verify(virtualResource).provider();
        verify(virtualResource).value();
        verify(testDescriptor).getVirtualResources();
        verify(serviceLocatorUtil).getOne(containerProviderType);
        verify(serviceInstance).inject(containerProvider);
        verify(containerProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(containerProvider).start(testContext, virtualResource, configuration);
        verify(serviceInstance).addConstant(virtualResourceInstance, imageName, virtualResourceInstanceType);
        verifyNoMoreInteractions(testContext, testReifier, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        VirtualResource virtualResource = mock(VirtualResource.class);
        List<VirtualResource> virtualResources = ImmutableList.of(virtualResource);
        Class containerProviderType = TestContainerProvider.class;
        VirtualResourceProvider containerProvider = mock(VirtualResourceProvider.class);
        Object configuration = null;
        TestReifier testReifier = mock(TestReifier.class);
        Class<VirtualResourceInstance> virtualResourceInstanceType = VirtualResourceInstance.class;
        VirtualResourceInstance virtualResourceInstance = mock(virtualResourceInstanceType);
        String serviceName = "containerName";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(virtualResource.name()).willReturn(serviceName);
        given(virtualResource.provider()).willReturn(containerProviderType);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(reflectionUtil.newInstance(containerProviderType)).willReturn(containerProvider);
        given(containerProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(containerProvider.start(testContext, virtualResource, configuration)).willReturn(virtualResourceInstance);
        willDoNothing().given(serviceInstance).addConstant(virtualResourceInstance, serviceName, virtualResourceInstanceType);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getVirtualResources();
        verify(virtualResource).name();
        verify(virtualResource).provider();
        verify(reflectionUtil).newInstance(containerProviderType);
        verify(serviceInstance).inject(containerProvider);
        verify(containerProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(containerProvider).start(testContext, virtualResource, configuration);
        verify(serviceInstance).addConstant(any(virtualResourceInstanceType), eq(serviceName), eq(virtualResourceInstanceType));
        verifyNoMoreInteractions(testContext, testReifier, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStopWithNoElementsStopShouldStopContainerProvider() {
        cut.stop();

        verify(containerProviders).parallelStream();
        verifyNoMoreInteractions(containerProviders);
    }

    @Test
    public void callToStopWithElementsStopShouldStopContainerProvider() {
        VirtualResourceProvider containerProvider = mock(VirtualResourceProvider.class);
        containerProviders.add(containerProvider);

        cut.stop();

        verify(containerProviders).parallelStream();
        verify(containerProvider).stop();
        verifyNoMoreInteractions(containerProvider);
    }

}
