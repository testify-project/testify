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
import org.testifyproject.ResourceInstance;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class ResourceInstanceBuilderTest {

    ResourceInstanceBuilder cut;

    @Before
    public void init() {
        cut = new ResourceInstanceBuilder();
    }

    @Test
    public void givenServerInstanceBuildShouldReturn() {
        Object server = mock(Object.class);

        ResourceInstance result = cut.server(server).build();

        assertThat(result).isNotNull();

        Instance serverInstance = result.getServer();
        assertThat(serverInstance).isNotNull();
        assertThat(serverInstance.getInstance()).isEqualTo(server);
        assertThat(serverInstance.getName()).isEmpty();
        assertThat(serverInstance.getContract()).isEmpty();
    }

    @Test
    public void givenServerInstanceAndNameBuildShouldReturn() {
        Object server = mock(Object.class);
        String name = "name";

        ResourceInstance result = cut.server(server, name).build();

        assertThat(result).isNotNull();

        Instance serverInstance = result.getServer();
        assertThat(serverInstance).isNotNull();
        assertThat(serverInstance.getInstance()).isEqualTo(server);
        assertThat(serverInstance.getName()).contains(name);
        assertThat(serverInstance.getContract()).isEmpty();
    }

    @Test
    public void givenServerInstanceAndContractBuildShouldReturn() {
        Object server = mock(Object.class);
        Class contract = Object.class;

        ResourceInstance result = cut.server(server, contract).build();

        assertThat(result).isNotNull();

        Instance serverInstance = result.getServer();
        assertThat(serverInstance).isNotNull();
        assertThat(serverInstance.getInstance()).isEqualTo(server);
        assertThat(serverInstance.getName()).isEmpty();
        assertThat(serverInstance.getContract()).contains(contract);
    }

    @Test
    public void givenServerInstanceAndNameAndContractBuildShouldReturn() {
        Object server = mock(Object.class);
        String name = "name";
        Class contract = Object.class;

        ResourceInstance result = cut.server(server, name, contract).build();

        assertThat(result).isNotNull();

        Instance serverInstance = result.getServer();
        assertThat(serverInstance).isNotNull();
        assertThat(serverInstance.getInstance()).isEqualTo(server);
        assertThat(serverInstance.getName()).contains(name);
        assertThat(serverInstance.getContract()).contains(contract);
    }

    @Test
    public void givenClientInstanceBuildShouldReturn() {
        Object client = mock(Object.class);

        ResourceInstance result = cut.client(client).build();

        assertThat(result).isNotNull();

        Optional<Instance<Object>> clientInstanceResult = result.getClient();
        assertThat(clientInstanceResult).isPresent();

        Instance<Object> clientInstance = clientInstanceResult.get();

        assertThat(clientInstance).isNotNull();
        assertThat(clientInstance.getInstance()).isEqualTo(client);
        assertThat(clientInstance.getName()).isEmpty();
        assertThat(clientInstance.getContract()).isEmpty();
    }

    @Test
    public void givenClientInstanceAndNameBuildShouldReturn() {
        Object client = mock(Object.class);
        String name = "name";

        ResourceInstance result = cut.client(client, name).build();

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
    public void givenClientInstanceAndContractBuildShouldReturn() {
        Object client = mock(Object.class);
        Class contract = Object.class;

        ResourceInstance result = cut.client(client, contract).build();

        assertThat(result).isNotNull();

        Optional<Instance<Object>> clientInstanceResult = result.getClient();
        assertThat(clientInstanceResult).isPresent();

        Instance<Object> clientInstance = clientInstanceResult.get();

        assertThat(clientInstance).isNotNull();
        assertThat(clientInstance.getInstance()).isEqualTo(client);
        assertThat(clientInstance.getName()).isEmpty();
        assertThat(clientInstance.getContract()).contains(contract);
    }

    @Test
    public void givenClientInstanceAndNameAndContractBuildShouldReturn() {
        Object client = mock(Object.class);
        String name = "name";
        Class contract = Object.class;

        ResourceInstance result = cut.client(client, name, contract).build();

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

        ResourceInstance result = cut.property(name, value).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

    @Test
    public void givenPropertiesPropertyShouldAddProperty() {
        String name = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(name, value);

        ResourceInstance result = cut.properties(properties).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

}
