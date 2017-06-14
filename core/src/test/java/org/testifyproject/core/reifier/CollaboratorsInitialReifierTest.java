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
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.testifyproject.SutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class CollaboratorsInitialReifierTest {

    CollaboratorsInitialReifier sut;

    @Before
    public void init() {
        sut = spy(new CollaboratorsInitialReifier());
    }

    @Test
    public void callToReifyShouldReifyCollaborators() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Object sutValue = new Object();
        Optional<Object> foundSutValue = Optional.of(sutValue);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);
        Object[] collaborators = {};
        Optional<Object> foundCollaborators = Optional.of(collaborators);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundSutValue);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(sut.getCollaborators(collaboratorProvider, testInstance)).willReturn(foundCollaborators);
        given(sut.convertToArray(collaborators)).willReturn(collaborators);
        willDoNothing().given(sut).processCollaborators(testContext, sutDescriptor, sutValue, collaborators);

        sut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).getValue(testInstance);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getCollaboratorProvider();
    }

    @Test
    public void givenNullCollaboratorsProcessCollaboratorsShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutValue = new Object();
        Object[] collaborators = {null};

        sut.processCollaborators(testContext, sutDescriptor, sutValue, collaborators);

        verifyZeroInteractions(testContext, sutDescriptor);
    }
    
    @Test
    public void givenNoCollaboratorsProcessCollaboratorsShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutValue = new Object();
        Object[] collaborators = {};

        sut.processCollaborators(testContext, sutDescriptor, sutValue, collaborators);

        verifyZeroInteractions(testContext, sutDescriptor);
    }

    @Test
    public void givenMockCollaboratorProcessCollaboratorsShouldProcessMockCollaborator() {
        TestContext testContext = mock(TestContext.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutValue = new Object();
        Object collaborator = mock(Supplier.class);
        Type collaboratorType = Supplier.class;
        Object[] collaborators = {collaborator};
        MockProvider mockProvider = mock(MockProvider.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Optional<FieldDescriptor> foundFieldDescriptor = Optional.of(fieldDescriptor);

        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(mockProvider.isMock(collaborator)).willReturn(true);
        given(sutDescriptor.findFieldDescriptor(collaboratorType)).willReturn(foundFieldDescriptor);

        sut.processCollaborators(testContext, sutDescriptor, sutValue, collaborators);

        verify(testContext).getMockProvider();
        verify(mockProvider).isMock(collaborator);
        verify(sutDescriptor).findFieldDescriptor(collaboratorType);
        verify(fieldDescriptor).setValue(sutValue, collaborator);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullConvertToArrayShouldThrowException() {
        Object value = null;

        sut.convertToArray(value);
    }

    @Test
    public void givenArrayConvertToArrayShouldReturnArray() {
        Object[] value = {};

        Object[] result = sut.convertToArray(value);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenCollectionConvertToArrayShouldReturnArray() {
        Object element = new Object();
        List value = ImmutableList.of(element);

        Object[] result = sut.convertToArray(value);

        assertThat(result).containsExactly(element);
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidValueConvertToArrayShouldReturnArray() {
        Object value = new Object();

        sut.convertToArray(value);
    }

    @Test
    public void givenMethodDescriptorWithInstanceGetCollaboratorsShouldReturnCollaborators() {
        MethodDescriptor methodDescriptor = mock(MethodDescriptor.class);
        Object testInstance = new Object();
        Object instance = new Object();
        Optional<Object> foundInstance = Optional.of(instance);
        Object value = new Object();
        Optional<Object> foundValue = Optional.of(value);

        given(methodDescriptor.getInstance()).willReturn(foundInstance);
        given(methodDescriptor.invoke(instance)).willReturn(foundValue);

        Optional<Object> result = sut.getCollaborators(methodDescriptor, testInstance);

        assertThat(result).contains(value);
        verify(methodDescriptor).getInstance();
        verify(methodDescriptor).invoke(instance);
    }

    @Test
    public void givenMethodDescriptorWithoutInstanceGetCollaboratorsShouldReturnCollaborators() {
        MethodDescriptor methodDescriptor = mock(MethodDescriptor.class);
        Object testInstance = new Object();
        Optional<Object> foundInstance = Optional.empty();
        Object value = new Object();
        Optional<Object> foundValue = Optional.of(value);

        given(methodDescriptor.getInstance()).willReturn(foundInstance);
        given(methodDescriptor.invoke(testInstance)).willReturn(foundValue);

        Optional<Object> result = sut.getCollaborators(methodDescriptor, testInstance);

        assertThat(result).contains(value);
        verify(methodDescriptor).getInstance();
        verify(methodDescriptor).invoke(testInstance);
    }

}
