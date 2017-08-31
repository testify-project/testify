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
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods to get information about a local resource
 * instance. A local resource instance consists of a resource, an optional
 * client that can be used to communicate with the local resource, and
 * properties associated with the local resource instance.
 *
 * @author saden
 * @param <R> the underlying local resource type
 * @param <C> the underlying local resource client type
 * @see ResourceProvider
 */
public interface LocalResourceInstance<R, C> extends PropertiesReader {

    /**
     * Get a unique fully qualified name associated with the local resource.
     *
     * @return the local resource's fully qualified name
     */
    String getFqn();

    /**
     * Get the local resource annotation associated with the local resource
     * instance.
     *
     * @return the local resource annotation
     */
    LocalResource getLocalResource();

    /**
     * Get the client instance associated with the local resource.
     *
     * @return an optional with client, empty optional otherwise
     */
    Optional<Instance<C>> getClient();

    /**
     * Get the underlying local resource instance.
     *
     * @return the underlying local resource
     */
    Instance<R> getResource();

}
