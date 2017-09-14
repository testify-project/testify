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
package org.testifyproject;

/**
 * A generic testify runtime exception. This useful for converting checked exceptions into unchecked
 * runtime exception.
 *
 * @author saden
 */
public class TestifyException extends RuntimeException {

    TestifyException(String message) {
        super(message);
    }

    TestifyException(Throwable cause) {
        super(cause);
    }

    TestifyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param message the message details
     * @return a new testify exception
     */
    public static TestifyException of(String message) {
        return new TestifyException(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail cause.
     *
     * @param cause the cause of the exception
     * @return a new logged exception instance
     */
    public static TestifyException of(Throwable cause) {
        return new TestifyException(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message the message details
     * @param cause the cause of the exception
     * @return a new testify exception
     */
    public static TestifyException of(String message, Throwable cause) {
        return new TestifyException(message, cause);
    }

}
