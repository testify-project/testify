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

import java.net.URI;
import java.util.Objects;
import org.testify.ServerInstance;

/**
 * Default implementation of {@link ServerInstance} contract.
 *
 * @author saden
 * @param <T> the underlying server type
 */
public class DefaultServerInstance<T> implements ServerInstance<T> {

    private final URI baseURI;
    private final T server;

    /**
     * Create a server instance with the given base URI and test context.
     *
     * @param <T> the underlying server type
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(URI baseURI, T server) {
        return new DefaultServerInstance<>(baseURI, server);
    }

    DefaultServerInstance(URI baseURI, T server) {
        this.baseURI = baseURI;
        this.server = server;
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
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.baseURI);
        hash = 53 * hash + Objects.hashCode(this.server);
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
        final DefaultServerInstance other = (DefaultServerInstance) obj;
        if (!Objects.equals(this.baseURI, other.baseURI)) {
            return false;
        }

        return Objects.equals(this.server, other.server);
    }

    @Override
    public String toString() {
        return "DefaultServerInstance{"
                + "baseURI=" + baseURI
                + ", server=" + server
                + '}';
    }

}
