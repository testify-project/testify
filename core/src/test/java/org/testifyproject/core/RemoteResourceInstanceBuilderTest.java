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
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class RemoteResourceInstanceBuilderTest {

    RemoteResourceInstanceBuilder sut;
    String fqn;
    RemoteResource remoteResource;

    @Before
    public void init() {
        fqn = "test";
        remoteResource = mock(RemoteResource.class);
        sut = RemoteResourceInstanceBuilder.builder();
    }

    @Test
    public void givenFqnAndRemoteResourceBuildShouldCreateInstance() {
        RemoteResourceInstance<Object> result = sut.build(fqn, remoteResource);

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo(fqn);
        assertThat(result.getRemoteResource()).isEqualTo(remoteResource);
    }

    @Test
    public void givenResourceAndContractWithoutCustomNameAndContractBuildShouldAddResource() {
        String customName = "";
        Class customContract = void.class;
        Object resourceValue = mock(Object.class);
        Class resourceContract = Object.class;

        given(remoteResource.resourceName()).willReturn(customName);
        given(remoteResource.resourceContract()).willReturn(customContract);

        RemoteResourceInstance<Object> result = sut.resource(resourceValue, resourceContract)
                .build(fqn, remoteResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/resource");
        assertThat(instance.getContract()).contains(resourceContract);
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenResourceWithoutCustomNameAndContractBuildShouldAddResource() {
        String customName = "";
        Class customContract = void.class;
        Object resourceValue = mock(Object.class);

        given(remoteResource.resourceName()).willReturn(customName);
        given(remoteResource.resourceContract()).willReturn(customContract);

        RemoteResourceInstance<Object> result = sut.resource(resourceValue).build(fqn, remoteResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/resource");
        assertThat(instance.getContract()).isEmpty();
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenResourceWithCustomNameAndContractBuildShouldAddResource() {
        String customName = "resourceName";
        Class customContract = Object.class;
        Object resourceValue = mock(Object.class);

        given(remoteResource.resourceName()).willReturn(customName);
        given(remoteResource.resourceContract()).willReturn(customContract);

        RemoteResourceInstance<Object> result = sut.resource(resourceValue).build(fqn, remoteResource);

        assertThat(result).isNotNull();

        Instance instance = result.getResource();
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).contains("resource:/test/resourceName");
        assertThat(instance.getContract()).contains(customContract);
        assertThat(instance.getValue()).isEqualTo(resourceValue);
    }

    @Test
    public void givenPropertyBuildShouldAddProperty() {
        String key = "key";
        String value = "value";

        RemoteResourceInstance<Object> result = sut.property(key, value).build(fqn, remoteResource);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

    @Test
    public void givenPropertiesBuildShouldAddProperties() {
        String key = "name";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(key, value);

        RemoteResourceInstance<Object> result = sut.properties(properties).build(fqn, remoteResource);

        assertThat(result).isNotNull();
        assertThat(result.findProperty(key)).contains(value);
    }

}
