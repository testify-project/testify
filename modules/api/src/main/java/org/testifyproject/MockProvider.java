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
 * An SPI contract that methods for creating mock test doubles.
 *
 * @author saden
 */
public interface MockProvider {

    /**
     * Create a fake instance of the given type.
     *
     * @param <T> the type of the object being faked
     * @param type the class of the object being faked
     * @return a faked instance
     */
    <T> T createFake(Class<? extends T> type);

    /**
     * Create a virtual instance of the given type that delegates to the given instance.
     *
     * @param <T> the type of the object being virtualized
     * @param type the class of the type being virtualized
     * @param delegate the instance calls are delegated to
     * @return a virtual instance
     */
    <T> T createVirtual(Class<? extends T> type, T delegate);

    /**
     * Create a virtual instance of the given type that delegates to the given instance.
     *
     * @param <T> the type of the object being virtualized
     * @param type the class of the type being virtualized
     * @param delegate the instance calls are delegated to
     * @return a virtual instance
     */
    default <T> T createVirtualSut(Class<? extends T> type, T delegate) {
        return createVirtual(type, delegate);
    }

    /**
     * Verify all interaction between the system under test and its collaborators.
     *
     * @param collaborators an array of collaborators that will be verified
     */
    void verifyAllInteraction(Object... collaborators);

    /**
     * Determine if the given instance is a mock instance.
     *
     * @param <T>the instance type
     * @param instance the instance
     * @return true if the instance is a mock object, false otherwise
     */
    <T> Boolean isMock(T instance);
}
