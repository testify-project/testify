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

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A contract that defines an instance object. An instance represents the properties of an
 * object in the dependency injection framework and how the object is made available for
 * injection.
 *
 * @author saden
 * @param <T> the instance object type
 */
public interface Instance<T> {

    /**
     * Get the instance value.
     *
     * @return the instance value
     */
    T getValue();

    /**
     * Get the name of instance. If present it represents the service name associated with the
     * instance and may be used to qualify the service.
     *
     * @return instance name, null otherwise
     */
    default String getName() {
        return null;
    }

    /**
     * The contract implemented by the instance. If present the instance value can be injected
     * into services by this contract.
     *
     * @return instance contract type, null otherwise
     */
    default Class<? extends T> getContract() {
        return null;
    }

    /**
     * The generic contract implemented by the instance.If present the instance value can be
     * injected into services by this contract.
     *
     * @return instance contract type, null otherwise
     */
    default Type getGenericContract() {
        return null;
    }

    /**
     * Execute the given function with the {@link #getValue() } as its parameters.
     *
     * @param <R> function return type
     * @param function the function
     * @return the result of executing the function
     */
    default <R> R query(Function<T, R> function) {
        return function.apply(getValue());
    }

    /**
     * Execute a function with the {@link #getValue() } as its parameters.
     *
     * @param consumer the consumer function
     */
    default void command(Consumer<T> consumer) {
        consumer.accept(getValue());
    }

    /**
     * Atomically execute the given function with the {@link #getValue() } as its parameters.
     *
     * @param <R> function return type
     * @param function the function
     * @return the result of executing the function
     */
    default <R> R operation(Function<T, R> function) {
        synchronized (this) {
            return function.apply(getValue());
        }
    }

}
