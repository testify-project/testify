/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.trait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * A contracts that specifies logging trait.
 *
 * @author saden
 */
public interface LoggingTrait extends ReportingTrait {

    /**
     * The logger logging messages are delegated to.
     */
    Logger LOGGER = LoggerFactory.getLogger("testify");

    /**
     * Get the underlying logger log messages are delegated to.
     *
     * @return logger instance.
     */
    default Logger getLogger() {
        return LOGGER;
    }

    /**
     * Put a diagnostic context value (the <code>value</code> parameter) as
     * identified with the <code>key</code> parameter into the current thread's
     * diagnostic context map. The <code>key</code> parameter cannot be null.
     * The <code>value</code> parameter can be null only if the underlying
     * implementation supports it.
     *
     * @param key non-null key
     * @param value value to put in the map
     */
    default void putMDC(String key, String value) {
        MDC.put(key, value);
    }

    /**
     * Remove the diagnostic context identified by the <code>key</code>
     * parameter using the underlying system's MDC implementation. The
     * <code>key</code> parameter cannot be null. This method does nothing if
     * there is no previous value associated with <code>key</code>.
     *
     * @param key non-null key
     */
    default void removeMDC(String key) {
        MDC.remove(key);
    }

    /**
     * Get the diagnostic context identified by the <code>key</code> parameter.
     * The <code>key</code> parameter cannot be null.
     *
     * <p>
     * This method delegates all work to the MDC of the underlying logging
     * system.
     *
     * @param key non-null key
     * @return the string value identified by the <code>key</code> parameter.
     */
    default String getMDC(String key) {
        return MDC.get(key);
    }

    /**
     * Clear all entries in the MDC of the underlying implementation.
     */
    default void clearMDC() {
        MDC.clear();
    }

    /**
     * Log a trace message.
     *
     * @param messageFormat log message format
     * @param args message format arguments.
     */
    default void trace(String messageFormat, Object... args) {
        String message = getFormattedMessage(messageFormat, args);
        LOGGER.debug(message);
    }

    /**
     * Log a debug message.
     *
     * @param messageFormat log message format
     * @param args message format arguments.
     */
    default void debug(String messageFormat, Object... args) {
        String message = getFormattedMessage(messageFormat, args);
        LOGGER.debug(message);
    }

    /**
     * Log an info message.
     *
     * @param messageFormat log message format
     * @param args message format arguments.
     */
    default void info(String messageFormat, Object... args) {
        String message = getFormattedMessage(messageFormat, args);

        LOGGER.info(message);
        info(message);
    }

    /**
     * Log a warning message.
     *
     * @param messageFormat log message format
     * @param args message format arguments.
     */
    default void warn(String messageFormat, Object... args) {
        String message = getFormattedMessage(messageFormat, args);

        LOGGER.warn(message);
        warn(message);
    }

    /**
     * Log an error message.
     *
     * @param messageFormat log message format
     * @param args message format arguments.
     */
    default void error(String messageFormat, Object... args) {
        String message = getFormattedMessage(messageFormat, args);

        LOGGER.error(message);
        warn(message);
    }

    /**
     * Given a message format and an array of arguments generate a formatted
     * message. Note that the last entry in the argument array can be an
     * instance of {@link Throwable}.
     *
     * @param messageFormat the message format
     * @param args message format arguments
     * @return a formatted message
     */
    default String getFormattedMessage(String messageFormat, Object... args) {
        if (args == null || args.length == 0) {
            return messageFormat;
        }

        Object lastEntry = args[args.length - 1];
        FormattingTuple formattingTuple;

        if (lastEntry instanceof Throwable) {
            Throwable throwable = (Throwable) lastEntry;

            if (args.length == 1) {
                formattingTuple = MessageFormatter.arrayFormat(messageFormat, new Object[]{}, throwable);
            } else {
                int length = args.length - 1;
                Object[] arguments = new Object[length];
                System.arraycopy(args, 0, arguments, 0, length);
                formattingTuple = MessageFormatter.arrayFormat(messageFormat, arguments, throwable);
            }
        } else {
            formattingTuple = MessageFormatter.arrayFormat(messageFormat, args);
        }

        return formattingTuple.getMessage();
    }

}
