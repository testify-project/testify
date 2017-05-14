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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;

/**
 *
 * @author saden
 */
public class DefaultRemoteResourceInstanceTest {

    RemoteResourceInstance<Object> sut;

    Instance<Object> client;
    Map<String, Object> properties;

    @Before
    public void init() {
        client = mock(Instance.class);
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        sut = DefaultRemoteResourceInstance.of(client, properties);
    }

    @Test
    public void callToGetClientShouldReturnOptionalWithClientInstance() {
        Instance<Object> result = sut.getClient();

        assertThat(result).isEqualTo(client);
    }

    @Test
    public void givenNonExistentProperty() {
        Optional<Object> result = sut.findProperty("non");

        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistentProperty() {
        String name = "name";
        String value = "value";
        properties.put(name, value);

        Optional<Object> result = sut.findProperty(name);

        assertThat(result).contains(value);
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
                = DefaultRemoteResourceInstance.of(null, properties);

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
                = DefaultRemoteResourceInstance.of(client, properties);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultRemoteResourceInstance", "client", "properties");
    }

}
