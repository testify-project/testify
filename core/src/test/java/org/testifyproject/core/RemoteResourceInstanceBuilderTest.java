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
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class RemoteResourceInstanceBuilderTest {

    RemoteResourceInstanceBuilder sut;

    @Before
    public void init() {
        sut = RemoteResourceInstanceBuilder.builder();
    }

    @Test
    public void givenClientInstanceAndNameBuildShouldReturn() {
        Object client = mock(Object.class);
        String name = "client";

        RemoteResourceInstance result = sut.client(client, name).build();

        assertThat(result).isNotNull();

        Instance<Object> clientInstance = result.getClient();

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

        RemoteResourceInstance result = sut.client(client, name, contract).build();

        assertThat(result).isNotNull();

        Instance<Object> clientInstance = result.getClient();

        assertThat(clientInstance).isNotNull();
        assertThat(clientInstance.getInstance()).isEqualTo(client);
        assertThat(clientInstance.getName()).contains(name);
        assertThat(clientInstance.getContract()).contains(contract);
    }

    @Test
    public void givenNameAndValuePropertyShouldAddProperty() {
        String name = "name";
        String value = "value";

        RemoteResourceInstance result = sut.property(name, value).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

    @Test
    public void givenPropertiesPropertyShouldAddProperty() {
        String name = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(name, value);

        RemoteResourceInstance result = sut.properties(properties).build();

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

}
