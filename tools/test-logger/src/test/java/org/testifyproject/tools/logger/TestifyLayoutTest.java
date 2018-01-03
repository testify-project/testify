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
package org.testifyproject.tools.logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.spy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.MDC;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 *
 * @author saden
 */
public class TestifyLayoutTest {

    Logger cut;

    ConsoleAppender appender;
    LayoutWrappingEncoder encoder;
    TestifyLayout layout;
    ResultCaptor<String> resultCaptor;

    public class ResultCaptor<T> implements Answer {

        private T result = null;

        public T getResult() {
            return result;
        }

        @Override
        public T answer(InvocationOnMock invocationOnMock) throws Throwable {
            result = (T) invocationOnMock.callRealMethod();

            return result;
        }
    }

    @Before
    public void init() {
        LoggerContext loggerContext = new LoggerContext();
        loggerContext.reset(); // we are not interested in auto-configuration
        cut = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        layout = spy(new TestifyLayout());
        resultCaptor = new ResultCaptor<>();
        willAnswer(resultCaptor).given(layout).doLayout(any(ILoggingEvent.class));
        layout.start();

        encoder = new LayoutWrappingEncoder();
        encoder.setLayout(layout);
        encoder.start();

        appender = new ConsoleAppender();
        appender.setEncoder(encoder);
        appender.start();

        cut.addAppender(appender);
    }

    @After
    public void destroy() {
        layout.stop();
        encoder.stop();
        appender.stop();
    }

    @Test
    public void givenMessageWithoutContextLogInfoShouldPrintMessageWithoutContext() {
        String message = "Hello";
        String messageFormat = message + "{}";
        String arg = "World";
        String test = "TestClass";
        String method = "TestMethod";

        cut.info(messageFormat, arg);

        String result = resultCaptor.getResult();

        assertThat(result)
                .contains(message + arg)
                .doesNotContain(test)
                .doesNotContain(method);
    }

    @Test
    public void givenMessageWithContextLogInfoShouldPrintMessageWithContext() {
        String message = "Hello";
        String messageFormat = message + "{}";
        String arg = "World";
        String test = "TestClass";
        String method = "TestMethod";

        MDC.put("test", test);
        MDC.put("method", method);

        cut.info(messageFormat, arg);

        String result = resultCaptor.getResult();

        assertThat(result).contains(message + arg, test, method);
    }

}
