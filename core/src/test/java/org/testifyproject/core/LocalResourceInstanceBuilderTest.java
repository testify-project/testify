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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class LocalResourceInstanceBuilderTest {

    LocalResourceInstanceBuilder cut;

    @Before
    public void init() {
        cut = LocalResourceInstanceBuilder.builder();
    }

    @Test
    public void givenInstanceInstanceAndNameBuildShouldReturn() {
        Object server = mock(Object.class);
        String name = "server";

        LocalResourceInstance result = cut.resource(server, name).build();

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getInstance()).isEqualTo(server);
        assertThat(instance.getName()).contains(name);
        assertThat(instance.getContract()).isEmpty();
    }

    @Test
    public void givenInstanceInstanceAndNameAndContractBuildShouldReturn() {
        Object server = mock(Object.class);
        String name = "server";
        Class contract = Object.class;

        LocalResourceInstance result = cut.resource(server, name, contract).build();

        assertThat(result).isNotNull();

        Instance resource = result.getResource();
        assertThat(resource).isNotNull();
        assertThat(resource.getInstance()).isEqualTo(server);
        assertThat(resource.getName()).contains(name);
        assertThat(resource.getContract()).contains(contract);
    }

    @Test
    public void givenClientInstanceAndNameBuildShouldReturn() {
        Object client = mock(Object.class);
        String name = "client";

        LocalResourceInstance result = cut.client(client, name).build();

        assertThat(result).isNotNull();

        Optional<Instance<Object>> clientInstanceResult = result.getClient();
        assertThat(clientInstanceResult).isPresent();

        Instance<Object> clientInstance = clientInstanceResult.get();

        assertThat(clientInstance).isNotNull();
        assertThat(clientInstance.getInstance()).isEqualTo(client);
        assertThat(clientInstance.getName()).contains(name);
        assertThat(clientInstance.getContract()).isEmpty();
    }

    @Test
    public void givenClientInstanceAndNameAndContractBuildShouldReturn() {
        Object client = mock(Object.class);
        String name = "client";
        Class contract = Object.class;

        LocalResourceInstance result = cut.client(client, name, contract).build();

        assertThat(result).isNotNull();

        Optional<Instance<Object>> clientInstanceResult = result.getClient();
        assertThat(clientInstanceResult).isPresent();

        Instance<Object> clientInstance = clientInstanceResult.get();

        assertThat(clientInstance).isNotNull();
        assertThat(clientInstance.getInstance()).isEqualTo(client);
        assertThat(clientInstance.getName()).contains(name);
        assertThat(clientInstance.getContract()).contains(contract);
    }

    @Test
    public void givenNameAndValuePropertyShouldAddProperty() {
        String name = "name";
        String value = "value";

        LocalResourceInstance result = cut.property(name, value).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

    @Test
    public void givenPropertiesPropertyShouldAddProperty() {
        String name = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(name, value);

        LocalResourceInstance result = cut.properties(properties).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

}
