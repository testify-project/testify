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

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.MockProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.core.TestContextProperties;

/**
 *
 * @author saden
 */
public class SutTestReifierTest {

    SutTestReifier sut;

    @Before
    public void init() {
        sut = new SutTestReifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        sut.reify(null);
    }

    @Test
    public void givenEmptySutDescriptorReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.empty();

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);

        sut.reify(testContext);

        verify(testContext).getSutDescriptor();
    }

    @Test
    public void givenSutDescriptorWithoutValueReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Optional<Object> foundValue = Optional.empty();

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundValue);

        sut.reify(testContext);

        verify(testContext).getSutDescriptor();
        verify(testContext).getTestInstance();
        verify(sutDescriptor).getValue(testInstance);
    }

    @Test
    public void givenSutDesriptorWithValueReifyShouldSetSutValue() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Object sutValue = new Object();
        Optional<Object> foundValue = Optional.of(sutValue);

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(sutDescriptor.isVirtualSut()).willReturn(false);

        sut.reify(testContext);

        verify(testContext).getSutDescriptor();
        verify(testContext).getTestInstance();
        verify(sutDescriptor).getValue(testInstance);
        verify(sutDescriptor).init(testInstance);
        verify(testContext).addProperty(TestContextProperties.SUT_INSTANCE, sutValue);
    }

    @Test
    public void givenVirtualSutDescriptorWithValueReifyShouldSetSutField() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Object sutValue = new Object();
        Optional<Object> foundValue = Optional.of(sutValue);
        MockProvider mockProvider = mock(MockProvider.class);
        Class sutType = Object.class;

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(sutDescriptor.isVirtualSut()).willReturn(true);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(sutDescriptor.getType()).willReturn(sutType);
        given(mockProvider.createVirtual(sutType, sutValue)).willReturn(sutValue);

        sut.reify(testContext);

        verify(testContext).getSutDescriptor();
        verify(testContext).getTestInstance();
        verify(sutDescriptor).getValue(testInstance);
        verify(sutDescriptor).isVirtualSut();
        verify(testContext).getMockProvider();
        verify(sutDescriptor).getType();
        verify(mockProvider).createVirtual(sutType, sutValue);
        verify(sutDescriptor).setValue(testInstance, sutValue);
        verify(sutDescriptor).init(testInstance);
        verify(testContext).addProperty(TestContextProperties.SUT_INSTANCE, sutValue);
    }
}
