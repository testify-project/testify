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

import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testifyproject.ClientInstance;

/**
 * Default implementation of {@link ClientInstance} contract.
 *
 * @author saden
 * @param <T> the client type
 */
public class DefaultClientInstance<T> implements ClientInstance<T> {

    private final T client;
    private final Class<? extends T> contract;

    /**
     * Create a new client client instance from the given parameters.
     *
     * @param <T> the client type
     * @param client the underlying client instance
     * @return a client instance
     */
    public static <T> ClientInstance of(T client) {
        return new DefaultClientInstance(client, null);
    }

    /**
     * Create a new client client instance from the given parameters. Note that
     * the name associated with the client will be derived from the client
     * implementation's {@link Class#getSimpleName() simple class name}.
     *
     * @param <T> the client type
     * @param client the underlying client instance
     * @param contract the contract implemented by the client
     * @return a client instance
     */
    public static <T> ClientInstance of(T client, Class<? extends T> contract) {
        return new DefaultClientInstance(client, contract);
    }

    DefaultClientInstance(T client, Class<? extends T> contract) {
        this.client = client;
        this.contract = contract;
    }

    @Override
    public T getInstance() {
        return client;
    }

    @Override
    public Optional<Class<? extends T>> getContract() {
        return ofNullable(contract);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.client);
        hash = 17 * hash + Objects.hashCode(this.contract);
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
        final DefaultClientInstance<?> other = (DefaultClientInstance<?>) obj;
        if (!Objects.equals(this.client, other.client)) {
            return false;
        }
        return Objects.equals(this.contract, other.contract);
    }

    @Override
    public String toString() {
        return "DefaultClientInstance{" + "client=" + client + ", contract=" + contract + '}';
    }

}
