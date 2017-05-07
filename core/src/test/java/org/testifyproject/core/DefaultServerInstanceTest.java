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

import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ServerInstance;

/**
 *
 * @author saden
 */
public class DefaultServerInstanceTest {

    ServerInstance<Object> sut;
    URI baseURI;
    Object server;
    Class<Object> contract;

    @Before
    public void init() {
        baseURI = URI.create("uri://test.server");
        server = new Object();
        contract = Object.class;

        sut = DefaultServerInstance.of(baseURI, server, contract);

        assertThat(sut.getBaseURI()).isEqualTo(baseURI);
        assertThat(sut.getInstance()).isEqualTo(server);
        assertThat(sut.getContract()).contains(contract);
    }

    @Test
    public void givenBaseURIAndServerOfShouldReturn() {
        sut = DefaultServerInstance.of(baseURI, server);

        assertThat(sut).isNotNull();
        assertThat(sut.getBaseURI()).isEqualTo(baseURI);
        assertThat(sut.getInstance()).isEqualTo(server);
        assertThat(sut.getContract()).isEmpty();
    }

    @Test
    public void givenNameOfShouldReturn() {
        sut = DefaultServerInstance.of(baseURI, server);

        assertThat(sut).isNotNull();
        assertThat(sut.getBaseURI()).isEqualTo(baseURI);
        assertThat(sut.getInstance()).isEqualTo(server);
        assertThat(sut.getContract()).isEmpty();
    }

    @Test
    public void givenContractOfShouldReturn() {
        sut = DefaultServerInstance.of(baseURI, server, contract);

        assertThat(sut).isNotNull();
        assertThat(sut.getBaseURI()).isEqualTo(baseURI);
        assertThat(sut.getInstance()).isEqualTo(server);
        assertThat(sut.getContract()).contains(contract);
    }

    @Test
    public void callToGetBaseURIShouldReturnURI() {
        URI result = sut.getBaseURI();

        assertThat(result).isEqualTo(baseURI);
    }

    @Test
    public void callToGetInstanceShouldReturnServer() {
        Object result = sut.getInstance();

        assertThat(result).isEqualTo(server);
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
        ServerInstance<Object> uneuqual = DefaultServerInstance.of(baseURI, new Object());

        assertThat(sut).isNotEqualTo(uneuqual);
        assertThat(sut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);

    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ServerInstance<Object> equal
                = DefaultServerInstance.of(baseURI, server, contract);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains(
                "DefaultServerInstance",
                "baseURI",
                "server",
                "contract"
        );
    }

}
