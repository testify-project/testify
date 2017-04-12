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
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.fixture.container.TestVirtualResourceProvider;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.TestConfigurer;

/**
 *
 * @author saden
 */
public class DefaultVirtualResourceProviderTest {

    DefaultVirtualResourceProvider cut;
    ServiceLocatorUtil serviceLocatorUtil;
    ReflectionUtil reflectionUtil;
    Queue<VirtualResourceProvider> virtualResourceProviders;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        reflectionUtil = mock(ReflectionUtil.class);
        virtualResourceProviders = mock(Queue.class, delegatesTo(new ConcurrentLinkedQueue<>()));

        cut = new DefaultVirtualResourceProvider(serviceLocatorUtil, reflectionUtil, virtualResourceProviders);
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
        verify(testContext).getTestConfigurer();
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
        Class virtualResourceProviderType = VirtualResourceProvider.class;
        VirtualResourceProvider virtualResourceProvider = mock(VirtualResourceProvider.class);
        Object configuration = mock(Object.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        Class<VirtualResourceInstance> virtualResourceInstanceType = VirtualResourceInstance.class;
        VirtualResourceInstance virtualResourceInstance = mock(virtualResourceInstanceType);
        String serviceName = "";
        String imageName = "imageName";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(virtualResource.name()).willReturn(serviceName);
        given(virtualResource.provider()).willReturn(virtualResourceProviderType);
        given(virtualResource.value()).willReturn(imageName);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(serviceLocatorUtil.getOne(virtualResourceProviderType)).willReturn(virtualResourceProvider);
        given(virtualResourceProvider.configure(testContext)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(virtualResourceProvider.start(testContext, virtualResource, configuration)).willReturn(virtualResourceInstance);
        willDoNothing().given(serviceInstance).addConstant(virtualResourceInstance, serviceName, virtualResourceInstanceType);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(virtualResource).name();
        verify(virtualResource).provider();
        verify(virtualResource).value();
        verify(testDescriptor).getVirtualResources();
        verify(serviceLocatorUtil).getOne(virtualResourceProviderType);
        verify(serviceInstance).inject(virtualResourceProvider);
        verify(virtualResourceProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, configuration);
        verify(virtualResourceProvider).start(testContext, virtualResource, configuration);
        verify(serviceInstance).addConstant(virtualResourceInstance, imageName, virtualResourceInstanceType);
        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        VirtualResource virtualResource = mock(VirtualResource.class);
        List<VirtualResource> virtualResources = ImmutableList.of(virtualResource);
        Class virtualResourceProviderType = TestVirtualResourceProvider.class;
        VirtualResourceProvider virtualResourceProvider = mock(VirtualResourceProvider.class);
        Object configuration = null;
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        Class<VirtualResourceInstance> virtualResourceInstanceType = VirtualResourceInstance.class;
        VirtualResourceInstance virtualResourceInstance = mock(virtualResourceInstanceType);
        String serviceName = "containerName";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(virtualResource.name()).willReturn(serviceName);
        given(virtualResource.provider()).willReturn(virtualResourceProviderType);
        given(testDescriptor.getVirtualResources()).willReturn(virtualResources);
        given(reflectionUtil.newInstance(virtualResourceProviderType)).willReturn(virtualResourceProvider);
        given(virtualResourceProvider.configure(testContext)).willReturn(configuration);
        given(testConfigurer.configure(testContext, configuration)).willReturn(configuration);
        given(virtualResourceProvider.start(testContext, virtualResource, configuration)).willReturn(virtualResourceInstance);
        willDoNothing().given(serviceInstance).addConstant(virtualResourceInstance, serviceName, virtualResourceInstanceType);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestConfigurer();
        verify(testDescriptor).getVirtualResources();
        verify(virtualResource).name();
        verify(virtualResource).provider();
        verify(reflectionUtil).newInstance(virtualResourceProviderType);
        verify(serviceInstance).inject(virtualResourceProvider);
        verify(virtualResourceProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, configuration);
        verify(virtualResourceProvider).start(testContext, virtualResource, configuration);
        verify(serviceInstance).addConstant(any(virtualResourceInstanceType), eq(serviceName), eq(virtualResourceInstanceType));
        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStopWithNoElementsStopShouldStopVirtualResourceProvider() {
        cut.stop();

        verify(virtualResourceProviders).parallelStream();
        verifyNoMoreInteractions(virtualResourceProviders);
    }

    @Test
    public void callToStopWithElementsStopShouldStopVirtualResourceProvider() {
        VirtualResourceProvider virtualResourceProvider = mock(VirtualResourceProvider.class);
        virtualResourceProviders.add(virtualResourceProvider);

        cut.stop();

        verify(virtualResourceProviders).parallelStream();
        verify(virtualResourceProvider).stop();
        verifyNoMoreInteractions(virtualResourceProvider);
    }

}
