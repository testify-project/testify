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

import java.util.Optional;

/**
 * A contract that defines methods to get information about a resource instance.
 * A resource instance consists of a resource, an optional client that can be used
 * to communicate with the resource, and properties associated with a resource
 * instance.
 *
 * @author saden
 * @param <R> the resource type
 * @param <C> the client type
 * @see ResourceProvider
 */
public interface ResourceInstance<R, C> {

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
     * Get the underlying resource instance resource.
     *
     * @return the underlying resource associated with the resource instance
     */
    Instance<R> getResource();

}
