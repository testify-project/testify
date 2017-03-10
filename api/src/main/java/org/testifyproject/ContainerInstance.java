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
import java.util.List;
import java.util.Optional;

/**
 * A contract that defines methods for getting information about a container.
 *
 * @author saden
 */
public interface ContainerInstance {

    /**
     * Get the name of the container.
     *
     * @return the required container IP address or hostname.
     */
    String getName();

    /**
     * Get the IP address or hostname of the container.
     *
     * @return the container IP address or hostname.
     */
    String getHost();

    /**
     * Get a list containing ports exposed by the container.
     *
     * @return an immutable list containing exposed ports, empty list otherwise.
     */
    List<Integer> getPorts();

    /**
     * Find the first exposed host port.
     *
     * @return optional of host port, empty optional otherwise.
     */
    Optional<Integer> findFirstPort();

    /**
     * Get a URI based on the given scheme and container port.
     *
     * @param scheme the scheme name
     * @param containerPort the container port
     *
     * @return a URI.
     */
    URI getURI(String scheme, Integer containerPort);

}
