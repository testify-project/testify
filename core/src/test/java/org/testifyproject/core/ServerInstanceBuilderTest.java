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

import java.net.URI;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
import org.testifyproject.ServerInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class ServerInstanceBuilderTest {

    ServerInstanceBuilder sut;
    String fqn;
    Application application;

    @Before
    public void init() {
        fqn = "test";
        application = mock(Application.class);
        sut = ServerInstanceBuilder.builder();
    }

    @Test
    public void givenFqnBuildShouldSetName() {
        ServerInstance result = sut.build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo(fqn);
        assertThat(result.getApplication()).isEqualTo(application);
    }

    @Test
    public void givenBaseURIBuildShouldSetBaseURI() {
        URI baseURI = URI.create("uri://test");

        ServerInstance result = sut.baseURI(baseURI).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getBaseURI()).isEqualTo(baseURI);
    }

    @Test
    public void givenServerBuildShouldSetServer() {
        Object server = mock(Object.class);

        ServerInstance result = sut.server(server).build(fqn, application);

        assertThat(result).isNotNull();

        Instance<Object> resourceInstance = result.getServer();

        assertThat(resourceInstance).isNotNull();
        assertThat(resourceInstance.getValue()).isEqualTo(server);
        assertThat(resourceInstance.getContract()).isEmpty();
    }

    @Test
    public void givenServerWithContractBuildShouldSetServer() {
        Object server = mock(Object.class);
        Class contract = Object.class;

        ServerInstance result = sut.server(server, contract).build(fqn, application);

        assertThat(result).isNotNull();

        Instance<Object> resourceInstance = result.getServer();

        assertThat(resourceInstance).isNotNull();
        assertThat(resourceInstance.getValue()).isEqualTo(server);
        assertThat(resourceInstance.getContract()).contains(contract);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        ServerInstance result = sut.property(key, value).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String name = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(name, value);

        ServerInstance result = sut.properties(properties).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(name)).contains(value);
    }

}
