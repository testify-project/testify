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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Primary;
import org.testifyproject.TestContext;
import org.testifyproject.di.fixture.common.ControllerService;
import org.testifyproject.di.fixture.module.TestModule;

/**
 *
 * @author saden
 */
@Ignore
public class SpringBeanFactoryPostProcessorTest {

    SpringBeanFactoryPostProcessor sut;
    TestContext testContext;

    @Before
    public void init() {
        testContext = mock(TestContext.class);

        sut = spy(new SpringBeanFactoryPostProcessor(testContext));
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
    public void callToProcessConfigurationWithPrimaryShouldProcessPrimary() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = TestModule.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String factoryBeanName = "factoryBeanName";

        given(beanDefinition.getFactoryBeanName()).willReturn(factoryBeanName);
        given(beanFactory.getType(factoryBeanName)).willReturn(beanType);
        willDoNothing().given(sut).processPrimary(
                eq(beanFactory),
                eq(beanDefinition),
                eq(beanType),
                eq(beanName),
                any(Primary.class),
                eq(replacedBeanNames));

        sut.processConfiguration(beanFactory, beanDefinition, beanType, beanName,
                replacedBeanNames);

        verify(beanDefinition).getFactoryBeanName();
    }

    @Test
    public void callToProcessConfigurationWithoutPrimaryShouldProcessPrimary() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = Object.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String factoryBeanName = "factoryBeanName";
        Class factoryBeanType = TestModule.class;

        given(beanDefinition.getFactoryBeanName()).willReturn(factoryBeanName);
        given(beanFactory.getType(factoryBeanName)).willReturn(factoryBeanType);

        willDoNothing().given(sut).processPrimary(
                eq(beanFactory),
                eq(beanDefinition),
                eq(beanType),
                eq(beanName),
                any(Primary.class),
                eq(replacedBeanNames));

        sut.processConfiguration(beanFactory, beanDefinition, beanType, beanName,
                replacedBeanNames);

        verify(beanDefinition).getFactoryBeanName();
        verify(beanFactory).getType(factoryBeanName);
    }

    @Test
    public void callToProcessPrimaryWithExistingBeanShouldRemoveExistingBean() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = Object.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        Primary primary = mock(Primary.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String beanNameForType = beanName;
        String[] beanNamesForType = {beanNameForType};

        given(beanFactory.getBeanNamesForType(beanType)).willReturn(beanNamesForType);

        sut.processPrimary(beanFactory, beanDefinition, beanType, beanName, primary,
                replacedBeanNames);

        verify(beanFactory).getBeanNamesForType(beanType);
        verify(beanFactory).removeBeanDefinition(beanNameForType);
    }

    @Test
    public void callToProcessPrimaryWithoutExistingBeanShouldReplaceExistingBean() {
        DefaultListableBeanFactory beanFactory = mock(DefaultListableBeanFactory.class);
        Class beanType = Object.class;
        String beanName = "beanName";
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        Primary primary = mock(Primary.class);
        Set<String> replacedBeanNames = new HashSet<>();
        String beanNameForType = "beanNameForType";
        String[] beanNamesForType = {beanNameForType};

        given(beanFactory.getBeanNamesForType(beanType)).willReturn(beanNamesForType);

        sut.processPrimary(beanFactory, beanDefinition, beanType, beanName, primary,
                replacedBeanNames);

        assertThat(replacedBeanNames).isNotEmpty();
        verify(beanFactory).getBeanNamesForType(beanType);
        verify(beanFactory).removeBeanDefinition(beanNameForType);
        verify(beanFactory).registerBeanDefinition(eq(beanNameForType), any(
                GenericBeanDefinition.class));
    }

}
