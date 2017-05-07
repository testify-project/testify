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
package org.testifyproject.core.verifier;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class ConstructorWiringVerifierTest {

    ConstructorWiringVerifier cut;

    @Before
    public void init() {
        cut = new ConstructorWiringVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        TestContext testContext = null;

        cut.verify(testContext);
    }

    @Test
    public void givenTestDescriptorWithCollaboratorProviderVerifyShoulDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        String testClassName = "TestClass";
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);

        cut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getCollaboratorProvider();
    }

    @Test
    public void givenTestDescriptorWithoutCollaboratorProviderAndWithoutCutDescriptorVerifyShoulDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        String testClassName = "TestClass";
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.empty();
        Optional<CutDescriptor> foundCutDescriptor = Optional.empty();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);

        cut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getCollaboratorProvider();
        verify(testContext).getCutDescriptor();
    }

    @Test
    public void givenCutWithExpectedFieldsDeclaredVerifyShouldLogNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        String testClassName = "TestClass";
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.empty();
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        String cutClassName = "CutClass";
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        Collection<ParameterDescriptor> parameterDescriptors = ImmutableList.of(parameterDescriptor);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Type fieldDescriptorType = String.class;
        Object fieldValue = new Object();
        Optional<Object> foundFieldValue = Optional.of(fieldValue);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(cutDescriptor.getTypeName()).willReturn(cutClassName);
        given(cutDescriptor.getParameterDescriptors()).willReturn(parameterDescriptors);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getGenericType()).willReturn(fieldDescriptorType);
        given(parameterDescriptor.isSubtypeOf(fieldDescriptorType)).willReturn(true);
        given(fieldDescriptor.getValue(testInstance)).willReturn(foundFieldValue);

        cut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getCollaboratorProvider();
        verify(testContext).getCutDescriptor();
        verify(cutDescriptor).getTypeName();
        verify(cutDescriptor).getParameterDescriptors();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getGenericType();
        verify(parameterDescriptor).isSubtypeOf(fieldDescriptorType);
        verify(fieldDescriptor).getValue(testInstance);

    }

    @Test
    public void givenCutWithExpectedFieldsDeclaredVerifyShouldLogWarning() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        String testClassName = "TestClass";
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.empty();
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        String cutClassName = "CutClass";
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        Collection<ParameterDescriptor> parameterDescriptors = ImmutableList.of(parameterDescriptor);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Type fieldDescriptorType = String.class;
        Object fieldValue = new Object();
        String paramterTypeName = "paramterClass";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getTestClassName()).willReturn(testClassName);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(cutDescriptor.getTypeName()).willReturn(cutClassName);
        given(cutDescriptor.getParameterDescriptors()).willReturn(parameterDescriptors);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getGenericType()).willReturn(fieldDescriptorType);
        given(parameterDescriptor.isSubtypeOf(fieldDescriptorType)).willReturn(false);
        given(parameterDescriptor.getTypeName()).willReturn(paramterTypeName);

        cut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getTestClassName();
        verify(testDescriptor).getCollaboratorProvider();
        verify(testContext).getCutDescriptor();
        verify(cutDescriptor).getTypeName();
        verify(cutDescriptor).getParameterDescriptors();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getGenericType();
        verify(parameterDescriptor).isSubtypeOf(fieldDescriptorType);
        verify(parameterDescriptor).getTypeName();
    }

}