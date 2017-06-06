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

import java.lang.reflect.Type;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;

/**
 *
 * @author saden
 */
public class DefaultTestConfigurerTest {

    DefaultTestConfigurer sut;

    @Before
    public void init() {
        sut = new DefaultTestConfigurer();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextConfigureShouldThrowException() {
        TestContext testContext = null;
        Object configuration = mock(Object.class);

        sut.configure(testContext, configuration);

    }

    @Test
    public void givenNullConfigurationConfigureShouldReturnNull() {
        TestContext testContext = mock(TestContext.class);
        Object configuration = null;

        Object result = sut.configure(testContext, configuration);
        assertThat(result).isNull();
    }

    @Test
    public void givenNoConfigHandlerConfigureShouldReturnOriginalConfiguration() {
        TestContext testContext = mock(TestContext.class);
        Object configuration = new Object();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Type configurableType = Object.class;
        Optional<MethodDescriptor> configHandler = Optional.empty();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.findConfigHandler(configurableType)).willReturn(configHandler);

        Object result = sut.configure(testContext, configuration);

        assertThat(result).isEqualTo(configuration);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).findConfigHandler(configurableType);
    }

    @Test
    public void givenConfigHandlerWithInstanceConfigureShouldReturnNewConfiguration() {
        TestContext testContext = mock(TestContext.class);
        Object configuration = new Object();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Type configurableType = Object.class;
        MethodDescriptor configHandler = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundConfigHandler = Optional.of(configHandler);
        Object testInstance = new Object();
        Object configInstance = new Object();
        Optional<Object> foundConfigInstance = Optional.of(configInstance);
        Object value = new Object();
        Optional<Object> foundValue = Optional.of(value);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.findConfigHandler(configurableType)).willReturn(foundConfigHandler);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(configHandler.getInstance()).willReturn(foundConfigInstance);
        given(configHandler.invoke(configInstance, configuration)).willReturn(foundValue);

        Object result = sut.configure(testContext, configuration);

        assertThat(result).isEqualTo(value);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).findConfigHandler(configurableType);
        verify(testContext).getTestInstance();
        verify(configHandler).getInstance();
        verify(configHandler).invoke(configInstance, configuration);
    }

    @Test
    public void givenConfigHandlerWithoutInstanceConfigureShouldReturnNewConfiguration() {
        TestContext testContext = mock(TestContext.class);
        Object configuration = new Object();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Type configurableType = Object.class;
        MethodDescriptor configHandler = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundConfigHandler = Optional.of(configHandler);
        Object testInstance = new Object();
        Optional<Object> foundConfigInstance = Optional.empty();
        Object value = new Object();
        Optional<Object> foundValue = Optional.of(value);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.findConfigHandler(configurableType)).willReturn(foundConfigHandler);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(configHandler.getInstance()).willReturn(foundConfigInstance);
        given(configHandler.invoke(testInstance, configuration)).willReturn(foundValue);

        Object result = sut.configure(testContext, configuration);

        assertThat(result).isEqualTo(value);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).findConfigHandler(configurableType);
        verify(testContext).getTestInstance();
        verify(configHandler).getInstance();
        verify(configHandler).invoke(testInstance, configuration);
    }
    
    @Test
    public void givenConfigHandlerWithInstanceAndNoValueConfigureShouldReturnOriginalConfiguration() {
        TestContext testContext = mock(TestContext.class);
        Object configuration = new Object();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Type configurableType = Object.class;
        MethodDescriptor configHandler = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundConfigHandler = Optional.of(configHandler);
        Object testInstance = new Object();
        Object configInstance = new Object();
        Optional<Object> foundConfigInstance = Optional.of(configInstance);
        Optional<Object> foundValue = Optional.empty();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.findConfigHandler(configurableType)).willReturn(foundConfigHandler);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(configHandler.getInstance()).willReturn(foundConfigInstance);
        given(configHandler.invoke(configInstance, configuration)).willReturn(foundValue);

        Object result = sut.configure(testContext, configuration);

        assertThat(result).isEqualTo(configuration);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).findConfigHandler(configurableType);
        verify(testContext).getTestInstance();
        verify(configHandler).getInstance();
        verify(configHandler).invoke(configInstance, configuration);
    }

}
