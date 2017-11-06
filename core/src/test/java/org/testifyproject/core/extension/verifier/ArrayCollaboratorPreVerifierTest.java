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
package org.testifyproject.core.extension.verifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.fixture.common.InvalidTestClass;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class ArrayCollaboratorPreVerifierTest {

    ArrayCollaboratorPreVerifier sut;

    @Before
    public void init() {
        sut = new ArrayCollaboratorPreVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test
    public void givenNoFieldDescriptorVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getFieldDescriptors();
    }

    @Test(expected = TestifyException.class)
    public void givenArrayCollaboratorVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Class fieldType = Object[].class;
        String fieldName = "testField";
        String fieldTypeName = fieldType.getSimpleName();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getType()).willReturn(fieldType);
        given(fieldDescriptor.getName()).willReturn(fieldName);
        given(fieldDescriptor.getTypeName()).willReturn(fieldTypeName);

        try {
            sut.verify(testContext);
        } catch (Exception e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getTestClassName();
            verify(testDescriptor).getFieldDescriptors();
            verify(fieldDescriptor).getType();
            verify(fieldDescriptor).getName();
            verify(fieldDescriptor).getTypeName();
            throw e;
        }
    }

    @Test
    public void givenNonArrayCollaboratortVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        String testClassName = InvalidTestClass.class.getSimpleName();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Class fieldType = String.class;
        String fieldName = "testField";
        String fieldTypeName = fieldType.getSimpleName();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getType()).willReturn(fieldType);
        given(fieldDescriptor.getName()).willReturn(fieldName);
        given(fieldDescriptor.getTypeName()).willReturn(fieldTypeName);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getType();
        verify(fieldDescriptor).getName();
        verify(fieldDescriptor).getTypeName();
    }

}
