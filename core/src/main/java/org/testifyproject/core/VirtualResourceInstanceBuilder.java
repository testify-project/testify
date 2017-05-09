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

import java.net.InetAddress;
import java.util.Map;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 * A builder class used to construction VirtualResourceInstance instances.
 *
 * @author saden
 * @see VirtualResourceInstance
 */
public class VirtualResourceInstanceBuilder {

    private String name;
    private InetAddress address;
    private final ImmutableMap.Builder<Integer, Integer> mappedPorts = ImmutableMap.builder();
    private final ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();

    /**
     * Create a new resource of VirtualResourceInstanceBuilder.
     *
     * @return a new resource
     */
    public static VirtualResourceInstanceBuilder builder() {
        return new VirtualResourceInstanceBuilder();
    }

    /**
     * Set the name of the virtual resource.
     *
     * @param name the name of the virtual resource
     * @return this object
     */
    public VirtualResourceInstanceBuilder name(String name) {
        this.name = name;

        return this;
    }

    /**
     * Set the address of the virtual resource.
     *
     * @param address the name of the virtual resource
     * @return this object
     */
    public VirtualResourceInstanceBuilder address(InetAddress address) {
        this.address = address;

        return this;
    }

    /**
     * Associate the specified value with the specified name in the resource
     * resource.
     *
     * @param hostPort the virtual resource host port
     * @param localPort the localhost port
     * @return this object
     */
    public VirtualResourceInstanceBuilder mappedPort(Integer hostPort, Integer localPort) {
        this.mappedPorts.put(hostPort, localPort);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param mappedPorts a map that contains mapped ports
     * @return this object
     */
    public VirtualResourceInstanceBuilder mappedPorts(Map<Integer, Integer> mappedPorts) {
        this.mappedPorts.putAll(mappedPorts);

        return this;
    }

    /**
     * Associate the specified value with the specified name in the resource
     * resource.
     *
     * @param name the name with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return this object
     */
    public VirtualResourceInstanceBuilder property(String name, Object value) {
        this.properties.put(name, value);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public VirtualResourceInstanceBuilder properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a virtual resource instance based on the builder state.
     *
     * @return a virtual resource instance
     */
    public VirtualResourceInstance build() {
        return DefaultVirtualResourceInstance.of(name,
                address,
                mappedPorts.build(),
                properties.build());
    }

}
