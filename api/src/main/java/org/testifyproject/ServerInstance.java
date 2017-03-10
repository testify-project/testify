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

import java.net.URI;
import java.util.Optional;
import static java.util.Optional.empty;

/**
 * A contract that defines methods for retrieving information about the running
 * server.
 *
 * @author saden
 * @param <T> the underlying server instance type.
 */
public interface ServerInstance<T> {

    /**
     * The server base URI.
     *
     * @return the base URI
     */
    URI getBaseURI();

    /**
     * Get the underlying server instance.
     *
     * @return the underlying server instance
     */
    T getServer();

    /**
     * Get the name of server instance. If present it represents a qualifier for
     * the server instance.
     *
     * @return optional with server name, empty optional otherwise
     */
    default Optional<String> getName() {
        return empty();
    }

    /**
     * The contract implemented by the server instance. If present any existing
     * implementations of the contract in the dependency injection framework
     * will be replaced by the server instance returned by {@link #getServer()}.
     *
     * @return optional with server contract type, empty optional otherwise
     */
    default Optional<Class<? extends T>> getContract() {
        return empty();
    }

}
