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
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class CollaboratorProviderPreVerifierTest {

    CollaboratorProviderPreVerifier sut;

    @Before
    public void init() {
        sut = new CollaboratorProviderPreVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test
    public void givenNoCollaboratorProviderVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProviders()).willReturn(collaboratorProviders);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getCollaboratorProviders();

        verifyNoMoreInteractions(testContext, testDescriptor);
    }

    @Test
    public void givenValidCollaboratorProviderMethodsVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of(collaboratorProvider);

        List<Class> parameterTypes = ImmutableList.of();
        String name = "name";
        String declaringClassName = "declaringClassName";
        Class returnType = Object.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProviders()).willReturn(collaboratorProviders);
        given(collaboratorProvider.getParameterTypes()).willReturn(parameterTypes);
        given(collaboratorProvider.getName()).willReturn(name);
        given(collaboratorProvider.getDeclaringClassName()).willReturn(declaringClassName);
        given(collaboratorProvider.getReturnType()).willReturn(returnType);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getCollaboratorProviders();
        verify(collaboratorProvider).getParameterTypes();
        verify(collaboratorProvider).getName();
        verify(collaboratorProvider).getDeclaringClassName();
        verify(collaboratorProvider).getReturnType();

        verifyNoMoreInteractions(testContext, testDescriptor, collaboratorProvider);
    }

    @Test(expected = TestifyException.class)
    public void givenCollaboratorProviderMethodThatReturnsVoidVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of(collaboratorProvider);

        List<Class> parameterTypes = ImmutableList.of();
        String name = "name";
        String declaringClassName = "declaringClassName";
        Class returnType = Void.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProviders()).willReturn(collaboratorProviders);
        given(collaboratorProvider.getParameterTypes()).willReturn(parameterTypes);
        given(collaboratorProvider.getName()).willReturn(name);
        given(collaboratorProvider.getDeclaringClassName()).willReturn(declaringClassName);
        given(collaboratorProvider.getReturnType()).willReturn(returnType);

        try {
            sut.verify(testContext);
        } catch (TestifyException e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getCollaboratorProviders();
            verify(collaboratorProvider).getParameterTypes();
            verify(collaboratorProvider).getName();
            verify(collaboratorProvider).getDeclaringClassName();
            verify(collaboratorProvider).getReturnType();

            verifyNoMoreInteractions(testContext, testDescriptor, collaboratorProvider);
            throw e;
        }
    }
}
