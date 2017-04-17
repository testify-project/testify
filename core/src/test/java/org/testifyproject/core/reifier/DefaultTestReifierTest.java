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
import org.testifyproject.CutDescriptor;
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
public class DefaultTestReifierTest {

    DefaultTestReifier cut;

    @Before
    public void init() {
        cut = spy(new DefaultTestReifier());
    }

    @Test
    public void callToReifyShouldReifyCollaborators() {
        TestContext testContext = mock(TestContext.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Object testInstance = new Object();
        Object cutInstance = new Object();
        Optional<Object> foundCutInstance = Optional.of(cutInstance);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(testFieldDescriptor);

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(cutDescriptor.getValue(testInstance)).willReturn(foundCutInstance);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testFieldDescriptor.isInjectable()).willReturn(true);
        willDoNothing().given(cut).processTestField(testContext, testFieldDescriptor, cutDescriptor, cutInstance);

        cut.reify(testContext);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestInstance();
        verify(cutDescriptor).getValue(testInstance);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getFieldDescriptors();
        verify(testFieldDescriptor).isInjectable();
    }

    @Test
    public void givenTypeAndNameMatchingFieldProcessTestFieldShouldProcessTestField() {
        TestContext testContext = mock(TestContext.class);
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Object cutInstance = new Object();

        MockProvider mockProvder = mock(MockProvider.class);
        Object testInstance = new Object();
        String testFieldName = "fieldName";
        Type testFieldGenericType = Object.class;
        FieldDescriptor cutFieldDescriptor = mock(FieldDescriptor.class);
        Optional<FieldDescriptor> foundMatchingField = Optional.of(cutFieldDescriptor);

        given(testContext.getMockProvider()).willReturn(mockProvder);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testFieldDescriptor.getDefinedName()).willReturn(testFieldName);
        given(testFieldDescriptor.getGenericType()).willReturn(testFieldGenericType);
        given(cutDescriptor.findFieldDescriptor(testFieldGenericType, testFieldName)).willReturn(foundMatchingField);
        willDoNothing().given(cut).processCutField(testFieldDescriptor, cutFieldDescriptor, testInstance, cutInstance, mockProvder);

        cut.processTestField(testContext, testFieldDescriptor, cutDescriptor, cutInstance);

        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(testFieldDescriptor).getDefinedName();
        verify(testFieldDescriptor).getGenericType();
        verify(cutDescriptor).findFieldDescriptor(testFieldGenericType, testFieldName);
    }

    @Test
    public void givenTypeMatchingFieldProcessTestFieldShouldProcessTestField() {
        TestContext testContext = mock(TestContext.class);
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Object cutInstance = new Object();

        MockProvider mockProvder = mock(MockProvider.class);
        Object testInstance = new Object();
        String testFieldName = "fieldName";
        Type testFieldGenericType = Object.class;
        FieldDescriptor cutFieldDescriptor = mock(FieldDescriptor.class);
        Optional<FieldDescriptor> unfoundMatchingField = Optional.empty();
        Optional<FieldDescriptor> foundMatchingField = Optional.of(cutFieldDescriptor);

        given(testContext.getMockProvider()).willReturn(mockProvder);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testFieldDescriptor.getDefinedName()).willReturn(testFieldName);
        given(testFieldDescriptor.getGenericType()).willReturn(testFieldGenericType);
        given(cutDescriptor.findFieldDescriptor(testFieldGenericType, testFieldName)).willReturn(unfoundMatchingField);
        given(cutDescriptor.findFieldDescriptor(testFieldGenericType)).willReturn(foundMatchingField);
        willDoNothing().given(cut).processCutField(testFieldDescriptor, cutFieldDescriptor, testInstance, cutInstance, mockProvder);

        cut.processTestField(testContext, testFieldDescriptor, cutDescriptor, cutInstance);

        verify(testContext).getMockProvider();
        verify(testContext).getTestInstance();
        verify(testFieldDescriptor).getDefinedName();
        verify(testFieldDescriptor).getGenericType();
        verify(cutDescriptor).findFieldDescriptor(testFieldGenericType, testFieldName);
        verify(cutDescriptor).findFieldDescriptor(testFieldGenericType);
    }

    @Test
    public void givenCutFieldAndVirtualTestFieldProcessCutFieldShouldSetTestFieldToVirtualInstance() {
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        FieldDescriptor cutFieldDescriptor = mock(FieldDescriptor.class);
        Object testInstance = new Object();
        Object cutInstance = new Object();
        MockProvider mockProvder = mock(MockProvider.class);
        Class testFieldType = Object.class;
        Optional<Object> testFieldValue = Optional.empty();
        Object cutValue = new Object();
        Optional<Object> cutFieldValue = Optional.of(cutValue);
        Virtual virtual = mock(Virtual.class);
        Optional<Virtual> foundVirtual = Optional.of(virtual);
        Object value = new Object();

        given(testFieldDescriptor.getType()).willReturn(testFieldType);
        given(testFieldDescriptor.getValue(testInstance)).willReturn(testFieldValue);
        given(cutFieldDescriptor.getValue(cutInstance)).willReturn(cutFieldValue);
        given(testFieldDescriptor.getVirtual()).willReturn(foundVirtual);
        given(mockProvder.createVirtual(testFieldType, cutValue)).willReturn(value);

        cut.processCutField(testFieldDescriptor, cutFieldDescriptor, testInstance, cutInstance, mockProvder);

        verify(testFieldDescriptor).getType();
        verify(testFieldDescriptor).getValue(testInstance);
        verify(cutFieldDescriptor).getValue(cutInstance);
        verify(testFieldDescriptor).getVirtual();
        verify(mockProvder).createVirtual(testFieldType, cutValue);
        verify(cutFieldDescriptor).setValue(cutInstance, value);
        verify(testFieldDescriptor).setValue(testInstance, value);
    }

    @Test
    public void givenCutFieldAndRealTestFieldProcessCutFieldShouldSetTestFieldToRealInstance() {
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        FieldDescriptor cutFieldDescriptor = mock(FieldDescriptor.class);
        Object testInstance = new Object();
        Object cutInstance = new Object();
        MockProvider mockProvder = mock(MockProvider.class);
        Class testFieldType = Object.class;
        Optional<Object> testFieldValue = Optional.empty();
        Object cutValue = new Object();
        Optional<Object> cutFieldValue = Optional.of(cutValue);
        Optional<Virtual> foundVirtual = Optional.empty();
        Real real = mock(Real.class);
        Optional<Real> foundReal = Optional.of(real);
        Object value = new Object();

        given(testFieldDescriptor.getType()).willReturn(testFieldType);
        given(testFieldDescriptor.getValue(testInstance)).willReturn(testFieldValue);
        given(cutFieldDescriptor.getValue(cutInstance)).willReturn(cutFieldValue);
        given(testFieldDescriptor.getVirtual()).willReturn(foundVirtual);
        given(testFieldDescriptor.getReal()).willReturn(foundReal);

        cut.processCutField(testFieldDescriptor, cutFieldDescriptor, testInstance, cutInstance, mockProvder);

        verify(testFieldDescriptor).getType();
        verify(testFieldDescriptor).getValue(testInstance);
        verify(cutFieldDescriptor).getValue(cutInstance);
        verify(testFieldDescriptor).getVirtual();
        verify(cutFieldDescriptor).setValue(cutInstance, cutValue);
        verify(testFieldDescriptor).setValue(testInstance, cutValue);
    }

    @Test
    public void givenTestFieldProcessCutFieldShouldSetCutFieldToRealInstance() {
        FieldDescriptor testFieldDescriptor = mock(FieldDescriptor.class);
        FieldDescriptor cutFieldDescriptor = mock(FieldDescriptor.class);
        Object testInstance = new Object();
        Object cutInstance = new Object();
        MockProvider mockProvder = mock(MockProvider.class);
        Class testFieldType = Object.class;
        Object testValue = new Object();
        Optional<Object> testFieldValue = Optional.of(testValue);
        Optional<Object> cutFieldValue = Optional.empty();
        Object value = new Object();

        given(testFieldDescriptor.getType()).willReturn(testFieldType);
        given(testFieldDescriptor.getValue(testInstance)).willReturn(testFieldValue);
        given(cutFieldDescriptor.getValue(cutInstance)).willReturn(cutFieldValue);

        cut.processCutField(testFieldDescriptor, cutFieldDescriptor, testInstance, cutInstance, mockProvder);

        verify(testFieldDescriptor).getType();
        verify(testFieldDescriptor).getValue(testInstance);
        verify(cutFieldDescriptor).getValue(cutInstance);
        verify(cutFieldDescriptor).setValue(cutInstance, testValue);
        verify(testFieldDescriptor).setValue(testInstance, testValue);
    }

}
