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

import static java.util.Optional.empty;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Sut;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class InteractionPostVerifierTest {

    InteractionPostVerifier sut;

    @Before
    public void init() {
        sut = new InteractionPostVerifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.verify(null);
    }

    @Test
    public void givenTestContextWithoutSutDescriptorVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Optional<SutDescriptor> foundSutDescriptor = empty();

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);

        sut.verify(testContext);

        verify(testContext).getSutDescriptor();
        verifyNoMoreInteractions(testContext);
    }

    @Test
    public void givenTestContextWithUnverifiedSutDescriptorVerifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Sut sutAnnotation = mock(Sut.class);

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getSut()).willReturn(sutAnnotation);
        given(sutAnnotation.verify()).willReturn(false);

        sut.verify(testContext);

        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).getSut();
        verify(sutAnnotation).verify();
        verifyNoMoreInteractions(testContext, sutDescriptor, sutAnnotation);
    }

    @Test
    public void givenTestContextWithVerifiedSutDescriptorAndEmptyFieldDescriptorsVerifyShouldPerformVerification() {
        TestContext testContext = mock(TestContext.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Sut sutAnnotation = mock(Sut.class);
        Object testInstance = mock(Object.class);
        MockProvider mockProvider = mock(MockProvider.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of();

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getSut()).willReturn(sutAnnotation);
        given(sutAnnotation.verify()).willReturn(true);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        sut.verify(testContext);

        verify(testContext).getSutDescriptor();
    }

    @Test
    public void givenTestContextWithVerifiedSutDescriptorAndNonEmptyFieldDescriptorsVerifyShouldPerformVerification() {
        TestContext testContext = mock(TestContext.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Sut sutAnnotation = mock(Sut.class);
        Object testInstance = mock(Object.class);
        MockProvider mockProvider = mock(MockProvider.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Object value = mock(Object.class);
        Optional<Object> foundValue = Optional.of(value);

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getSut()).willReturn(sutAnnotation);
        given(sutAnnotation.verify()).willReturn(true);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.isMock()).willReturn(true);
        given(fieldDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(mockProvider.isMock(value)).willReturn(true);

        sut.verify(testContext);

        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).getSut();
        verify(sutAnnotation).verify();
        verify(testContext).getTestInstance();
        verify(testContext).getMockProvider();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).isMock();
        verify(fieldDescriptor).getValue(testInstance);
        verify(mockProvider).isMock(value);
        verify(mockProvider).verifyAllInteraction(value);
    }
}
