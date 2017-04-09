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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.TestContext;
import slf4jtest.LogLevel;
import slf4jtest.TestLogger;
import slf4jtest.TestLoggerFactory;

/**
 *
 * @author saden
 */
public class LoggingUtilTest {

    LoggingUtil cut;
    TestLogger logger;

    @Before
    public void init() {
        TestLoggerFactory loggerFactory = new TestLoggerFactory();
        logger = loggerFactory.getLogger("testify");

        cut = new LoggingUtil(logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullKeyPutMDCShouldThrowException() {
        String key = null;
        String value = "value";

        cut.putMDC(key, value);
    }

    @Test
    public void givenKeyAndValuePutMDCShouldPutIntoMDC() {
        String key = "key";
        String value = "value";

        cut.putMDC(key, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullKeyRemoveMDCShouldThrowException() {
        String key = null;

        cut.removeMDC(key);
    }

    @Test
    public void givenKeyRemoveMDCShouldRemoveRemoveFromMDC() {
        String key = "key";

        cut.removeMDC(key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullKeyGetMDCShouldThrowException() {
        String key = null;

        cut.getMDC(key);
    }

    @Test
    public void givenKeyGetMDCShouldGetFromMDC() {
        String key = "key";

        cut.getMDC(key);
    }

    @Test
    public void callToClearMDCShouldClearMDC() {
        cut.clearMDC();
    }

    @Test
    public void callToDebugWithoutArgsShouldLogMessage() {
        String messageFormat = "debugging";

        cut.debug(messageFormat);

        assertThat(logger.contains(LogLevel.DebugLevel, messageFormat)).isTrue();
    }

    @Test
    public void callToDebugWithArgsShouldLogMessage() {
        String message = "debugging";
        String messageFormat = message + "{}";
        String arg = "message";

        cut.debug(messageFormat, new Object[]{arg});

        assertThat(logger.contains(LogLevel.DebugLevel, message + arg)).isTrue();
    }

    @Test
    public void callToInfoWithoutArgsShouldLogMessage() {
        String messageFormat = "informing";

        cut.info(messageFormat);

        assertThat(logger.contains(LogLevel.InfoLevel, messageFormat)).isTrue();
    }

    @Test
    public void callToInfoWithArgsShouldLogMessage() {
        String message = "informing";
        String messageFormat = message + "{}";
        String arg = "message";

        cut.info(messageFormat, new Object[]{arg});

        assertThat(logger.contains(LogLevel.InfoLevel, message + arg)).isTrue();
    }

    @Test
    public void callToWarnWithoutArgsShouldLogMessage() {
        String messageFormat = "warning";

        cut.warn(messageFormat);

        assertThat(logger.contains(LogLevel.WarnLevel, messageFormat)).isTrue();
    }

    @Test
    public void callToWarnWithArgsShouldLogMessage() {
        String message = "warning";
        String messageFormat = message + "{}";
        String arg = "message";

        cut.warn(messageFormat, new Object[]{arg});

        assertThat(logger.contains(LogLevel.WarnLevel, message + arg)).isTrue();
    }

    @Test
    public void callToErrorWithoutArgsShouldLogMessage() {
        String messageFormat = "erroring";

        cut.error(messageFormat);

        assertThat(logger.contains(LogLevel.ErrorLevel, messageFormat)).isTrue();
    }

    @Test
    public void callToErrorWithArgsShouldLogMessage() {
        String message = "erroring";
        String messageFormat = message + "{}";
        String arg = "message";

        cut.error(messageFormat, new Object[]{arg});

        assertThat(logger.contains(LogLevel.ErrorLevel, message + arg)).isTrue();
    }

    @Test
    public void givenNullMessageFormatFormatMessageShouldReturnNull() {
        String messageFormat = null;
        String arg = "message";

        String result = cut.formatMessage(messageFormat, new Object[]{arg});

        assertThat(result).contains("null");
    }

    @Test
    public void givenNoArgsFormatMessageShouldReturnMessage() {
        String message = "testing";

        String result = cut.formatMessage(message);

        assertThat(result).isEqualTo(message);
    }

    @Test
    public void givenMessageFormatAndArgsFormatMessageShouldReturnFormattedMessage() {
        String message = "testing";
        String messageFormat = message + "{}";
        String arg = "message";

        String result = cut.formatMessage(messageFormat, new Object[]{arg});

        assertThat(result).isEqualTo(message + arg);
    }

    @Test
    public void givenExceptionArgumentFormatMessageShouldReturnFormattedMessage() {
        String message = "testing";
        String causeMessage = "exceptionArg";
        IllegalStateException arg = new IllegalStateException(causeMessage);

        String result = cut.formatMessage(message, new Object[]{arg});

        assertThat(result).contains(message, causeMessage);
    }

    @Test
    public void givenArgumentsWithExceptionFormatMessageShouldReturnFormattedMessage() {
        String message = "testing";
        String messageFormat = message + "{}";
        String causeMessage = "exceptionArg";
        String arg0 = "message";
        IllegalStateException arg1 = new IllegalStateException(causeMessage);

        String result = cut.formatMessage(messageFormat, new Object[]{arg0, arg1});

        assertThat(result).contains(message + arg0, causeMessage);
    }

    @Test
    public void givenTestContextSetTestContextShouldSetMDC() {
        TestContext testContext = mock(TestContext.class);
        String testName = "testName";
        String methodName = "methodName";

        given(testContext.getTestName()).willReturn(testName);
        given(testContext.getMethodName()).willReturn(methodName);

        cut.setTextContext(testContext);
        
        verify(testContext).getTestName();
        verify(testContext).getMethodName();
    }

}
