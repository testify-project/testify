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
package org.testifyproject.core.reifier;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.testifyproject.SutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Virtual;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultFinalReifierTest {

    DefaultFinalReifier sut;

    @Before
    public void init() {
        sut = spy(new DefaultFinalReifier());
    }

    @Test
    public void callToReifyShouldReifyCollaborators() {
        TestContext testContext = mock(TestContext.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Object testInstance = new Object();
        Object sutInstance = new Object();
        Optional<Object> foundSutInstance = Optional.of(sutInstance);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(testFieldDescriptor);

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundSutInstance);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testFieldDescriptor.isInjectable()).willReturn(true);
        willDoNothing().given(sut).processTestField(testContext, testFieldDescriptor, sutDescriptor, sutInstance);

        sut.reify(testContext);

        verify(testContext).getSutDescriptor();
        verify(testContext).getTestInstance();
        verify(sutDescriptor).getValue(testInstance);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getFieldDescriptors();
        verify(testFieldDescriptor).isInjectable();
    }

    @Test
    public void givenTypeAndNameMatchingFieldProcessTestFieldShouldProcessTestField() {
        TestContext testContext = mock(TestContext.class);
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutInstance = new Object();

        MockProvider mockProvder = mock(MockProvider.class);
        Object testInstance = new Object();
        String testFieldName = "fieldName";
        Type testFieldGenericType = Object.class;
        FieldDescriptor sutFieldDescriptor = mock(FieldDescriptor.class);
        Optional<FieldDescriptor> foundMatchingField = Optional.of(sutFieldDescriptor);

        given(testContext.getMockProvider()).willReturn(mockProvder);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testFieldDescriptor.getDeclaredName()).willReturn(testFieldName);
        given(testFieldDescriptor.getGenericType()).willReturn(testFieldGenericType);
        given(sutDescriptor.findFieldDescriptor(testFieldGenericType, testFieldName)).willReturn(foundMatchingField);
        willDoNothing().given(sut).processSutField(testFieldDescriptor, sutFieldDescriptor, testInstance, sutInstance, mockProvder);

        sut.processTestField(testContext, testFieldDescriptor, sutDescriptor, sutInstance);

        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(testFieldDescriptor).getDeclaredName();
        verify(testFieldDescriptor).getGenericType();
        verify(sutDescriptor).findFieldDescriptor(testFieldGenericType, testFieldName);
    }

    @Test
    public void givenTypeMatchingFieldProcessTestFieldShouldProcessTestField() {
        TestContext testContext = mock(TestContext.class);
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutInstance = new Object();

        MockProvider mockProvder = mock(MockProvider.class);
        Object testInstance = new Object();
        String testFieldName = "fieldName";
        Type testFieldGenericType = Object.class;
        FieldDescriptor sutFieldDescriptor = mock(FieldDescriptor.class);
        Optional<FieldDescriptor> unfoundMatchingField = Optional.empty();
        Optional<FieldDescriptor> foundMatchingField = Optional.of(sutFieldDescriptor);

        given(testContext.getMockProvider()).willReturn(mockProvder);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testFieldDescriptor.getDeclaredName()).willReturn(testFieldName);
        given(testFieldDescriptor.getGenericType()).willReturn(testFieldGenericType);
        given(sutDescriptor.findFieldDescriptor(testFieldGenericType, testFieldName)).willReturn(unfoundMatchingField);
        given(sutDescriptor.findFieldDescriptor(testFieldGenericType)).willReturn(foundMatchingField);
        willDoNothing().given(sut).processSutField(testFieldDescriptor, sutFieldDescriptor, testInstance, sutInstance, mockProvder);

        sut.processTestField(testContext, testFieldDescriptor, sutDescriptor, sutInstance);

        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(testFieldDescriptor).getDeclaredName();
        verify(testFieldDescriptor).getGenericType();
        verify(sutDescriptor).findFieldDescriptor(testFieldGenericType, testFieldName);
        verify(sutDescriptor).findFieldDescriptor(testFieldGenericType);
    }

    @Test
    public void givenSutFieldAndVirtualTestFieldProcessSutFieldShouldSetTestFieldToVirtualInstance() {
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        FieldDescriptor sutFieldDescriptor = mock(FieldDescriptor.class);
        Object testInstance = new Object();
        Object sutInstance = new Object();
        MockProvider mockProvder = mock(MockProvider.class);
        Class testFieldType = Object.class;
        Optional<Object> testFieldValue = Optional.empty();
        Object sutValue = new Object();
        Optional<Object> sutFieldValue = Optional.of(sutValue);
        Virtual virtual = mock(Virtual.class);
        Optional<Virtual> foundVirtual = Optional.of(virtual);
        Object value = new Object();

        given(testFieldDescriptor.getType()).willReturn(testFieldType);
        given(testFieldDescriptor.getValue(testInstance)).willReturn(testFieldValue);
        given(sutFieldDescriptor.getValue(sutInstance)).willReturn(sutFieldValue);
        given(testFieldDescriptor.getVirtual()).willReturn(foundVirtual);
        given(mockProvder.createVirtual(testFieldType, sutValue)).willReturn(value);

        sut.processSutField(testFieldDescriptor, sutFieldDescriptor, testInstance, sutInstance, mockProvder);

        verify(testFieldDescriptor).getType();
        verify(testFieldDescriptor).getValue(testInstance);
        verify(sutFieldDescriptor).getValue(sutInstance);
        verify(testFieldDescriptor).getVirtual();
        verify(mockProvder).createVirtual(testFieldType, sutValue);
        verify(sutFieldDescriptor).setValue(sutInstance, value);
        verify(testFieldDescriptor).setValue(testInstance, value);
    }

    @Test
    public void givenSutFieldAndRealTestFieldProcessSutFieldShouldSetTestFieldToRealInstance() {
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        FieldDescriptor sutFieldDescriptor = mock(FieldDescriptor.class);
        Object testInstance = new Object();
        Object sutInstance = new Object();
        MockProvider mockProvder = mock(MockProvider.class);
        Class testFieldType = Object.class;
        Optional<Object> testFieldValue = Optional.empty();
        Object sutValue = new Object();
        Optional<Object> sutFieldValue = Optional.of(sutValue);
        Optional<Virtual> foundVirtual = Optional.empty();
        Real real = mock(Real.class);
        Optional<Real> foundReal = Optional.of(real);
        Object value = new Object();

        given(testFieldDescriptor.getType()).willReturn(testFieldType);
        given(testFieldDescriptor.getValue(testInstance)).willReturn(testFieldValue);
        given(sutFieldDescriptor.getValue(sutInstance)).willReturn(sutFieldValue);
        given(testFieldDescriptor.getVirtual()).willReturn(foundVirtual);
        given(testFieldDescriptor.getReal()).willReturn(foundReal);

        sut.processSutField(testFieldDescriptor, sutFieldDescriptor, testInstance, sutInstance, mockProvder);

        verify(testFieldDescriptor).getType();
        verify(testFieldDescriptor).getValue(testInstance);
        verify(sutFieldDescriptor).getValue(sutInstance);
        verify(testFieldDescriptor).getVirtual();
        verify(sutFieldDescriptor).setValue(sutInstance, sutValue);
        verify(testFieldDescriptor).setValue(testInstance, sutValue);
    }

    @Test
    public void givenTestFieldProcessSutFieldShouldSetSutFieldToRealInstance() {
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        FieldDescriptor sutFieldDescriptor = mock(FieldDescriptor.class);
        Object testInstance = new Object();
        Object sutInstance = new Object();
        MockProvider mockProvder = mock(MockProvider.class);
        Class testFieldType = Object.class;
        Object testValue = new Object();
        Optional<Object> testFieldValue = Optional.of(testValue);
        Optional<Object> sutFieldValue = Optional.empty();
        Object value = new Object();

        given(testFieldDescriptor.getType()).willReturn(testFieldType);
        given(testFieldDescriptor.getValue(testInstance)).willReturn(testFieldValue);
        given(sutFieldDescriptor.getValue(sutInstance)).willReturn(sutFieldValue);

        sut.processSutField(testFieldDescriptor, sutFieldDescriptor, testInstance, sutInstance, mockProvder);

        verify(testFieldDescriptor).getType();
        verify(testFieldDescriptor).getValue(testInstance);
        verify(sutFieldDescriptor).getValue(sutInstance);
        verify(sutFieldDescriptor).setValue(sutInstance, testValue);
        verify(testFieldDescriptor).setValue(testInstance, testValue);
    }

}
