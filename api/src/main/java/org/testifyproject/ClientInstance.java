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
 * A contract that defines methods for retrieving information about a client and
 * communicating with web resources running inside of the server.
 *
 * @author saden
 * @param <T> the target type
 */
public interface ClientInstance<T> {

    /**
     * Get the base URI used by the client to communicate with the server.
     *
     * @return the base server URI used by the client.
     */
    URI getBaseURI();

    /**
     * Get a client instance that can be used to communicate with web resources
     * running inside of the server.
     *
     * @return a client instance.
     */
    T getClient();

    /**
     * Get the name of client instance. If present it represents a qualifier for
     * the client instance.
     *
     * @return optional with client name, empty optional otherwise
     */
    default Optional<String> getName() {
        return empty();
    }

    /**
     * The contract implemented by the client instance. If present any existing
     * implementations of the contract in the dependency injection framework
     * will be replaced by the client instance returned by {@link #getClient()}.
     *
     * @return optional with client contract type, empty optional otherwise
     */
    default Optional<Class<? extends T>> getContract() {
        return empty();
    }
}
