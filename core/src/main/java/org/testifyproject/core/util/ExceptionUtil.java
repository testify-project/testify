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

import org.testifyproject.TestifyException;

/**
 * A utility class for propagating and logging exceptions.
 *
 * @author saden
 */
public class ExceptionUtil {

    public static final ExceptionUtil INSTANCE;

    static {
        INSTANCE = new ExceptionUtil(LoggingUtil.INSTANCE);
    }

    private final LoggingUtil loggingUtil;

    ExceptionUtil(LoggingUtil loggingUtil) {
        this.loggingUtil = loggingUtil;
    }

    /**
     * Propagate the given throwable as a TestifyException.
     *
     * @param cause the cause of the exception
     * @return a new TestifyException instance with the message and cause
     */
    public TestifyException propagate(Throwable cause) {
        return TestifyException.of(cause);
    }

    /**
     * Format the given message using the given args, log the error and raise
     * the throwable as a TestifyException.
     *
     * @param messageFormat the message format
     * @param cause the cause of the exception
     * @param args message format arguments
     * @return a new TestifyException instance with the message and cause
     */
    public TestifyException propagate(String messageFormat, Throwable cause, Object... args) {
        String message = loggingUtil.formatMessage(messageFormat, args);

        return TestifyException.of(message, cause);
    }

    /**
     * Format the given message using the given args and propogate a new
     * TestifyException.
     *
     * @param messageFormat the message format
     * @param args message format arguments
     * @return a new TestifyException instance with the message
     */
    public TestifyException propagate(String messageFormat, Object... args) {
        String message = loggingUtil.formatMessage(messageFormat, args);

        return TestifyException.of(message);
    }

    /**
     * Format the given message using the given args and raise the throwable as
     * a TestifyException.
     *
     * @param messageFormat the message format
     * @param args message format arguments
     */
    public void raise(String messageFormat, Object... args) {
        String message = loggingUtil.formatMessage(messageFormat, args);

        throw TestifyException.of(message);
    }

    /**
     * Format the given message using the given args and raise the throwable as
     * a TestifyException if the given condition is true.
     *
     * @param condition the condition used to determine propagation
     * @param messageFormat the message format
     * @param args message format arguments
     */
    public void raise(Boolean condition, String messageFormat, Object... args) {
        if (condition) {
            String message = loggingUtil.formatMessage(messageFormat, args);

            throw TestifyException.of(message);
        }
    }

    /**
     * Format the given message using the given args and raise the throwable as
     * a TestifyException if the given condition is true.
     *
     * @param condition the condition used to determine propagation
     * @param messageFormat the message format
     * @param throwable the cause of the exception
     * @param args message format arguments
     */
    public void raise(Boolean condition, String messageFormat, Throwable throwable, Object... args) {
        if (condition) {
            String message = loggingUtil.formatMessage(messageFormat, args);

            throw TestifyException.of(message, throwable);
        }
    }

}
