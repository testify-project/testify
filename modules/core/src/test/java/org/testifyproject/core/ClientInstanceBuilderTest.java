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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ClientInstance;
import org.testifyproject.Instance;
import org.testifyproject.annotation.Application;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class ClientInstanceBuilderTest {

    ClientInstanceBuilder sut;
    String fqn;
    Application application;

    @Before
    public void init() {
        fqn = "test";
        application = mock(Application.class);
        sut = ClientInstanceBuilder.builder();
    }

    @Test
    public void givenFqnAndLocalClientBuildShouldCreateInstance() {
        ClientInstance<Object, Object> result = sut.build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo(fqn);
        assertThat(result.getApplication()).isEqualTo(application);
    }

    @Test
    public void givenClientAndContractWithoutCustomNameAndContractBuildShouldAddClient() {
        Object clientValue = mock(Object.class);
        Class clientContract = Object.class;

        ClientInstance<Object, Object> result = sut.client(clientValue, clientContract)
                .build(fqn, application);

        assertThat(result).isNotNull();

        Instance instance = result.getClient();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isNull();
        assertThat(instance.getContract()).isEqualTo(clientContract);
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientWithoutCustomNameAndContractBuildShouldAddClient() {
        Object clientValue = mock(Object.class);

        ClientInstance<Object, Object> result = sut.client(clientValue).build(fqn, application);

        assertThat(result).isNotNull();

        Instance instance = result.getClient();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isNull();
        assertThat(instance.getContract()).isNull();
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientWithCustomNameAndContractBuildShouldAddClient() {
        Object clientValue = mock(Object.class);

        ClientInstance<Object, Object> result = sut.client(clientValue).build(fqn, application);

        assertThat(result).isNotNull();

        Instance instance = result.getClient();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isNull();
        assertThat(instance.getContract()).isNull();
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientProviderAndContractWithoutCustomNameAndContractBuildShouldAddClientProvider() {
        Object clientProviderValue = mock(Object.class);
        Class clientProviderContract = Object.class;

        ClientInstance<Object, Object> result = sut.clientSupplier(clientProviderValue,
                clientProviderContract)
                .build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getClientSupplier()).isPresent();

        Instance instance = result.getClientSupplier().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isNull();
        assertThat(instance.getContract()).isEqualTo(clientProviderContract);
        assertThat(instance.getValue()).isEqualTo(clientProviderValue);
    }

    @Test
    public void givenClientProviderWithoutCustomNameAndContractBuildShouldAddClientProvider() {
        Object clientProviderValue = mock(Object.class);

        ClientInstance<Object, Object> result = sut.clientSupplier(clientProviderValue)
                .build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getClientSupplier()).isPresent();

        Instance instance = result.getClientSupplier().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isNull();
        assertThat(instance.getContract()).isNull();
        assertThat(instance.getValue()).isEqualTo(clientProviderValue);
    }

    @Test
    public void givenClientProviderWithCustomNameAndContractBuildShouldAddClientProvider() {
        Object clientProviderValue = mock(Object.class);

        ClientInstance<Object, Object> result = sut.clientSupplier(clientProviderValue).build(
                fqn,
                application);

        assertThat(result).isNotNull();
        assertThat(result.getClientSupplier()).isPresent();

        Instance instance = result.getClientSupplier().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isNull();
        assertThat(instance.getContract()).isNull();
        assertThat(instance.getValue()).isEqualTo(clientProviderValue);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        ClientInstance<Object, Object> result = sut.property(key, value).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String key = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(key, value);

        ClientInstance<Object, Object> result = sut.properties(properties).build(fqn,
                application);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }
}
