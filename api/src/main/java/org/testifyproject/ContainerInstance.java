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

import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * A contract that defines methods for getting information about a container.
 *
 * @author saden
 */
public interface ContainerInstance {

    /**
     * Get a unique name associated with the container instance.
     *
     * @return the container name
     */
    String getName();

    /**
     * Get the IP address of the container instance.
     *
     * @return the container instance address
     */
    InetAddress getAddress();

    /**
     * Get a mapping of container host ports and the local ports they map to.
     * Note that the key presents the container host port and the value the
     * local host port.
     *
     * @return an immutable map of port mappings, empty map otherwise
     */
    Map<Integer, Integer> getMappedPorts();

    /**
     * Get a list of ports exposed by the container instance.
     *
     * @return an immutable list of ports, empty list otherwise
     */
    default List<Integer> getExposedPorts() {
        return getMappedPorts().entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    /**
     * Find the first container exposed host port.
     *
     * @return optional of container host port, empty optional otherwise
     */
    default Optional<Integer> findFirstPort() {
        return getMappedPorts().entrySet()
                .stream()
                .findFirst()
                .map(Map.Entry::getKey);
    }

    /**
     * Get a URI based on the given scheme and container port.
     *
     * @param scheme the scheme name
     * @param port the port
     *
     * @return a URI
     */
    default URI getURI(String scheme, Integer port) {
        String uri = String.format(
                "%s://%s:%d",
                scheme,
                getAddress().getHostAddress(),
                port
        );

        return URI.create(uri);
    }

}
