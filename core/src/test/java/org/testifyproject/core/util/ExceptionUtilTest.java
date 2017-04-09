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
package org.testifyproject.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.testifyproject.TestifyException;

/**
 *
 * @author saden
 */
public class ExceptionUtilTest {

    ExceptionUtil cut;
    LoggingUtil loggingUtil;

    @Before
    public void init() {
        loggingUtil = mock(LoggingUtil.class, delegatesTo(LoggingUtil.INSTANCE));

        cut = new ExceptionUtil(loggingUtil);
    }

    @Test
    public void givenCausePropogateShouldReturn() {
        Throwable cause = mock(Throwable.class);

        TestifyException result = cut.propagate(cause);

        assertThat(result).isNotNull();
        assertThat(result.getCause()).isEqualTo(cause);
    }

    @Test
    public void givenMessageFormatAndThrowableAndArgsPropogateShouldReturn() {
        String greeting = "Hello";
        String name = "tester";
        String messageFormat = greeting + "{}";
        Object[] args = new Object[]{name};
        Throwable cause = mock(Throwable.class);

        TestifyException result = cut.propagate(messageFormat, cause, args);

        assertThat(result).isNotNull();
        assertThat(result.getCause()).isEqualTo(cause);
        assertThat(result.getMessage()).contains(greeting, name);
        verify(loggingUtil).formatMessage(messageFormat, args);
    }
    
    @Test
    public void givenMessageFormatAndArgsRaiseShouldThrowException() {
        String greeting = "Hello";
        String name = "tester";
        String messageFormat = greeting + "{}";
        Object[] args = new Object[]{name};

        try {
            cut.raise(messageFormat, args);
        } catch (TestifyException e) {
            verify(loggingUtil).formatMessage(messageFormat, args);
        }
    }

    @Test
    public void givenFalseConditionAndMessageFormatAndArgsRaiseShouldDoNothing() {
        Boolean condition = false;
        String greeting = "Hello";
        String name = "tester";
        String messageFormat = greeting + "{}";
        Object[] args = new Object[]{name};

        cut.raise(condition, messageFormat, args);

        verifyZeroInteractions(loggingUtil);
    }

    @Test
    public void givenFalseConditionAndMessageFormatAndArgsRaiseShouldThrowException() {
        Boolean condition = true;
        String greeting = "Hello";
        String name = "tester";
        String messageFormat = greeting + "{}";
        Object[] args = new Object[]{name};

        try {
            cut.raise(condition, messageFormat, args);
        } catch (TestifyException e) {
            verify(loggingUtil).formatMessage(messageFormat, args);
        }
    }

    @Test
    public void givenFalseConditionAndMessageFormatAndCauseAndArgsRaiseShouldDoNothing() {
        Boolean condition = false;
        String greeting = "Hello";
        String name = "tester";
        String messageFormat = greeting + "{}";
        Object[] args = new Object[]{name};
        Throwable cause = mock(Throwable.class);

        cut.raise(condition, messageFormat, cause, args);

        verifyZeroInteractions(loggingUtil);
    }

    @Test
    public void givenTrueConditionAndMessageFormatAndArgsAndCauseRaiseShouldThrowException() {
        Boolean condition = true;
        String greeting = "Hello";
        String name = "tester";
        String messageFormat = greeting + "{}";
        Object[] args = new Object[]{name};
        Throwable cause = mock(Throwable.class);

        try {
            cut.raise(condition, messageFormat, cause, args);
        } catch (TestifyException e) {
            verify(loggingUtil).formatMessage(messageFormat, args);
        }
    }

}
