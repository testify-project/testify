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

import java.util.Optional;
import static java.util.Optional.ofNullable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.Instance;

/**
 * Default implementation of {@link Instance} contract.
 *
 * @author saden
 * @param <T> the underlying instance type
 */
@ToString
@EqualsAndHashCode
public class DefaultInstance<T> implements Instance<T> {

    private final T instance;
    private final String name;
    private final Class<? extends T> contract;

    DefaultInstance(T instance, String name, Class<? extends T> contract) {
        this.name = name;
        this.instance = instance;
        this.contract = contract;
    }

    /**
     * Create a instance with the given instance object.
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance) {
        return new DefaultInstance(instance, null, null);
    }

    /**
     * Create a instance with the given instance object and name.
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @param name the name associated with the object
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance, String name) {
        return new DefaultInstance(instance, name, null);
    }

    /**
     * Create a instance with the given instance object and contract.
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @param contract the contract implemented by the instance
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance, Class<? extends T> contract) {
        return new DefaultInstance(instance, null, contract);
    }

    /**
     * <p>
     * Create an instance with the given instance object, name and contract.
     * </p>
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @param name the name associated with the object
     * @param contract the contract implemented by the instance
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance, String name, Class<? extends T> contract) {
        return new DefaultInstance(instance, name, contract);
    }

    @Override
    public T getValue() {
        return instance;
    }

    @Override
    public Optional<String> getName() {
        return ofNullable(name);
    }

    @Override
    public Optional<Class<? extends T>> getContract() {
        return ofNullable(contract);
    }

}
