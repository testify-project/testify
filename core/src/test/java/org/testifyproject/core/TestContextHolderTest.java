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
package org.testifyproject.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestContext;

/**
 *
 * @author saden
 */
public class TestContextHolderTest {

    TestContextHolder sut;
    InheritableThreadLocal<TestContext> inheritableThreadLocal;

    @Before
    public void init() {
        inheritableThreadLocal = new InheritableThreadLocal<>();

        sut = TestContextHolder.of(inheritableThreadLocal);
    }

    @Test
    public void givenTestContextSetShouldSetIheritableThreadLocal() {
        TestContext testContext = mock(TestContext.class);

        sut.set(testContext);

        assertThat(inheritableThreadLocal.get()).isEqualTo(testContext);
    }

    @Test
    public void callToGetShouldReturnTestContext() {
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        Optional<TestContext> result = sut.get();

        assertThat(result).contains(testContext);
    }

    @Test
    public void callToRemoveShouldRemoveTestContext() {
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        sut.remove();

        assertThat(inheritableThreadLocal.get()).isNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullConsumerExecuteShouldThrowException() {
        Consumer<TestContext> consumer = null;
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        sut.command(consumer);
    }

    @Test
    public void givenValidConsumerAndNoTestContextExecuteShouldDoNothing() {
        Consumer<TestContext> consumer = mock(Consumer.class);

        sut.command(consumer);

        verifyZeroInteractions(consumer);
    }

    @Test
    public void givenValidConsumerExecuteShouldCallConsumer() {
        Consumer<TestContext> consumer = mock(Consumer.class);
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        willDoNothing().given(consumer).accept(testContext);

        sut.command(consumer);

        verify(consumer).accept(testContext);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullFunctionExecuteShouldThrowException() {
        Function<TestContext, Object> function = null;
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        sut.query(function);
    }

    @Test
    public void givenValidFunctionAndNoTestContextExecuteShouldDoNothing() {
        Function<TestContext, Object> function = mock(Function.class);

        Object result = sut.query(function);

        assertThat(result).isNull();
        verifyZeroInteractions(function);
    }

    @Test
    public void givenValidFunctionExecuteShouldCallConsumer() {
        Object answer = mock(Object.class);

        Function<TestContext, Object> function = mock(Function.class);
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        given(function.apply(testContext)).willReturn(answer);

        Object result = sut.query(function);

        assertThat(result).isEqualTo(answer);
        verify(function).apply(testContext);
    }

}
