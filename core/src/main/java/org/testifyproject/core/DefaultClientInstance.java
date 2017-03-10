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

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import org.testifyproject.ClientInstance;

/**
 * Default implementation of {@link ClientInstance} contract.
 *
 * @author saden
 * @param <T> the client type
 */
public class DefaultClientInstance<T> implements ClientInstance<T> {

    private final URI baseURI;
    private final T client;
    private final String name;
    private final Class<? extends T> contract;

    /**
     * Create a new client client instance from the given parameters.
     *
     * @param <T> the client type
     * @param baseURI base URI used by the client to communicate with the server
     * @param client the underlying client instance
     * @return a client instance
     */
    public static <T> ClientInstance of(URI baseURI, T client) {
        return new DefaultClientInstance(baseURI, client, null, null);
    }

    /**
     * Create a new client client instance from the given parameters.
     *
     * @param <T> the client type
     * @param baseURI base URI used by the client to communicate with the server
     * @param client the underlying client instance
     * @param name the name associated with the client
     * @return a client instance
     */
    public static <T> ClientInstance of(URI baseURI, T client, String name) {
        return new DefaultClientInstance(baseURI, client, name, null);
    }

    /**
     * Create a new client client instance from the given parameters. Note that
     * the name associated with the client will be derived from the client
     * implementation's {@link Class#getSimpleName() simple class name}.
     *
     * @param <T> the client type
     * @param baseURI base URI used by the client to communicate with the server
     * @param client the underlying client instance
     * @param contract the contract implemented by the client
     * @return a client instance
     */
    public static <T> ClientInstance of(URI baseURI, T client, Class<? extends T> contract) {
        return new DefaultClientInstance(baseURI, client, null, contract);
    }

    /**
     * Create a new client client instance from the given parameters.
     *
     * @param <T> the client type
     * @param baseURI base URI used by the client to communicate with the server
     * @param client the underlying client instance
     * @param name the name associated with the client
     * @param contract the contract implemented by the client
     * @return a client instance
     */
    public static <T> ClientInstance of(URI baseURI, T client, String name, Class<? extends T> contract) {
        return new DefaultClientInstance(baseURI, client, name, contract);
    }

    DefaultClientInstance(URI baseURI, T client, String name, Class<? extends T> contract) {
        this.baseURI = baseURI;
        this.client = client;
        this.name = name;
        this.contract = contract;
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public T getClient() {
        return client;
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public Optional<Class<? extends T>> getContract() {
        return Optional.ofNullable(contract);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.baseURI);
        hash = 79 * hash + Objects.hashCode(this.client);
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.contract);
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.baseURI, other.baseURI)) {
            return false;
        }
        if (!Objects.equals(this.client, other.client)) {
            return false;
        }
        return Objects.equals(this.contract, other.contract);
    }

    @Override
    public String toString() {
        return "DefaultClientInstance{"
                + "baseURI=" + baseURI
                + ", client=" + client
                + ", name=" + name
                + ", contract=" + contract
                + '}';
    }

}
