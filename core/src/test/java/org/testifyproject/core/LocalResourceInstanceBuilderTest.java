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

    LocalResourceInstanceBuilder sut;
    String fqn;

    @Before
    public void init() {
        fqn = "test";
        sut = LocalResourceInstanceBuilder.builder();
    }

    @Test
    public void givenResourceBuildShouldSetResource() {
        Object resource = mock(Object.class);

        LocalResourceInstance result = sut.resource(resource).build(fqn);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getValue()).isEqualTo(resource);
        assertThat(instance.getContract()).isEmpty();
    }

    @Test
    public void givenResourceWithContractBuildShouldSetResource() {
        Object resource = mock(Object.class);
        Class contract = Object.class;

        LocalResourceInstance result = sut.resource(resource, contract).build(fqn);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getValue()).isEqualTo(resource);
        assertThat(instance.getContract()).contains(contract);
    }

    @Test
    public void givenClientBuildShouldSetClient() {
        Object client = mock(Object.class);

        LocalResourceInstance result = sut.client(client).build(fqn);

        assertThat(result).isNotNull();

        Optional<Instance<Object>> foundClient = result.getClient();
        assertThat(foundClient).isPresent();

        Instance<Object> instance = foundClient.get();

        assertThat(instance).isNotNull();
        assertThat(instance.getValue()).isEqualTo(client);
        assertThat(instance.getContract()).isEmpty();
    }

    @Test
    public void givenClientWithContractBuildShouldSetClient() {
        Object client = mock(Object.class);
        Class contract = Object.class;

        LocalResourceInstance result = sut.client(client, contract).build(fqn);

        assertThat(result).isNotNull();

        Optional<Instance<Object>> foundClient = result.getClient();
        assertThat(foundClient).isPresent();

        Instance<Object> clientInstance = foundClient.get();

        assertThat(clientInstance).isNotNull();
        assertThat(clientInstance.getValue()).isEqualTo(client);
        assertThat(clientInstance.getContract()).contains(contract);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        LocalResourceInstance result = sut.property(key, value).build(fqn);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String key = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(key, value);

        LocalResourceInstance result = sut.properties(properties).build(fqn);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

}
