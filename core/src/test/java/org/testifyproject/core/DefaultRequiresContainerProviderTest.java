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
import org.testifyproject.ContainerInstance;
import org.testifyproject.ContainerProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.annotation.RequiresContainer;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.fixture.container.TestContainerProvider;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultRequiresContainerProviderTest {

    DefaultRequiresContainerProvider cut;
    ServiceLocatorUtil serviceLocatorUtil;
    ReflectionUtil reflectionUtil;
    Queue<ContainerProvider> containerProviders;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        reflectionUtil = mock(ReflectionUtil.class);
        containerProviders = mock(Queue.class, delegatesTo(new ConcurrentLinkedQueue<>()));

        cut = new DefaultRequiresContainerProvider(serviceLocatorUtil, reflectionUtil, containerProviders);
    }

    @Test
    public void verifyDefaultConstructor() {
        DefaultRequiresContainerProvider result = new DefaultRequiresContainerProvider();

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextCallToStartShouldThrowException() {
        TestContext testContext = null;
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        cut.start(testContext, serviceInstance);
    }

    @Test
    public void callToStartWithoutRequiresContainersShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<RequiresContainer> requiresContainers = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getRequiresContainers()).willReturn(requiresContainers);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getRequiresContainers();
        verifyNoMoreInteractions(testContext, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithNoConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RequiresContainer requiresContainer = mock(RequiresContainer.class);
        List<RequiresContainer> requiresContainers = ImmutableList.of(requiresContainer);
        Class containerProviderType = ContainerProvider.class;
        ContainerProvider containerProvider = mock(ContainerProvider.class);
        Object configuration = mock(Object.class);
        TestReifier testReifier = mock(TestReifier.class);
        Class<ContainerInstance> containerInstanceType = ContainerInstance.class;
        ContainerInstance containerInstance = mock(containerInstanceType);
        String serviceName = "";
        String imageName = "imageName";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(requiresContainer.name()).willReturn(serviceName);
        given(requiresContainer.provider()).willReturn(containerProviderType);
        given(requiresContainer.value()).willReturn(imageName);
        given(testDescriptor.getRequiresContainers()).willReturn(requiresContainers);
        given(serviceLocatorUtil.getOne(containerProviderType)).willReturn(containerProvider);
        given(containerProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(containerProvider.start(testContext, requiresContainer, configuration)).willReturn(containerInstance);
        willDoNothing().given(serviceInstance).addConstant(containerInstance, serviceName, containerInstanceType);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(requiresContainer).name();
        verify(requiresContainer).provider();
        verify(requiresContainer).value();
        verify(testDescriptor).getRequiresContainers();
        verify(serviceLocatorUtil).getOne(containerProviderType);
        verify(serviceInstance).inject(containerProvider);
        verify(containerProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(containerProvider).start(testContext, requiresContainer, configuration);
        verify(serviceInstance).addConstant(containerInstance, imageName, containerInstanceType);
        verifyNoMoreInteractions(testContext, testReifier, testDescriptor, serviceInstance);
    }

    @Test
    public void callToStartWithConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RequiresContainer requiresContainer = mock(RequiresContainer.class);
        List<RequiresContainer> requiresContainers = ImmutableList.of(requiresContainer);
        Class containerProviderType = TestContainerProvider.class;
        ContainerProvider containerProvider = mock(ContainerProvider.class);
        Object configuration = null;
        TestReifier testReifier = mock(TestReifier.class);
        Class<ContainerInstance> containerInstanceType = ContainerInstance.class;
        ContainerInstance containerInstance = mock(containerInstanceType);
        String serviceName = "containerName";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(requiresContainer.name()).willReturn(serviceName);
        given(requiresContainer.provider()).willReturn(containerProviderType);
        given(testDescriptor.getRequiresContainers()).willReturn(requiresContainers);
        given(reflectionUtil.newInstance(containerProviderType)).willReturn(containerProvider);
        given(containerProvider.configure(testContext)).willReturn(configuration);
        given(testReifier.configure(testContext, configuration)).willReturn(configuration);
        given(containerProvider.start(testContext, requiresContainer, configuration)).willReturn(containerInstance);
        willDoNothing().given(serviceInstance).addConstant(containerInstance, serviceName, containerInstanceType);

        cut.start(testContext, serviceInstance);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestReifier();
        verify(testDescriptor).getRequiresContainers();
        verify(requiresContainer).name();
        verify(requiresContainer).provider();
        verify(reflectionUtil).newInstance(containerProviderType);
        verify(serviceInstance).inject(containerProvider);
        verify(containerProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(containerProvider).start(testContext, requiresContainer, configuration);
        verify(serviceInstance).addConstant(any(containerInstanceType), eq(serviceName), eq(containerInstanceType));
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
        ContainerProvider containerProvider = mock(ContainerProvider.class);
        containerProviders.add(containerProvider);

        cut.stop();

        verify(containerProviders).parallelStream();
        verify(containerProvider).stop();
        verifyNoMoreInteractions(containerProvider);
    }

}
