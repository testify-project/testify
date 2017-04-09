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
import java.util.Optional;
import static java.util.Optional.ofNullable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.ServerInstance;

/**
 * Default implementation of {@link ServerInstance} contract.
 *
 * @author saden
 * @param <T> the underlying server type
 */
@ToString
@EqualsAndHashCode
public class DefaultServerInstance<T> implements ServerInstance<T> {

    private final URI baseURI;
    private final T server;
    private final Class<? extends T> contract;

    DefaultServerInstance(URI baseURI, T server, Class<? extends T> contract) {
        this.baseURI = baseURI;
        this.server = server;
        this.contract = contract;
    }

    /**
     * Create a server instance with the given base URI and test context.
     *
     * @param <T> the underlying server type
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(URI baseURI, T server) {
        return new DefaultServerInstance<>(baseURI, server, null);
    }

    /**
     * Create a server instance with the given base URI and test context. Note that the name
     * associated with the server will be derived from the server implementation's
     * {@link Class#getSimpleName() simple class name}.
     *
     * @param <T> the underlying server type
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @param contract the contract implemented by the server
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(URI baseURI, T server, Class<? extends T> contract) {
        return new DefaultServerInstance<>(baseURI, server, contract);
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public T getInstance() {
        return server;
    }

    @Override
    public Optional<Class<? extends T>> getContract() {
        return ofNullable(contract);
    }

}
