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
package org.testifyproject.core.analyzer.inspector;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.core.analyzer.TestDescriptorProperties;
import org.testifyproject.fixture.inspector.TestCollaboratorProvider;
import org.testifyproject.fixture.inspector.TestCompositCollaboratorProvider;
import org.testifyproject.fixture.inspector.TestEmptyCollaboratorProvider;

/**
 *
 * @author saden
 */
public class CollaboratorProviderInspectorTest {

    CollaboratorProviderInspector sut;

    @Before
    public void init() {
        sut = new CollaboratorProviderInspector();
    }

    @Test(expected = TestifyException.class)
    public void givenEmptyCollaboratorProviderValueInspectShouldThrowException() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        CollaboratorProvider annotation = mock(CollaboratorProvider.class);
        Class[] providers = {};

        given(annotation.value()).willReturn(providers);

        sut.inspect(testDescriptor, annotatedType, annotation);
    }

    @Test
    public void givenCollaboratorProviderWithoutMethodsInspectShouldNotAddCollaboratorProviders() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        CollaboratorProvider annotation = mock(CollaboratorProvider.class);
        Class[] providers = {TestEmptyCollaboratorProvider.class};

        given(annotation.value()).willReturn(providers);

        sut.inspect(testDescriptor, annotatedType, annotation);

        verify(testDescriptor).addProperty(TestDescriptorProperties.COLLABORATOR_PROVIDER,
                annotation);
        verifyNoMoreInteractions(testDescriptor);
    }

    @Test
    public void givenCollaboratorProviderWithMethodsInspectShouldAddCollaboratorProviders() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        CollaboratorProvider annotation = mock(CollaboratorProvider.class);
        Class[] providers = {TestCollaboratorProvider.class};

        given(annotation.value()).willReturn(providers);

        sut.inspect(testDescriptor, annotatedType, annotation);

        verify(testDescriptor).addCollectionElement(eq(
                TestDescriptorProperties.COLLABORATOR_PROVIDERS), any(MethodDescriptor.class));
        verify(testDescriptor).addProperty(TestDescriptorProperties.COLLABORATOR_PROVIDER,
                annotation);
        verifyNoMoreInteractions(testDescriptor);
    }

    @Test
    public void givenCompositCollaboratorProviderWithMethodsInspectShouldAddCollaboratorProviders() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        CollaboratorProvider annotation = mock(CollaboratorProvider.class);
        Class[] providers = {TestCompositCollaboratorProvider.class};

        given(annotation.value()).willReturn(providers);

        sut.inspect(testDescriptor, annotatedType, annotation);

        verify(testDescriptor, times(2)).addCollectionElement(eq(
                TestDescriptorProperties.COLLABORATOR_PROVIDERS), any(MethodDescriptor.class));
        verify(testDescriptor, times(2)).addProperty(eq(
                TestDescriptorProperties.COLLABORATOR_PROVIDER), any(CollaboratorProvider.class));
        verifyNoMoreInteractions(testDescriptor);
    }
}
