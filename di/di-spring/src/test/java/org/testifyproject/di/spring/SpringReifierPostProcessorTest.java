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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Provider;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.beans.PropertyValues;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Fake;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 *
 * @author saden
 */
public class SpringReifierPostProcessorTest {

    SpringReifierPostProcessor cut;
    TestContext testContext;

    @Before
    public void init() {
        testContext = mock(TestContext.class);

        cut = new SpringReifierPostProcessor(testContext);
    }

    @Test
    public void givenTestContextWithoutCutDescriptorResolveShouldReturnNull() {
        Class beanClass = Object.class;
        String beanName = "beanName";
        Optional<CutDescriptor> foundCutDescriptor = Optional.empty();

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);

        Object result = cut.postProcessBeforeInstantiation(beanClass, beanName);

        assertThat(result).isNull();

        verify(testContext).getCutDescriptor();
        verifyNoMoreInteractions(testContext);
    }

    @Test
    public void givenTestContextWithCutDescriptorResolveShouldReturnFieldValue() {
        Class beanClass = Object.class;
        String beanName = "beanName";
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = new Object();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Type fieldType = Object.class;
        Object value = new Object();
        Optional<Object> foundValue = Optional.of(value);
        Fake fake = mock(Fake.class);
        Optional<Fake> foundFake = Optional.of(fake);

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getFake()).willReturn(foundFake);
        given(fieldDescriptor.getGenericType()).willReturn(fieldType);
        given(fieldDescriptor.getValue(testInstance)).willReturn(foundValue);

        Object result = cut.postProcessBeforeInstantiation(beanClass, beanName);

        assertThat(result).isEqualTo(value);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getGenericType();
        verify(fieldDescriptor).getValue(testInstance);
    }

    @Test
    public void givenTestContextWithCutDescriptorResolveShouldReturnFakeInstance() {
        Class beanClass = Object.class;
        String beanName = "beanName";
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = new Object();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Type fieldType = new TypeToken<List<Object>>() {
        }.getType();

        Object value = new Object();
        Fake fake = mock(Fake.class);
        Optional<Fake> foundFake = Optional.of(fake);

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getFake()).willReturn(foundFake);
        given(fieldDescriptor.getGenericType()).willReturn(fieldType);
        given(mockProvider.createFake(Object.class)).willReturn(value);

        Object result = cut.postProcessBeforeInstantiation(beanClass, beanName);

        assertThat(result).isEqualTo(value);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(testContext).getMockProvider();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getGenericType();
        verify(mockProvider).createFake(Object.class);
    }

    @Test
    public void givenTestContextWithCutDescriptorAndNoFakeFieldResolveShouldReturnNull() {
        Class beanClass = Object.class;
        String beanName = "beanName";
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = new Object();
        Type requiredType = Object.class;
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Optional<Fake> foundFake = Optional.empty();

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getFake()).willReturn(foundFake);

        Object result = cut.postProcessBeforeInstantiation(beanClass, beanName);

        assertThat(result).isNull();

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(testContext).getMockProvider();
        verify(testDescriptor).getFieldDescriptors();
    }

    @Test
    public void callToProcessAfterInitializationShouldReturnSetCutFieldAndReturnBean() {
        Object bean = new Object();
        String beanName = "beanName";
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Type beanClass = Object.class;
        Object testInstance = new Object();

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(cutDescriptor.isCutClass(beanClass)).willReturn(true);
        given(testContext.getTestInstance()).willReturn(testInstance);

        Object result = cut.postProcessAfterInitialization(bean, beanName);

        assertThat(result).isSameAs(bean);
        verify(testContext).getCutDescriptor();
        verify(cutDescriptor).isCutClass(beanClass);
        verify(cutDescriptor).setValue(testInstance, bean);
    }

    @Test
    public void callToPostProcessAfterInstantiationShouldReturnTrue() {
        Object bean = new Object();
        String beanName = "beanName";

        boolean result = cut.postProcessAfterInstantiation(bean, beanName);

        assertThat(result).isTrue();

    }

    @Test
    public void callToPostProcessPropertyValuesShouldReturnSamePropertyValues() {
        PropertyValues propertyValues = mock(PropertyValues.class);
        PropertyDescriptor[] pds = {};
        Object bean = new Object();
        String beanName = "beanName";

        PropertyValues result = cut.postProcessPropertyValues(propertyValues, pds, bean, beanName);

        assertThat(result).isSameAs(propertyValues);
    }

    @Test
    public void callToPostProcessBeforeInitializationShouldReturnSameBean() {
        Object bean = new Object();
        String beanName = "beanName";

        Object result = cut.postProcessBeforeInitialization(bean, beanName);

        assertThat(result).isSameAs(bean);
    }

    @Test
    public void givenProviderGetRawTypeTokenShouldReturnRawType() {
        TypeToken<Provider<String>> typeToken = new TypeToken<Provider<String>>() {
        };
        TypeToken result = cut.getRawTypeToken(typeToken.getType());

        assertThat(result.getRawType()).isEqualTo(String.class);
    }

    @Test
    public void givenOptionalGetRawTypeTokenShouldReturnRawType() {
        TypeToken<Optional<String>> typeToken = new TypeToken<Optional<String>>() {
        };
        TypeToken result = cut.getRawTypeToken(typeToken.getType());

        assertThat(result.getRawType()).isEqualTo(String.class);
    }

    @Test
    public void givenMapProviderGetRawTypeTokenShouldReturnRawType() {
        TypeToken<Map<Integer, String>> typeToken = new TypeToken<Map<Integer, String>>() {
        };
        TypeToken result = cut.getRawTypeToken(typeToken.getType());

        assertThat(result.getRawType()).isEqualTo(String.class);
    }

    @Test
    public void givenCollectionProviderGetRawTypeTokenShouldReturnRawType() {
        TypeToken<Collection<String>> typeToken = new TypeToken<Collection<String>>() {
        };
        TypeToken result = cut.getRawTypeToken(typeToken.getType());

        assertThat(result.getRawType()).isEqualTo(String.class);
    }

}
