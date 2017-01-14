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
package org.testify.core.impl;

import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testify.Instance;

/**
 * Default implementation of {@link Instance} contract.
 *
 * @author saden
 * @param <T> the underlying instance type
 */
public class DefaultInstance<T> implements Instance<T> {

    private final T instance;
    private final String name;
    private final Class<? super T> contract;

    /**
     * Create a instance with the given instance object.
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance) {
        return new DefaultInstance(instance);
    }

    /**
     * Create a instance with the given instance object and name.
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @param name the name name associated with the object
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance, String name) {
        return new DefaultInstance(instance, name);
    }

    /**
     * Create a instance with the given instance object and contract.
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @param contract the contract implemented by the instance
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance, Class<? super T> contract) {
        return new DefaultInstance(instance, contract);
    }

    /**
     * <p>
     * Create an instance with the given instance object, name and contract.
     * </p>
     * <p>
     * Please note that if a contract is present any existing implementations of
     * the contract will be replaced by this instance.
     * </p>
     *
     * @param <T> the underlying instance type
     * @param instance the instance object
     * @param name the name name associated with the object
     * @param contract the contract implemented by the instance
     * @return returns an instance
     */
    public static <T> Instance<T> of(T instance, String name, Class<? super T> contract) {
        return new DefaultInstance(instance, name, contract);
    }

    DefaultInstance(T instance) {
        this(instance, null, null);
    }

    DefaultInstance(T instance, String name) {
        this(instance, name, null);
    }

    DefaultInstance(T instance, Class<? super T> contract) {
        this(instance, null, contract);
    }

    DefaultInstance(T instance, String name, Class<? super T> contract) {
        this.name = name;
        this.instance = instance;
        this.contract = contract;
    }

    @Override
    public T getInstance() {
        return instance;
    }

    @Override
    public Optional<String> getName() {
        return ofNullable(name);
    }

    @Override
    public Optional<Class<? super T>> getContract() {
        return ofNullable(contract);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.instance);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.contract);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultInstance<?> other = (DefaultInstance<?>) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.instance, other.instance)) {
            return false;
        }
        return Objects.equals(this.contract, other.contract);
    }

    @Override
    public String toString() {
        return "DefaultInstance{"
                + "instance=" + instance
                + ", name=" + name
                + ", contract=" + contract
                + '}';
    }

}
