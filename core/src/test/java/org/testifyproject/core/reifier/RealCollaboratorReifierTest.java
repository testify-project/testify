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

import java.util.Collection;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Real;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class RealCollaboratorReifierTest {
    
    RealCollaboratorReifier sut;

    @Before
    public void init() {
        sut = new RealCollaboratorReifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextReifyShouldThrowException() {
        sut.reify(null);
    }

    @Test
    public void givenTestDescriptorWithoutFieldsReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = mock(Object.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        sut.reify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getFieldDescriptors();
    }

    @Test
    public void givenTestDescriptorWithRealFieldAndMockValueReifyShouldSetRealField() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = mock(Object.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Real real = mock(Real.class);
        Optional<Real> foundReal = Optional.of(real);
        Class fieldType = Object.class;
        Object fieldValue = mock(Object.class);
        Optional<Object> foundValue = Optional.of(fieldValue);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getReal()).willReturn(foundReal);
        given(fieldDescriptor.getType()).willReturn(fieldType);
        given(fieldDescriptor.getValue(testInstance)).willReturn(foundValue);

        sut.reify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getReal();
        verify(fieldDescriptor).getType();
        verify(fieldDescriptor).getValue(testInstance);
        verify(fieldDescriptor).setValue(testInstance, fieldValue);
    }

    @Test
    public void givenTestDescriptorWithRealFieldAndNoValueReifyShouldSetRealField() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = mock(Object.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Real real = mock(Real.class);
        Optional<Real> foundReal = Optional.of(real);
        Class fieldType = Object.class;
        Optional<Object> foundValue = Optional.empty();
        Object fieldValue = new Object();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getReal()).willReturn(foundReal);
        given(fieldDescriptor.getType()).willReturn(fieldType);
        given(fieldDescriptor.getValue(testInstance)).willReturn(foundValue);

        sut.reify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getReal();
        verify(fieldDescriptor).getType();
        verify(fieldDescriptor).getValue(testInstance);
        verify(fieldDescriptor).setValue(eq(testInstance), any(fieldType));
    }
    
}
