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
import org.testifyproject.ResourceInstance;

/**
 *
 * @author saden
 */
public class DefaultResourceInstanceTest {

    ResourceInstance<Object, Object> cut;

    Instance<Object> instance;
    Instance<Object> client;
    Map<String, Object> properties;

    @Before
    public void init() {
        instance = mock(Instance.class);
        client = mock(Instance.class);
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        cut = DefaultResourceInstance.of(instance, client, properties);
    }

    @Test
    public void callToGetInstanceShouldReturnResourceInstance() {
        Instance<Object> result = cut.getInstance();

        assertThat(result).isEqualTo(instance);
    }

    @Test
    public void callToGetClientShouldReturnOptionalWithClientInstance() {
        Optional<Instance<Object>> result = cut.getClient();

        assertThat(result).contains(client);
    }

    @Test
    public void givenNonExistentProperty() {
        Optional<Object> result = cut.findProperty("non");

        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistentProperty() {
        String name = "name";
        String value = "value";
        properties.put(name, value);

        Optional<Object> result = cut.findProperty(name);

        assertThat(result).contains(value);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        ResourceInstance<Object, Object> resourceInstance = null;

        assertThat(cut).isNotEqualTo(resourceInstance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        ResourceInstance<Object, Object> uneuqual
                = DefaultResourceInstance.of(instance, null, null);

        assertThat(cut).isNotEqualTo(uneuqual);
        assertThat(cut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ResourceInstance<Object, Object> equal
                = DefaultResourceInstance.of(instance, client, properties);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultResourceInstance", "instance", "client", "properties");
    }

}
