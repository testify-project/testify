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
import org.testify.ContainerInstance;
import org.testify.ContainerProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.annotation.RequiresContainer;
import org.testify.core.util.ReflectionUtil;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.fixture.container.TestContainerProvider;
import org.testify.guava.common.collect.ImmutableList;

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
        Class providerType = ContainerProvider.class;
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
        given(requiresContainer.provider()).willReturn(providerType);
        given(requiresContainer.value()).willReturn(imageName);
        given(testDescriptor.getRequiresContainers()).willReturn(requiresContainers);
        given(serviceLocatorUtil.getOne(providerType)).willReturn(containerProvider);
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
        verify(serviceLocatorUtil).getOne(providerType);
        verify(containerProvider).configure(testContext);
        verify(containerProvider).start(testContext, requiresContainer, configuration);
        verify(serviceInstance).addConstant(containerInstance, imageName, containerInstanceType);
    }

    @Test
    public void callToStartWithConfigurationShouldStart() {
        TestContext testContext = mock(TestContext.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        RequiresContainer requiresContainer = mock(RequiresContainer.class);
        List<RequiresContainer> requiresContainers = ImmutableList.of(requiresContainer);
        Class providerType = TestContainerProvider.class;
        ContainerProvider containerProvider = mock(ContainerProvider.class);
        Object configuration = null;
        TestReifier testReifier = mock(TestReifier.class);
        Class<ContainerInstance> containerInstanceType = ContainerInstance.class;
        ContainerInstance containerInstance = mock(containerInstanceType);
        String serviceName = "containerName";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestReifier()).willReturn(testReifier);
        given(requiresContainer.name()).willReturn(serviceName);
        given(requiresContainer.provider()).willReturn(providerType);
        given(testDescriptor.getRequiresContainers()).willReturn(requiresContainers);
        given(reflectionUtil.newInstance(providerType)).willReturn(containerProvider);
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
        verify(reflectionUtil).newInstance(providerType);
        verify(containerProvider).configure(testContext);
        verify(testReifier).configure(testContext, configuration);
        verify(containerProvider).start(testContext, requiresContainer, configuration);
        verify(serviceInstance).addConstant(any(containerInstanceType), eq(serviceName), eq(containerInstanceType));
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
