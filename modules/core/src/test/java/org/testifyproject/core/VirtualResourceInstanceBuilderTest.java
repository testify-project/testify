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
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class VirtualResourceInstanceBuilderTest {

    VirtualResourceInstanceBuilder sut;
    String fqn;
    VirtualResource virtualResource;

    @Before
    public void init() {
        fqn = "test";
        virtualResource = mock(VirtualResource.class);
        sut = VirtualResourceInstanceBuilder.builder();
    }

    @Test
    public void givenFqnAndVirtualResourceBuildShouldCreateInstance() {
        VirtualResourceInstance<Object> result = sut.build(fqn, virtualResource);

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo(fqn);
        assertThat(result.getVirtualResource()).isEqualTo(virtualResource);
    }

    @Test
    public void givenResourceAndContractWithoutCustomNameAndContractBuildShouldAddResource() {
        String customName = "";
        Class customContract = void.class;
        Object resourceValue = mock(Object.class);
        Class resourceContract = Object.class;

        given(virtualResource.resourceName()).willReturn(customName);
        given(virtualResource.resourceContract()).willReturn(customContract);

        VirtualResourceInstance<Object> result = sut.resource(resourceValue, resourceContract)
                .build(fqn, virtualResource);

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

        given(virtualResource.resourceName()).willReturn(customName);
        given(virtualResource.resourceContract()).willReturn(customContract);

        VirtualResourceInstance<Object> result = sut.resource(resourceValue).build(fqn,
                virtualResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/resource");
        assertThat(instance.getContract()).isNull();
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenResourceWithCustomNameAndContractBuildShouldAddResource() {
        String customName = "resourceName";
        Class customContract = Object.class;
        Object resourceValue = mock(Object.class);

        given(virtualResource.resourceName()).willReturn(customName);
        given(virtualResource.resourceContract()).willReturn(customContract);

        VirtualResourceInstance<Object> result = sut.resource(resourceValue).build(fqn,
                virtualResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/resourceName");
        assertThat(instance.getContract()).isEqualTo(customContract);
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        VirtualResourceInstance<Object> result = sut.property(key, value)
                .build(fqn, virtualResource);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String key = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(key, value);

        VirtualResourceInstance<Object> result = sut.properties(properties).build(fqn,
                virtualResource);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

}
