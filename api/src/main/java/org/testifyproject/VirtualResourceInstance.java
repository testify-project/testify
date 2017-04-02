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

import static java.lang.String.format;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * A contract that defines methods for getting information about a virtual
 * resource.
 *
 * @author saden
 */
public interface VirtualResourceInstance {

    /**
     * Get a unique name associated with the virtual resource.
     *
     * @return the virtual resource name
     */
    String getName();

    /**
     * Get the IP address of the virtual resource.
     *
     * @return the virtual resource address
     */
    InetAddress getAddress();

    /**
     * Get a mapping of virtual resource host ports and the local ports they map
     * to. Note that the key presents the virtual resource host port and the
     * value the local host port.
     *
     * @return an immutable map of port mappings, empty map otherwise
     */
    Map<Integer, Integer> getMappedPorts();

    /**
     * Find the first host port exposed by the virtual resource. This is a
     * convenience method for getting a port from virtual resources that expose
     * at most one port.
     *
     * @return optional with first host port exposed by virtual resource
     */
    default Optional<Integer> findFirstExposedPort() {
        return getMappedPorts().entrySet()
                .stream()
                .findFirst()
                .map(Map.Entry::getKey);
    }

    /**
     * Get a URI based on the given scheme and virtual resource port.
     *
     * @param scheme the scheme name
     * @param port the port
     *
     * @return a URI
     */
    default URI getURI(String scheme, Integer port) {
        String uri = format("%s://%s:%d", scheme, getAddress().getHostAddress(), port);

        return URI.create(uri);
    }

}
