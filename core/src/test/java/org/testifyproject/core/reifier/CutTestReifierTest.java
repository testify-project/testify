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
import org.testifyproject.CutDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.core.TestContextProperties;

/**
 *
 * @author saden
 */
public class CutTestReifierTest {

    CutTestReifier cut;

    @Before
    public void init() {
        cut = new CutTestReifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextVerifyShouldThrowException() {
        cut.reify(null);
    }

    @Test
    public void givenEmptyCutDescriptorReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.empty();

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);

        cut.reify(testContext);

        verify(testContext).getCutDescriptor();
    }

    @Test
    public void givenCutDescriptorWithoutValueReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Optional<Object> foundValue = Optional.empty();

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(cutDescriptor.getValue(testInstance)).willReturn(foundValue);

        cut.reify(testContext);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestInstance();
        verify(cutDescriptor).getValue(testInstance);
    }

    @Test
    public void givenCutDesriptorWithValueReifyShouldSetCutValue() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Object cutValue = new Object();
        Optional<Object> foundValue = Optional.of(cutValue);

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(cutDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(cutDescriptor.isVirtualCut()).willReturn(false);

        cut.reify(testContext);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestInstance();
        verify(cutDescriptor).getValue(testInstance);
        verify(cutDescriptor).setValue(testInstance, cutValue);
        verify(cutDescriptor).init(cutValue);
        verify(testContext).addProperty(TestContextProperties.CUT_INSTANCE, cutValue);
    }

    @Test
    public void givenVirtualCutDescriptorWithValueReifyShouldSetCutField() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = mock(Object.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
        Object cutValue = new Object();
        Optional<Object> foundValue = Optional.of(cutValue);
        MockProvider mockProvider = mock(MockProvider.class);
        Class cutType = Object.class;

        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(cutDescriptor.getValue(testInstance)).willReturn(foundValue);
        given(cutDescriptor.isVirtualCut()).willReturn(true);
        given(testContext.getMockProvider()).willReturn(mockProvider);
        given(cutDescriptor.getType()).willReturn(cutType);
        given(mockProvider.createVirtual(cutType, cutValue)).willReturn(cutValue);

        cut.reify(testContext);

        verify(testContext).getCutDescriptor();
        verify(testContext).getTestInstance();
        verify(cutDescriptor).getValue(testInstance);
        verify(cutDescriptor).isVirtualCut();
        verify(testContext).getMockProvider();
        verify(cutDescriptor).getType();
        verify(mockProvider).createVirtual(cutType, cutValue);
        verify(cutDescriptor).setValue(testInstance, cutValue);
        verify(cutDescriptor).init(cutValue);
        verify(testContext).addProperty(TestContextProperties.CUT_INSTANCE, cutValue);
    }
}
