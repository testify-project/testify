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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;

/**
 *
 * @author saden
 */
public class CreateSutReifierTest {

    CreateSutReifier sut;

    @Before
    public void init() {
        sut = new CreateSutReifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextReifyShouldThrowException() {
        sut.reify(null);
    }

    @Test
    public void givenTestContextWithoutSutDescriptorReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.empty();

        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);

        sut.reify(testContext);

        verify(testContext).getSutDescriptor();
    }

    @Test
    public void givenSutDescriptorWithValueReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Object sutValue = new Object();
        Optional<Object> foundValue = Optional.of(sutValue);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundValue);

        sut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).getValue(testInstance);
        verify(sutDescriptor).setValue(testInstance, sutValue);
        verifyNoMoreInteractions(sutDescriptor);
    }

    @Test
    public void givenSutDescriptorWithoutValueReifyShouldSetSutField() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Class sutType = Object.class;
        Optional<Object> foundValue = Optional.empty();

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(sutDescriptor.getType()).willReturn(sutType);

        sut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).getType();
        verify(sutDescriptor).setValue(eq(testInstance), any(sutType));
    }

}
