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
import org.testifyproject.CutDescriptor;
import org.testifyproject.TestContext;

/**
 *
 * @author saden
 */
public class TestFieldCutReifierTest {

    TestFieldCutReifier cut;

    @Before
    public void init() {
        cut = new TestFieldCutReifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        cut.reify(null);
    }

    @Test
    public void givenTestContextWithoutCutDescriptorReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.empty();

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);

        cut.reify(testContext);

        verify(testContext).getCutDescriptor();
    }

    @Test
    public void givenCutDescriptorWithValueReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Object cutValue = new Object();
        Optional<Object> foundValue = Optional.of(cutValue);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(cutDescriptor.getValue(testInstance)).willReturn(foundValue);

        cut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getCutDescriptor();
        verify(cutDescriptor).getValue(testInstance);
        verify(cutDescriptor).setValue(testInstance, cutValue);
        verifyNoMoreInteractions(cutDescriptor);
    }

    @Test
    public void givenCutDescriptorWithoutValueReifyShouldSetCutField() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Class cutType = Object.class;
        Optional<Object> foundValue = Optional.empty();

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(cutDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(cutDescriptor.getType()).willReturn(cutType);

        cut.reify(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getCutDescriptor();
        verify(cutDescriptor).getType();
        verify(cutDescriptor).setValue(eq(testInstance), any(cutType));
    }

}
