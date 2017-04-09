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

    VirtualResourceInstance cut;

    String name;
    InetAddress address;
    Map<Integer, Integer> mappedPorts;

    @Before
    public void init() {
        name = "name";
        address = mock(InetAddress.class);
        mappedPorts = ImmutableMap.of(1000, 2000);

        cut = DefaultVirtualResourceInstance.of(name, address, mappedPorts);
    }

    @Test
    public void validateCutInstance() {
        assertThat(cut).isNotNull();
        assertThat(cut.getName()).isEqualTo(name);
        assertThat(cut.getAddress()).isEqualTo(address);
        assertThat(cut.getMappedPorts()).isEqualTo(mappedPorts);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        VirtualResourceInstance unequal = DefaultVirtualResourceInstance.of(null, address, mappedPorts);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        VirtualResourceInstance equal = DefaultVirtualResourceInstance.of(name, address, mappedPorts);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultVirtualResourceInstance", "name", "address", "mappedPorts");
    }

}
