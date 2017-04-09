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
import org.testifyproject.ClientInstance;

/**
 * Default implementation of {@link ClientInstance} contract.
 *
 * @author saden
 * @param <T> the client type
 */
@ToString
@EqualsAndHashCode
public class DefaultClientInstance<T> implements ClientInstance<T> {

    private final T instance;
    private final Class<? extends T> contract;

    DefaultClientInstance(T client, Class<? extends T> contract) {
        this.instance = client;
        this.contract = contract;
    }

    /**
     * Create a new client client instance from the given parameters.
     *
     * @param <T> the client type
     * @param instance the underlying client instance
     * @return a client instance
     */
    public static <T> ClientInstance of(T instance) {
        return new DefaultClientInstance(instance, null);
    }

    /**
     * Create a new client client instance from the given parameters. Note that the name associated
     * with the client will be derived from the client implementation's
     * {@link Class#getSimpleName() simple class name}.
     *
     * @param <T> the client type
     * @param instance the underlying client instance
     * @param contract the contract implemented by the client
     * @return a client instance
     */
    public static <T> ClientInstance of(T instance, Class<? extends T> contract) {
        return new DefaultClientInstance(instance, contract);
    }

    @Override
    public T getInstance() {
        return instance;
    }

    @Override
    public Optional<Class<? extends T>> getContract() {
        return ofNullable(contract);
    }

}
