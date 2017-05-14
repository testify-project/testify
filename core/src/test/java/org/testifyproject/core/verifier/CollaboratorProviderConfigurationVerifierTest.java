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

import java.util.Collection;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;

/**
 *
 * @author saden
 */
public class CollaboratorProviderConfigurationVerifierTest {

    CollaboratorProviderConfigurationVerifier sut;

    @Before
    public void init() {
        sut = new CollaboratorProviderConfigurationVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test
    public void givenNoCollaboratorProviderVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.empty();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getCollaboratorProvider();
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidCollaboratorProviderVerifyShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);
        Class returnType = Void.class;
        String methodName = "testMethod";
        String declaringClassName = "TestClass";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(collaboratorProvider.getReturnType()).willReturn(returnType);
        given(collaboratorProvider.getName()).willReturn(methodName);
        given(collaboratorProvider.getDeclaringClassName()).willReturn(declaringClassName);

        try {
            sut.verify(testContext);
        }
        catch (Exception e) {
            verify(testContext).getTestDescriptor();
            verify(testDescriptor).getCollaboratorProvider();
            verify(collaboratorProvider).getReturnType();
            verify(collaboratorProvider).getName();
            verify(collaboratorProvider).getDeclaringClassName();
            throw e;
        }
    }

    @Test
    public void givenCollectionCollaboratorProviderVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);
        Class returnType = Collection.class;
        String methodName = "testMethod";
        String declaringClassName = "TestClass";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(collaboratorProvider.getReturnType()).willReturn(returnType);
        given(collaboratorProvider.getName()).willReturn(methodName);
        given(collaboratorProvider.getDeclaringClassName()).willReturn(declaringClassName);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getCollaboratorProvider();
        verify(collaboratorProvider).getReturnType();
        verify(collaboratorProvider).getName();
        verify(collaboratorProvider).getDeclaringClassName();
    }
    
    @Test
    public void givenObjectArrayCollaboratorProviderVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);
        Class returnType = Object[].class;
        String methodName = "testMethod";
        String declaringClassName = "TestClass";

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(collaboratorProvider.getReturnType()).willReturn(returnType);
        given(collaboratorProvider.getName()).willReturn(methodName);
        given(collaboratorProvider.getDeclaringClassName()).willReturn(declaringClassName);

        sut.verify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getCollaboratorProvider();
        verify(collaboratorProvider).getReturnType();
        verify(collaboratorProvider).getName();
        verify(collaboratorProvider).getDeclaringClassName();
    }
}
