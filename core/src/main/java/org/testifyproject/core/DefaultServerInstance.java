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
import org.testifyproject.ServerInstance;

/**
 * Default implementation of {@link ServerInstance} contract.
 *
 * @author saden
 * @param <T> the underlying server type
 */
public class DefaultServerInstance<T> implements ServerInstance<T> {

    private final URI baseURI;
    private final T server;
    private final String name;
    private final Class<? extends T> contract;

    /**
     * Create a server instance with the given base URI and test context.
     *
     * @param <T> the underlying server type
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(URI baseURI, T server) {
        return new DefaultServerInstance<>(baseURI, server, null, null);
    }

    /**
     * Create a server instance with the given base URI and test context.
     *
     * @param <T> the underlying server type
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @param name the name associated with the server
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(URI baseURI, T server, String name) {
        return new DefaultServerInstance<>(baseURI, server, name, null);
    }

    /**
     * Create a server instance with the given base URI and test context. Note
     * that the name associated with the server will be derived from the server
     * implementation's {@link Class#getSimpleName() simple class name}.
     *
     * @param <T> the underlying server type
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @param contract the contract implemented by the server
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(URI baseURI, T server, Class<? extends T> contract) {
        return new DefaultServerInstance<>(baseURI, server, null, contract);
    }

    /**
     * Create a server instance with the given base URI and test context.
     *
     * @param <T> the underlying server type
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @param name the name associated with the server
     * @param contract the contract implemented by the server
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(URI baseURI, T server, String name, Class<? extends T> contract) {
        return new DefaultServerInstance<>(baseURI, server, name, contract);
    }

    DefaultServerInstance(URI baseURI, T server, String name, Class<? extends T> contract) {
        this.baseURI = baseURI;
        this.server = server;
        this.name = name;
        this.contract = contract;
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public T getServer() {
        return server;
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
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.baseURI);
        hash = 23 * hash + Objects.hashCode(this.server);
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.contract);
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
        final DefaultServerInstance<?> other = (DefaultServerInstance<?>) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.baseURI, other.baseURI)) {
            return false;
        }
        if (!Objects.equals(this.server, other.server)) {
            return false;
        }
        return Objects.equals(this.contract, other.contract);
    }

    @Override
    public String toString() {
        return "DefaultServerInstance{"
                + "baseURI=" + baseURI
                + ", server=" + server
                + ", name=" + name
                + ", contract=" + contract
                + '}';
    }

}
