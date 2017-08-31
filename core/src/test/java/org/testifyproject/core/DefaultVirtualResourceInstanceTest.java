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
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.annotation.VirtualResource;

/**
 *
 * @author saden
 */
public class DefaultVirtualResourceInstanceTest {

    VirtualResourceInstance sut;

    String name;
    VirtualResource virtualResource;
    Instance<InetAddress> resource;
    Map<Integer, Integer> mappedPorts;
    Map<String, Object> properties;

    @Before
    public void init() {
        name = "name";
        virtualResource = mock(VirtualResource.class);
        resource = mock(Instance.class);
        properties = new HashMap<>();

        sut = DefaultVirtualResourceInstance.of(name, virtualResource, resource, properties);
    }

    @Test
    public void callToGetNameShouldReturnName() {
        String result = sut.getFqn();

        assertThat(result).isEqualTo(name);
    }

    @Test
    public void callToGetResourceShouldReturnResource() {
        Instance<Object> result = sut.getResource();

        assertThat(result).isEqualTo(resource);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        VirtualResourceInstance unequal = DefaultVirtualResourceInstance.of(null, virtualResource, resource, properties);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        VirtualResourceInstance equal = DefaultVirtualResourceInstance.of(name, virtualResource, resource, properties);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultVirtualResourceInstance",
                "name",
                "virtualResource",
                "resource",
                "properties");
    }

}
