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
public class DefaultVirtualResourceInstanceTest {

    VirtualResourceInstance sut;

    String name;
    InetAddress address;
    Map<Integer, Integer> mappedPorts;

    @Before
    public void init() {
        name = "name";
        address = mock(InetAddress.class);
        mappedPorts = ImmutableMap.of(1000, 2000);

        sut = DefaultVirtualResourceInstance.of(name, address, mappedPorts);
    }

    @Test
    public void validateSutInstance() {
        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(name);
        assertThat(sut.getAddress()).isEqualTo(address);
        assertThat(sut.getMappedPorts()).isEqualTo(mappedPorts);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        VirtualResourceInstance unequal = DefaultVirtualResourceInstance.of(null, address, mappedPorts);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        VirtualResourceInstance equal = DefaultVirtualResourceInstance.of(name, address, mappedPorts);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultVirtualResourceInstance", "name", "address", "mappedPorts");
    }

}
