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

import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.annotation.RemoteResource;

/**
 *
 * @author saden
 */
public class DefaultRemoteResourceInstanceTest {

    RemoteResourceInstance<Object> sut;

    String name;
    RemoteResource remoteResource;
    Instance<Object> resource;
    Map<String, Object> properties;

    @Before
    public void init() {
        name = "name";
        remoteResource = mock(RemoteResource.class);
        resource = mock(Instance.class);
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        sut = DefaultRemoteResourceInstance.of(name, remoteResource, resource, properties);
    }

    @Test
    public void callToGetNameShouldReturnName() {
        String result = sut.getFqn();

        assertThat(result).isEqualTo(name);
    }

    @Test
    public void callToGetResourceShouldReturnResource() {
        Instance<Object> result = sut.getResource();

        assertThat(result).isEqualTo(resource);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        RemoteResourceInstance<Object> remoteResourceInstance = null;

        assertThat(sut).isNotEqualTo(remoteResourceInstance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        RemoteResourceInstance<Object> uneuqual
                = DefaultRemoteResourceInstance.of(name, remoteResource, null, properties);

        assertThat(sut).isNotEqualTo(uneuqual);
        assertThat(sut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        RemoteResourceInstance<Object> equal
                = DefaultRemoteResourceInstance.of(name, remoteResource, resource, properties);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultRemoteResourceInstance",
                "name",
                "remoteResource",
                "resource",
                "properties");
    }

}
