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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
        ClientInstance<Object> result = sut.build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo(fqn);
        assertThat(result.getApplication()).isEqualTo(application);
    }

    @Test
    public void givenClientAndContractWithoutCustomNameAndContractBuildShouldAddClient() {
        String customName = "";
        Class customContract = void.class;
        Object clientValue = mock(Object.class);
        Class clientContract = Object.class;

        given(application.clientName()).willReturn(customName);
        given(application.clientContract()).willReturn(customContract);

        ClientInstance<Object> result = sut.client(clientValue, clientContract)
                .build(fqn, application);

        assertThat(result).isNotNull();

        Instance instance = result.getClient();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("application:/test/client");
        assertThat(instance.getContract()).contains(clientContract);
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientWithoutCustomNameAndContractBuildShouldAddClient() {
        String customName = "";
        Class customContract = void.class;
        Object clientValue = mock(Object.class);

        given(application.clientName()).willReturn(customName);
        given(application.clientContract()).willReturn(customContract);

        ClientInstance<Object> result = sut.client(clientValue).build(fqn, application);

        assertThat(result).isNotNull();

        Instance instance = result.getClient();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("application:/test/client");
        assertThat(instance.getContract()).isEmpty();
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientWithCustomNameAndContractBuildShouldAddClient() {
        String customName = "customName";
        Class customContract = Object.class;
        Object clientValue = mock(Object.class);

        given(application.clientName()).willReturn(customName);
        given(application.clientContract()).willReturn(customContract);

        ClientInstance<Object> result = sut.client(clientValue).build(fqn, application);

        assertThat(result).isNotNull();

        Instance instance = result.getClient();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("application:/test/customName");
        assertThat(instance.getContract()).contains(customContract);
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientProviderAndContractWithoutCustomNameAndContractBuildShouldAddClientProvider() {
        String customName = "";
        Class customContract = void.class;
        Object clientProviderValue = mock(Object.class);
        Class clientProviderContract = Object.class;

        given(application.clientProviderName()).willReturn(customName);
        given(application.clientProviderContract()).willReturn(customContract);

        ClientInstance<Object> result = sut.clientProvider(clientProviderValue, clientProviderContract)
                .build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getClientProvider()).isPresent();

        Instance instance = result.getClientProvider().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("application:/test/clientProvider");
        assertThat(instance.getContract()).contains(clientProviderContract);
        assertThat(instance.getValue()).isEqualTo(clientProviderValue);
    }

    @Test
    public void givenClientProviderWithoutCustomNameAndContractBuildShouldAddClientProvider() {
        String customName = "";
        Class customContract = void.class;
        Object clientProviderValue = mock(Object.class);

        given(application.clientProviderName()).willReturn(customName);
        given(application.clientProviderContract()).willReturn(customContract);

        ClientInstance<Object> result = sut.clientProvider(clientProviderValue).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getClientProvider()).isPresent();

        Instance instance = result.getClientProvider().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("application:/test/clientProvider");
        assertThat(instance.getContract()).isEmpty();
        assertThat(instance.getValue()).isEqualTo(clientProviderValue);
    }

    @Test
    public void givenClientProviderWithCustomNameAndContractBuildShouldAddClientProvider() {
        String customName = "customName";
        Class customContract = Object.class;
        Object clientProviderValue = mock(Object.class);

        given(application.clientProviderName()).willReturn(customName);
        given(application.clientProviderContract()).willReturn(customContract);

        ClientInstance<Object> result = sut.clientProvider(clientProviderValue).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.getClientProvider()).isPresent();

        Instance instance = result.getClientProvider().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("application:/test/customName");
        assertThat(instance.getContract()).contains(customContract);
        assertThat(instance.getValue()).isEqualTo(clientProviderValue);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        ClientInstance<Object> result = sut.property(key, value).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String key = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(key, value);

        ClientInstance<Object> result = sut.properties(properties).build(fqn, application);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }
}
