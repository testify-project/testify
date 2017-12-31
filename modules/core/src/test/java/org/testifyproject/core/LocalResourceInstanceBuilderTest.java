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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class LocalResourceInstanceBuilderTest {

    LocalResourceInstanceBuilder sut;
    String fqn;
    LocalResource localResource;

    @Before
    public void init() {
        fqn = "test";
        localResource = mock(LocalResource.class);
        sut = LocalResourceInstanceBuilder.builder();
    }

    @Test
    public void givenFqnAndLocalResourceBuildShouldCreateInstance() {
        LocalResourceInstance<Object, Object> result = sut.build(fqn, localResource);

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo(fqn);
        assertThat(result.getLocalResource()).isEqualTo(localResource);
    }

    @Test
    public void givenResourceAndContractWithoutCustomNameAndContractBuildShouldAddResource() {
        String customName = "";
        Class customContract = void.class;
        Object resourceValue = mock(Object.class);
        Class resourceContract = Object.class;

        given(localResource.resourceName()).willReturn(customName);
        given(localResource.resourceContract()).willReturn(customContract);

        LocalResourceInstance<Object, Object> result = sut.resource(resourceValue,
                resourceContract)
                .build(fqn, localResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/resource");
        assertThat(instance.getContract()).isEqualTo(resourceContract);
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenResourceWithoutCustomNameAndContractBuildShouldAddResource() {
        String customName = "";
        Class customContract = void.class;
        Object resourceValue = mock(Object.class);

        given(localResource.resourceName()).willReturn(customName);
        given(localResource.resourceContract()).willReturn(customContract);

        LocalResourceInstance<Object, Object> result = sut.resource(resourceValue).build(fqn,
                localResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/resource");
        assertThat(instance.getContract()).isNull();
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenResourceWithCustomNameAndContractBuildShouldAddResource() {
        String customName = "customName";
        Class customContract = Object.class;
        Object resourceValue = mock(Object.class);

        given(localResource.resourceName()).willReturn(customName);
        given(localResource.resourceContract()).willReturn(customContract);

        LocalResourceInstance<Object, Object> result = sut.resource(resourceValue).build(fqn,
                localResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/customName");
        assertThat(instance.getContract()).isEqualTo(customContract);
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenClientAndContractWithoutCustomNameAndContractBuildShouldAddClient() {
        String customName = "";
        Class customContract = void.class;
        Object clientValue = mock(Object.class);
        Class clientContract = Object.class;

        given(localResource.clientName()).willReturn(customName);
        given(localResource.clientContract()).willReturn(customContract);

        LocalResourceInstance<Object, Object> result = sut.client(clientValue, clientContract)
                .build(fqn, localResource);

        assertThat(result).isNotNull();
        assertThat(result.getClient()).isPresent();

        Instance instance = result.getClient().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/client");
        assertThat(instance.getContract()).isEqualTo(clientContract);
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientWithoutCustomNameAndContractBuildShouldAddClient() {
        String customName = "";
        Class customContract = void.class;
        Object clientValue = mock(Object.class);

        given(localResource.clientName()).willReturn(customName);
        given(localResource.clientContract()).willReturn(customContract);

        LocalResourceInstance<Object, Object> result = sut.client(clientValue).build(fqn,
                localResource);

        assertThat(result).isNotNull();
        assertThat(result.getClient()).isPresent();

        Instance instance = result.getClient().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/client");
        assertThat(instance.getContract()).isNull();
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenClientWithCustomNameAndContractBuildShouldAddClient() {
        String customName = "customName";
        Class customContract = Object.class;
        Object clientValue = mock(Object.class);

        given(localResource.clientName()).willReturn(customName);
        given(localResource.clientContract()).willReturn(customContract);

        LocalResourceInstance<Object, Object> result = sut.client(clientValue).build(fqn,
                localResource);

        assertThat(result).isNotNull();
        assertThat(result.getClient()).isPresent();

        Instance instance = result.getClient().get();

        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/customName");
        assertThat(instance.getContract()).isEqualTo(customContract);
        assertThat(instance.getValue()).isEqualTo(clientValue);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        LocalResourceInstance<Object, Object> result = sut.property(key, value).build(fqn,
                localResource);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String key = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(key, value);

        LocalResourceInstance<Object, Object> result = sut.properties(properties).build(fqn,
                localResource);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

}
