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
package org.testifyproject.di.hk2;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import javax.inject.Provider;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import static org.glassfish.hk2.api.InjectionResolver.SYSTEM_RESOLVER_NAME;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
public class HK2InjectionResolverTest {

    HK2InjectionResolver cut;
    TestContext testContext;
    private ServiceLocator serviceLocator;

    @Before
    public void init() {
        testContext = mock(TestContext.class);
        serviceLocator = mock(ServiceLocator.class);

        cut = new HK2InjectionResolver(testContext, serviceLocator);
    }

    @Test
    public void callToIsConstructorParameterIndicatorShouldReturnFalse() {
        boolean result = cut.isConstructorParameterIndicator();

        assertThat(result).isFalse();
    }

    @Test
    public void callToIsMethodParameterIndicatorShouldReturnFalse() {
        boolean result = cut.isMethodParameterIndicator();

        assertThat(result).isFalse();
    }

    @Test
    public void givenTestContextWithoutCutDescriptorResolveShouldCallThreeThirtyResolver() {
        Injectee injectee = mock(Injectee.class);
        ServiceHandle root = mock(ServiceHandle.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.empty();
        InjectionResolver threeThirtyResolver = mock(InjectionResolver.class);
        Object serviceInstance = new Object();

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(serviceLocator.getService(InjectionResolver.class, SYSTEM_RESOLVER_NAME)).willReturn(threeThirtyResolver);
        given(threeThirtyResolver.resolve(injectee, root)).willReturn(serviceInstance);

        Object result = cut.resolve(injectee, root);

        assertThat(result).isEqualTo(serviceInstance);

        verify(testContext).getCutDescriptor();
        verify(serviceLocator).getService(InjectionResolver.class, SYSTEM_RESOLVER_NAME);
        verify(threeThirtyResolver).resolve(injectee, root);
        verifyNoMoreInteractions(testContext, serviceLocator, threeThirtyResolver);
    }

    @Test
    public void givenTestContextWithCutDescriptorResolveShouldReturnFieldValue() {
        Injectee injectee = mock(Injectee.class);
        ServiceHandle root = mock(ServiceHandle.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = new Object();
        Type requiredType = Object.class;
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
        given(injectee.getRequiredType()).willReturn(requiredType);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getFake()).willReturn(foundFake);
        given(fieldDescriptor.getGenericType()).willReturn(fieldType);
        given(fieldDescriptor.getValue(testInstance)).willReturn(foundValue);

        Object result = cut.resolve(injectee, root);

        assertThat(result).isEqualTo(value);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(injectee).getRequiredType();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getGenericType();
        verify(fieldDescriptor).getValue(testInstance);
    }

    @Test
    public void givenTestContextWithCutDescriptorResolveShouldReturnFakeInstance() {
        Injectee injectee = mock(Injectee.class);
        ServiceHandle root = mock(ServiceHandle.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = new Object();
        Type requiredType = Object.class;
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Type fieldType = new TypeToken<IterableProvider<Object>>() {
        }.getType();

        Object value = new Object();
        Fake fake = mock(Fake.class);
        Optional<Fake> foundFake = Optional.of(fake);

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(injectee.getRequiredType()).willReturn(requiredType);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getFake()).willReturn(foundFake);
        given(fieldDescriptor.getGenericType()).willReturn(fieldType);
        given(mockProvider.createFake(Object.class)).willReturn(value);

        Object result = cut.resolve(injectee, root);

        assertThat(result).isEqualTo(value);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(injectee).getRequiredType();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getGenericType();
        verify(mockProvider).createFake(Object.class);
    }

    @Test
    public void givenTestContextWithCutDescriptorAndNoFakeFieldResolveShouldReturnThreeThirtyInstance() {
        Injectee injectee = mock(Injectee.class);
        ServiceHandle root = mock(ServiceHandle.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        InjectionResolver threeThirtyResolver = mock(InjectionResolver.class);
        Object serviceInstance = new Object();
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
        given(injectee.getRequiredType()).willReturn(requiredType);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getFake()).willReturn(foundFake);
        given(serviceLocator.getService(InjectionResolver.class, SYSTEM_RESOLVER_NAME)).willReturn(threeThirtyResolver);
        given(threeThirtyResolver.resolve(injectee, root)).willReturn(serviceInstance);

        Object result = cut.resolve(injectee, root);

        assertThat(result).isEqualTo(serviceInstance);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(injectee).getRequiredType();
        verify(testDescriptor).getFieldDescriptors();
        verify(serviceLocator).getService(InjectionResolver.class, SYSTEM_RESOLVER_NAME);
        verify(threeThirtyResolver).resolve(injectee, root);
    }

    @Test
    public void givenIterableProviderGetRawTypeTokenShouldReturnRawType() {
        TypeToken<IterableProvider<String>> typeToken = new TypeToken<IterableProvider<String>>() {
        };
        TypeToken result = cut.getRawTypeToken(typeToken.getType());

        assertThat(result.getRawType()).isEqualTo(String.class);
    }

    @Test
    public void givenProviderGetRawTypeTokenShouldReturnRawType() {
        TypeToken<Provider<String>> typeToken = new TypeToken<Provider<String>>() {
        };
        TypeToken result = cut.getRawTypeToken(typeToken.getType());

        assertThat(result.getRawType()).isEqualTo(String.class);
    }

}
