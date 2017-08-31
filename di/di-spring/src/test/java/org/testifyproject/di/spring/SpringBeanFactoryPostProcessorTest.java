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
package org.testifyproject.di.spring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.config.BeanDefinition;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.event.ContextClosedEvent;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Fixture;
import org.testifyproject.di.fixture.common.ControllerService;
import org.testifyproject.di.fixture.module.TestModule;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class SpringBeanFactoryPostProcessorTest {

    SpringBeanFactoryPostProcessor sut;
    TestContext testContext;
    ServiceInstance serviceInstance;
    List<ResourceProvider> resourcesProviders;

    @Before
    public void init() {
        testContext = mock(TestContext.class);
        serviceInstance = mock(ServiceInstance.class);
        resourcesProviders = mock(List.class);

        sut = spy(new SpringBeanFactoryPostProcessor(testContext, serviceInstance, resourcesProviders));
    }

    @Test
    public void callToGetOrderShouldReturnLoweestPrecedence() {
        int result = sut.getOrder();

        assertThat(result).isEqualTo(LOWEST_PRECEDENCE);
    }

    @Test
    public void givenConfigurationBeanTypePostProcessBeanFactoryShould() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        ConfigurableListableBeanFactory configurableListableBeanFactory = beanFactory;

        String beanName = "beanName";
        String[] beanNames = {beanName};
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Class beanType = TestModule.class;

        given(beanFactory.getBeanDefinitionNames()).willReturn(beanNames);
        given(beanFactory.getBeanDefinition(beanName)).willReturn(beanDefinition);
        given(beanFactory.getType(beanName)).willReturn(beanType);
        willDoNothing().given(sut).processConfiguration(
                eq(beanFactory),
                eq(beanDefinition),
                eq(beanType),
                eq(beanName),
                any(Set.class));

        sut.postProcessBeanFactory(configurableListableBeanFactory);

        verify(beanFactory).getBeanDefinitionNames();
        verify(beanFactory).getBeanDefinition(beanName);
        verify(beanFactory).getType(beanName);
        verify(beanFactory).addBeanPostProcessor(any(SpringReifierPostProcessor.class));
    }

    @Test
    public void givenControllerBeanTypePostProcessBeanFactoryShould() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        ConfigurableListableBeanFactory configurableListableBeanFactory = beanFactory;

        String beanName = "beanName";
        String[] beanNames = {beanName};
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Class beanType = ControllerService.class;

        given(beanFactory.getBeanDefinitionNames()).willReturn(beanNames);
        given(beanFactory.getBeanDefinition(beanName)).willReturn(beanDefinition);
        given(beanFactory.getType(beanName)).willReturn(beanType);

        sut.postProcessBeanFactory(configurableListableBeanFactory);

        verify(beanFactory).getBeanDefinitionNames();
        verify(beanFactory).getBeanDefinition(beanName);
        verify(beanFactory).getType(beanName);
        verify(beanDefinition).setScope(SCOPE_PROTOTYPE);
        verify(beanFactory).addBeanPostProcessor(any(SpringReifierPostProcessor.class));
    }

    @Test
    public void callToProcessConfigurationWithFixtureShouldProcessFixture() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = TestModule.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String factoryBeanName = "factoryBeanName";

        given(beanDefinition.getFactoryBeanName()).willReturn(factoryBeanName);
        willDoNothing().given(sut).processFixture(
                eq(beanFactory),
                eq(beanDefinition),
                eq(beanType),
                eq(beanName),
                any(Fixture.class),
                eq(replacedBeanNames));

        sut.processConfiguration(beanFactory, beanDefinition, beanType, beanName, replacedBeanNames);

        verify(beanDefinition).getFactoryBeanName();
    }

    @Test
    public void callToProcessConfigurationWithoutFixtureShouldProcessFixture() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = Object.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String factoryBeanName = "factoryBeanName";
        Class factoryBeanType = TestModule.class;

        given(beanDefinition.getFactoryBeanName()).willReturn(factoryBeanName);
        given(beanFactory.getType(factoryBeanName)).willReturn(factoryBeanType);

        willDoNothing().given(sut).processFixture(
                eq(beanFactory),
                eq(beanDefinition),
                eq(beanType),
                eq(beanName),
                any(Fixture.class),
                eq(replacedBeanNames));

        sut.processConfiguration(beanFactory, beanDefinition, beanType, beanName, replacedBeanNames);

        verify(beanDefinition).getFactoryBeanName();
        verify(beanFactory).getType(factoryBeanName);
    }

    @Test
    public void callToProcessFixtureWithExistingBeanShouldRemoveExistingBean() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = Object.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Fixture fixture = mock(Fixture.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String beanNameForType = beanName;
        String[] beanNamesForType = {beanNameForType};

        given(beanFactory.getBeanNamesForType(beanType)).willReturn(beanNamesForType);

        sut.processFixture(beanFactory, beanDefinition, beanType, beanName, fixture, replacedBeanNames);

        verify(beanFactory).getBeanNamesForType(beanType);
        verify(beanFactory).removeBeanDefinition(beanNameForType);
    }

    @Test
    public void callToProcessFixtureWithoutExistingBeanShouldReplaceExistingBean() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = Object.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        Fixture fixture = mock(Fixture.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String beanNameForType = "beanNameForType";
        String[] beanNamesForType = {beanNameForType};

        given(beanFactory.getBeanNamesForType(beanType)).willReturn(beanNamesForType);
        given(fixture.init()).willReturn("init");
        given(fixture.destroy()).willReturn("destroy");

        sut.processFixture(beanFactory, beanDefinition, beanType, beanName, fixture, replacedBeanNames);

        assertThat(replacedBeanNames).isNotEmpty();
        verify(beanFactory).getBeanNamesForType(beanType);
        verify(beanFactory).removeBeanDefinition(beanNameForType);
        verify(beanFactory).registerBeanDefinition(eq(beanNameForType), any(GenericBeanDefinition.class));
    }

    @Test
    public void callToOnApplicationEventWithLazyStartStrategyShouldStartResources() {
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        List<ResourceProvider> resourceProviders = ImmutableList.of(resourceProvider);
        sut = spy(new SpringBeanFactoryPostProcessor(testContext, serviceInstance, resourceProviders));

        StartStrategy resourceStartStrategy = StartStrategy.LAZY;

        given(testContext.getResourceStartStrategy()).willReturn(resourceStartStrategy);
        ContextClosedEvent event = mock(ContextClosedEvent.class);

        sut.onApplicationEvent(event);

        verify(testContext).getResourceStartStrategy();
        verify(resourceProvider).stop(testContext);
    }

}
