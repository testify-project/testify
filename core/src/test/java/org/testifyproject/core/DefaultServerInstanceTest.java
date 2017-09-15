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

import java.net.URI;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.Instance;
import org.testifyproject.ServerInstance;
import org.testifyproject.annotation.Application;

/**
 *
 * @author saden
 */
public class DefaultServerInstanceTest {

    ServerInstance<Object> sut;
    String fqn;
    Application application;
    URI baseURI;
    Instance<Object> server;
    Class<Object> contract;
    Map<String, Object> properties;

    @Before
    public void init() {
        fqn = "fqn";
        application = mock(Application.class);
        baseURI = URI.create("uri://test.server");
        server = mock(Instance.class);
        properties = mock(Map.class);

        sut = DefaultServerInstance.of(fqn, application, baseURI, server, properties);
    }

    @Test
    public void validateSutInstance() {
        assertThat(sut.getFqn()).isEqualTo(fqn);
        assertThat(sut.getBaseURI()).isEqualTo(baseURI);
        assertThat(sut.getServer()).isEqualTo(server);
        assertThat(sut.getProperties()).isEqualTo(properties);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        ServerInstance<Object> instance = null;

        assertThat(sut).isNotEqualTo(instance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        ServerInstance<Object> uneuqual = DefaultServerInstance.of(fqn, null, null, null, null);

        assertThat(sut).isNotEqualTo(uneuqual);
        assertThat(sut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);

    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ServerInstance<Object> equal = DefaultServerInstance.of(fqn, application, baseURI,
                server,
                properties);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains(
                "DefaultServerInstance",
                "fqn",
                "baseURI",
                "server",
                "properties"
        );
    }

}
