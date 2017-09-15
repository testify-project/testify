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
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.annotation.Application;

/**
 *
 * @author saden
 */
public class DefaultClientInstanceTest {

    DefaultClientInstance<Object> sut;

    String fqn;
    Application application;
    Instance<Object> client;
    Instance<Object> clientProvider;
    Map<String, Object> properties;

    @Before
    public void init() {
        fqn = "name";
        application = mock(Application.class);
        client = mock(Instance.class);
        clientProvider = mock(Instance.class);
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        sut = DefaultClientInstance.of(fqn, application, client, clientProvider, properties);
    }

    @Test
    public void callToGetNameShouldReturnName() {
        String result = sut.getFqn();

        assertThat(result).isEqualTo(fqn);
    }

    @Test
    public void callToGetClientShouldReturnClient() {
        Instance<Object> result = sut.getClient();

        assertThat(result).isEqualTo(client);
    }

    @Test
    public void callToGetResourceShouldReturnResource() {
        Optional<Instance<Object>> result = sut.getClientProvider();

        assertThat(result).contains(clientProvider);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        LocalResourceInstance<Object, Object> localResourceInstance = null;

        assertThat(sut).isNotEqualTo(localResourceInstance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        DefaultClientInstance<Object> uneuqual =
                DefaultClientInstance.of(fqn, application, clientProvider, null, properties);

        assertThat(sut).isNotEqualTo(uneuqual);
        assertThat(sut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        DefaultClientInstance<Object> equal =
                DefaultClientInstance.of(fqn, application, client, clientProvider, properties);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultClientInstance",
                "application",
                "fqn",
                "client",
                "clientProvider",
                "properties");
    }
}
