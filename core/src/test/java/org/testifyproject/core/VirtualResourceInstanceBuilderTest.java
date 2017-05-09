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
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class VirtualResourceInstanceBuilderTest {

    VirtualResourceInstanceBuilder sut;

    @Before
    public void init() {
        sut = VirtualResourceInstanceBuilder.builder();
    }

    @Test
    public void givenNameCallToNameShouldSetName() {
        String name = "containerName";

        VirtualResourceInstance result = sut.name(name).build();

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    public void givenAddressCallToNameShouldSetName() {
        InetAddress address = mock(InetAddress.class);

        VirtualResourceInstance result = sut.address(address).build();

        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo(address);
    }

    @Test
    public void givenHostPortAndLocalPortCallToMappedPortShouldAddPortMapping() {
        Integer hostPort = 1;
        Integer localPort = 1;

        VirtualResourceInstance result = sut.mappedPort(hostPort, localPort).build();

        assertThat(result).isNotNull();
        assertThat(result.getMappedPorts()).containsEntry(hostPort, localPort);
    }

    @Test
    public void givenMappedPortsCallToMappedPortsShouldAddPortMappings() {
        Integer hostPort = 1;
        Integer localPort = 1;
        Map<Integer, Integer> mappedPorts = ImmutableMap.of(hostPort, localPort);

        VirtualResourceInstance result = sut.mappedPorts(mappedPorts).build();

        assertThat(result).isNotNull();
        assertThat(result.getMappedPorts()).containsEntry(hostPort, localPort);
    }

    @Test
    public void givenNameAndValueCallToPropertyShouldAddProperty() {
        String name = "name";
        String value = "value";

        VirtualResourceInstance result = sut.property(name, value).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

    @Test
    public void givenPropertiesCallToPropertiesShouldAddProperties() {
        String name = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(name, value);

        VirtualResourceInstance result = sut.properties(properties).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }
}
