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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.VirtualResourceInstance;

/**
 * An implementation of {@link VirtualResourceInstance SPI Contract} that provides information about
 * running container.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultVirtualResourceInstance implements VirtualResourceInstance {

    private final String name;
    private final InetAddress address;
    private final Map<Integer, Integer> mappedPorts;

    DefaultVirtualResourceInstance(String name, InetAddress address, Map<Integer, Integer> mappedPorts) {
        this.name = name;
        this.address = address;
        this.mappedPorts = mappedPorts;
    }

    /**
     * Create a new container instance with the given parameters.
     *
     * @param name the name of the container
     * @param address the address of the container
     * @param mappedPorts the mappedPorts exposed by the container
     * @return a new container instance.
     */
    public static VirtualResourceInstance of(String name, InetAddress address, Map<Integer, Integer> mappedPorts) {
        return new DefaultVirtualResourceInstance(name, address, mappedPorts);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public Map<Integer, Integer> getMappedPorts() {
        return mappedPorts;
    }

}
