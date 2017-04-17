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
import org.testifyproject.CutDescriptor;
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
public class DefaultCollaboratorsRefierTest {

    DefaultCollaboratorsRefier cut;

    @Before
    public void init() {
        cut = spy(new DefaultCollaboratorsRefier());
    }

    @Test
    public void callToReifyShouldReifyCollaborators() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = new Object();
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Object cutValue = new Object();
        Optional<Object> foundCutValue = Optional.of(cutValue);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);
        Object[] collaborators = {};
        Optional<Object> foundCollaborators = Optional.of(collaborators);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(cutDescriptor.getValue(testInstance)).willReturn(foundCutValue);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(cut.getCollaborators(collaboratorProvider, testInstance)).willReturn(foundCollaborators);
        given(cut.convertToArray(collaborators)).willReturn(collaborators);
        willDoNothing().given(cut).processCollaborators(testContext, cutDescriptor, cutValue, collaborators);

        cut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getCutDescriptor();
        verify(cutDescriptor).getValue(testInstance);
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getCollaboratorProvider();
    }

    @Test
    public void givenNullCollaboratorsProcessCollaboratorsShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Object cutValue = new Object();
        Object[] collaborators = {null};

        cut.processCollaborators(testContext, cutDescriptor, cutValue, collaborators);

        verifyZeroInteractions(testContext, cutDescriptor);
    }
    
    @Test
    public void givenNoCollaboratorsProcessCollaboratorsShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Object cutValue = new Object();
        Object[] collaborators = {};

        cut.processCollaborators(testContext, cutDescriptor, cutValue, collaborators);

        verifyZeroInteractions(testContext, cutDescriptor);
    }

    @Test
    public void givenMockCollaboratorProcessCollaboratorsShouldProcessMockCollaborator() {
        TestContext testContext = mock(TestContext.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Object cutValue = new Object();
        Object collaborator = mock(Supplier.class);
        Type collaboratorType = Supplier.class;
        Object[] collaborators = {collaborator};
        MockProvider mockProvider = mock(MockProvider.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Optional<FieldDescriptor> foundFieldDescriptor = Optional.of(fieldDescriptor);

        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(mockProvider.isMock(collaborator)).willReturn(true);
        given(cutDescriptor.findFieldDescriptor(collaboratorType)).willReturn(foundFieldDescriptor);

        cut.processCollaborators(testContext, cutDescriptor, cutValue, collaborators);

        verify(testContext).getMockProvider();
        verify(mockProvider).isMock(collaborator);
        verify(cutDescriptor).findFieldDescriptor(collaboratorType);
        verify(fieldDescriptor).setValue(cutValue, collaborator);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullConvertToArrayShouldThrowException() {
        Object value = null;

        cut.convertToArray(value);
    }

    @Test
    public void givenArrayConvertToArrayShouldReturnArray() {
        Object[] value = {};

        Object[] result = cut.convertToArray(value);

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenCollectionConvertToArrayShouldReturnArray() {
        Object element = new Object();
        List value = ImmutableList.of(element);

        Object[] result = cut.convertToArray(value);

        assertThat(result).containsExactly(element);
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidValueConvertToArrayShouldReturnArray() {
        Object value = new Object();

        cut.convertToArray(value);
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

        Optional<Object> result = cut.getCollaborators(methodDescriptor, testInstance);

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

        Optional<Object> result = cut.getCollaborators(methodDescriptor, testInstance);

        assertThat(result).contains(value);
        verify(methodDescriptor).getInstance();
        verify(methodDescriptor).invoke(testInstance);
    }

}
