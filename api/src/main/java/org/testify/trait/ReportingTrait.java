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

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * A contract that specifies reporting traits.
 *
 * @author saden
 */
public interface ReportingTrait {

    /**
     * Set used to store informational messages.
     */
    Set<String> INFO = new ConcurrentSkipListSet<>();
    /**
     * Set used to store error messages.
     */
    Set<String> ERRORS = new ConcurrentSkipListSet<>();
    /**
     * Set used to store warning messages.
     */
    Set<String> WARNINGS = new ConcurrentSkipListSet<>();

    /**
     * Report an informational message.
     *
     * @param message the message format
     */
    default void info(String message) {
        INFO.add(message);
    }

    /**
     * Get informational messages.
     *
     * @return a list of informational messages, empty list otherwise
     */
    default Set<String> getInformation() {
        return INFO;
    }

    /**
     * Determine if there are informational messages.
     *
     * @return true if there are informational messages, false otherwise
     */
    default boolean hasInformation() {
        return !INFO.isEmpty();
    }

    /**
     * Report an error message.
     *
     * @param message the message format
     */
    default void error(String message) {
        ERRORS.add(message);
    }

    /**
     * Get error messages.
     *
     * @return a list of error messages, empty list otherwise
     */
    default Set<String> getErrors() {
        return ERRORS;
    }

    /**
     * Determine if there are error messages.
     *
     * @return true if there are error messages, false otherwise
     */
    default boolean hasErrors() {
        return !ERRORS.isEmpty();
    }

    /**
     * Report a warning message.
     *
     * @param message the message format
     */
    default void warn(String message) {
        WARNINGS.add(message);
    }

    /**
     * Get warning messages.
     *
     * @return a list of warning messages, empty list otherwise
     */
    default Set<String> getWarnings() {
        return WARNINGS;
    }

    /**
     * Determine if there are warning messages.
     *
     * @return true if there are warning messages, false otherwise
     */
    default boolean hasWarnings() {
        return !WARNINGS.isEmpty();
    }
}
