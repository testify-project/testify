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
package org.testify;

import java.util.Optional;

/**
 * A contract that defines methods for creating and getting information about a
 * resource instance.
 *
 * @author saden
 * @param <S> the server type
 * @param <C> the client type
 * @see ResourceProvider
 */
public interface ResourceInstance<S, C> {

    /**
     * Find property associated with the resource with the given name.
     *
     * @param <T> the property type
     * @param name the property name
     * @return an optional with property value, empty optional otherwise
     */
    <T> Optional<T> findProperty(String name);

    /**
     * Get the client instance associated with the resource.
     *
     * @return an optional with client, empty optional otherwise
     */
    Optional<Instance<C>> getClient();

    /**
     * Get the server instance associated with the resource.
     *
     * @return the server instance associated with the resource
     */
    Instance<S> getServer();

}
