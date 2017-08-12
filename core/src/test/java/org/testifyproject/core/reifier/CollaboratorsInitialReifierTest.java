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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.Sut;
import org.testifyproject.fixture.analyzer.AnalyzedSutClass;
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
    public void callToReifyWithoutFactoryMethodShouldProcessFields() {
        TestContext testContext = mock(TestContext.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Sut annotation = mock(Sut.class);
        String factoryMethodName = "";
        Object sutValue = new Object();
        Optional<Object> foundSutValue = Optional.of(sutValue);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getSut()).willReturn(annotation);
        given(annotation.factoryMethod()).willReturn(factoryMethodName);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundSutValue);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        willDoNothing().given(sut).processFields(testDescriptor, sutDescriptor, mockProvider, sutValue, testInstance);

        sut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).getSut();
        verify(annotation).factoryMethod();
        verify(sutDescriptor).getValue(testInstance);
        verify(testContext).getTestDescriptor();
    }

    @Test
    public void callToReifyWithFactoryMethodShouldProcessFields() {
        TestContext testContext = mock(TestContext.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Sut annotation = mock(Sut.class);
        String factoryMethodName = "factoryMethod";
        Object sutValue = new Object();
        Optional<Object> foundSutValue = Optional.of(sutValue);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getSut()).willReturn(annotation);
        given(annotation.factoryMethod()).willReturn(factoryMethodName);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundSutValue);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        willDoNothing().given(sut).processFactoryMethod(factoryMethodName, testDescriptor, sutDescriptor, mockProvider, sutValue, testInstance);

        sut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).getSut();
        verify(annotation).factoryMethod();
        verify(sutDescriptor).getValue(testInstance);
        verify(testContext).getTestDescriptor();
    }

    @Test
    public void givenArrayOfCollaboratorsProcessFactoryMethodShouldCreateSut() throws NoSuchMethodException {
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        String factoryMethodName = "builder";
        Method method = AnalyzedSutClass.class.getMethod("builder", Map.class);
        Optional<Method> foundMethod = Optional.of(method);
        Object sutValue = new Object();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of(collaboratorProvider);
        Map collaborator = mock(Map.class);
        Object[] collaborators = {collaborator};
        Optional<Object> foundCollaborators = Optional.of(collaborators);
        MockProvider mockProvider = mock(MockProvider.class);

        willReturn(foundMethod).given(sutDescriptor).findMethod(factoryMethodName);
        willReturn(collaboratorProviders).given(testDescriptor).getCollaboratorProviders();
        willReturn(true).given(collaboratorProvider).hasReturnType(Object[].class);
        willReturn(foundCollaborators).given(sut).getCollaborators(collaboratorProvider, testInstance);
        willReturn(collaborators).given(sut).convertToArray(collaborators);
        willReturn(Map.class).given(sut).getCollaboratorType(mockProvider, collaborator);

        sut.processFactoryMethod(factoryMethodName,
                testDescriptor,
                sutDescriptor,
                mockProvider,
                sutValue,
                testInstance);

        verify(sutDescriptor).findMethod(factoryMethodName);
        verify(testDescriptor).getCollaboratorProviders();
        verify(collaboratorProvider).hasReturnType(Object[].class);
    }

    @Test
    public void givenCollectionOfCollaboratorsProcessFactoryMethodShouldCreateSut() throws NoSuchMethodException {
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        String factoryMethodName = "builder";
        Method method = AnalyzedSutClass.class.getMethod("builder", Map.class);
        Optional<Method> foundMethod = Optional.of(method);
        Object sutValue = new Object();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of(collaboratorProvider);
        Map collaborator = mock(Map.class);
        Collection collaborators = ImmutableList.of(collaborator);
        Object[] collaboratorsArray = collaborators.toArray();
        Optional<Object> foundCollaborators = Optional.of(collaborators);
        MockProvider mockProvider = mock(MockProvider.class);

        willReturn(foundMethod).given(sutDescriptor).findMethod(factoryMethodName);
        willReturn(collaboratorProviders).given(testDescriptor).getCollaboratorProviders();
        willReturn(true).given(collaboratorProvider).hasReturnType(Collection.class);
        willReturn(foundCollaborators).given(sut).getCollaborators(collaboratorProvider, testInstance);
        willReturn(collaboratorsArray).given(sut).convertToArray(collaborators);
        willReturn(Map.class).given(sut).getCollaboratorType(mockProvider, collaborator);

        sut.processFactoryMethod(factoryMethodName,
                testDescriptor,
                sutDescriptor,
                mockProvider,
                sutValue,
                testInstance);

        verify(sutDescriptor).findMethod(factoryMethodName);
        verify(testDescriptor).getCollaboratorProviders();
        verify(collaboratorProvider).hasReturnType(Collection.class);
    }

    @Test
    public void givenIndividualCollaboratorProcessFactoryMethodShouldCreateSut() throws NoSuchMethodException {
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        String factoryMethodName = "builder";
        Method method = AnalyzedSutClass.class.getMethod("builder", Map.class);
        Optional<Method> foundMethod = Optional.of(method);
        Object sutValue = new AnalyzedSutClass();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of(collaboratorProvider);
        Map collaborator = mock(Map.class);
        Optional<Object> foundCollaborators = Optional.of(collaborator);
        MockProvider mockProvider = mock(MockProvider.class);
        MethodDescriptor collaboratorMethodDescriptor = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorMethodDescriptor = Optional.of(collaboratorMethodDescriptor);

        willReturn(foundMethod).given(sutDescriptor).findMethod(factoryMethodName);
        willReturn(collaboratorProviders).given(testDescriptor).getCollaboratorProviders();
        willReturn(false).given(collaboratorProvider).hasReturnType(any());
        given(testDescriptor.findCollaboratorProvider(Map.class)).willReturn(foundCollaboratorMethodDescriptor);
        willReturn(foundCollaborators).given(sut).getCollaborators(collaboratorMethodDescriptor, testInstance);

        sut.processFactoryMethod(factoryMethodName,
                testDescriptor,
                sutDescriptor,
                mockProvider,
                sutValue,
                testInstance);

        verify(sutDescriptor).findMethod(factoryMethodName);
        verify(testDescriptor).getCollaboratorProviders();
        verify(collaboratorProvider, times(2)).hasReturnType(any());
    }

    @Test(expected = TestifyException.class)
    public void givenNoInvidualCollaboratorsProcessFactoryMethodShouldCreateSut() throws NoSuchMethodException {
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        String factoryMethodName = "builder";
        Method method = AnalyzedSutClass.class.getMethod("builder", Map.class);
        Optional<Method> foundMethod = Optional.of(method);
        Object sutValue = new AnalyzedSutClass();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of(collaboratorProvider);
        Map collaborator = mock(Map.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Optional<MethodDescriptor> foundCollaboratorMethodDescriptor = Optional.empty();

        willReturn(foundMethod).given(sutDescriptor).findMethod(factoryMethodName);
        willReturn(collaboratorProviders).given(testDescriptor).getCollaboratorProviders();
        willReturn(false).given(collaboratorProvider).hasReturnType(any());
        given(testDescriptor.findCollaboratorProvider(Map.class)).willReturn(foundCollaboratorMethodDescriptor);

        sut.processFactoryMethod(factoryMethodName,
                testDescriptor,
                sutDescriptor,
                mockProvider,
                sutValue,
                testInstance);

        verify(sutDescriptor).findMethod(factoryMethodName);
        verify(testDescriptor).getCollaboratorProviders();
        verify(collaboratorProvider, times(2)).hasReturnType(any());
    }

    @Test
    public void givenCollaboratorProvidersProcessFieldsShouldReifySutFields() throws NoSuchMethodException {
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutValue = new AnalyzedSutClass();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        List<MethodDescriptor> collaboratorProviders = ImmutableList.of(collaboratorProvider);
        Map collaborator = mock(Map.class);
        Optional<Object> foundCollaborators = Optional.of(collaborator);
        Object[] collaborators = {collaborator};
        MockProvider mockProvider = mock(MockProvider.class);

        willReturn(collaboratorProviders).given(testDescriptor).getCollaboratorProviders();
        willReturn(true).given(collaboratorProvider).hasReturnType(any());
        willReturn(foundCollaborators).given(sut).getCollaborators(collaboratorProvider, testInstance);
        willReturn(collaborators).given(sut).convertToArray(collaborator);
        willDoNothing().given(sut).processCollaborators(mockProvider, sutDescriptor, sutValue, collaborators);

        sut.processFields(testDescriptor, sutDescriptor, mockProvider, sutValue, testInstance);

        verify(testDescriptor).getCollaboratorProviders();
        verify(collaboratorProvider).hasReturnType(any());
    }

    @Test
    public void givenNullCollaboratorsProcessCollaboratorsShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        MockProvider mockProvider = mock(MockProvider.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutValue = new Object();
        Object[] collaborators = {null};

        sut.processCollaborators(mockProvider, sutDescriptor, sutValue, collaborators);

        verifyZeroInteractions(testContext, sutDescriptor);
    }

    @Test
    public void givenNoCollaboratorsProcessCollaboratorsShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        MockProvider mockProvider = mock(MockProvider.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutValue = new Object();
        Object[] collaborators = {};

        sut.processCollaborators(mockProvider, sutDescriptor, sutValue, collaborators);

        verifyZeroInteractions(testContext, sutDescriptor);
    }

    @Test
    public void givenMockCollaboratorProcessCollaboratorsShouldProcessMockCollaborator() {
        MockProvider mockProvider = mock(MockProvider.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Object sutValue = new Object();
        Object collaborator = mock(Supplier.class);
        Type collaboratorType = Supplier.class;
        Object[] collaborators = {collaborator};
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Optional<FieldDescriptor> foundFieldDescriptor = Optional.of(fieldDescriptor);

        given(mockProvider.isMock(collaborator)).willReturn(true);
        given(sutDescriptor.findFieldDescriptor(collaboratorType)).willReturn(foundFieldDescriptor);

        sut.processCollaborators(mockProvider, sutDescriptor, sutValue, collaborators);

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

    @Test
    public void givenInvalidValueConvertToArrayShouldReturnArray() {
        Object value = new Object();

        Object[] result = sut.convertToArray(value);
        assertThat(result).containsExactly(value);
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
