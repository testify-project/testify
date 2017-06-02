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

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
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
    public void givenNameBuildShouldSetName() {
        String name = "name";

        VirtualResourceInstance result = sut.fqn(name).build();

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo(name);
    }

    @Test
    public void givenResourceBuildShouldSetResource() {
        Object resource = mock(Object.class);

        VirtualResourceInstance result = sut.resource(resource).build();

        assertThat(result).isNotNull();

        Instance<Object> resourceInstance = result.getResource();

        assertThat(resourceInstance).isNotNull();
        assertThat(resourceInstance.getValue()).isEqualTo(resource);
        assertThat(resourceInstance.getContract()).isEmpty();
    }

    @Test
    public void givenResourceWithContractBuildShouldSetResource() {
        Object resource = mock(Object.class);
        Class contract = Object.class;

        VirtualResourceInstance result = sut.resource(resource, contract).build();

        assertThat(result).isNotNull();

        Instance<Object> resourceInstance = result.getResource();

        assertThat(resourceInstance).isNotNull();
        assertThat(resourceInstance.getValue()).isEqualTo(resource);
        assertThat(resourceInstance.getContract()).contains(contract);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        VirtualResourceInstance result = sut.property(key, value).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String name = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(name, value);

        VirtualResourceInstance result = sut.properties(properties).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }
}
