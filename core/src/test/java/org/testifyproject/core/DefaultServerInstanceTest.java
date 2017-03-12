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

    ServerInstance<Object> cut;
    URI baseURI;
    Object server;
    Class<Object> contract;

    @Before
    public void init() {
        baseURI = URI.create("uri://test.server");
        server = new Object();
        contract = Object.class;

        cut = DefaultServerInstance.of(baseURI, server, contract);

        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getInstance()).isEqualTo(server);
        assertThat(cut.getContract()).contains(contract);
    }

    @Test
    public void givenBaseURIAndServerOfShouldReturn() {
        cut = DefaultServerInstance.of(baseURI, server);

        assertThat(cut).isNotNull();
        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getInstance()).isEqualTo(server);
        assertThat(cut.getContract()).isEmpty();
    }

    @Test
    public void givenNameOfShouldReturn() {
        cut = DefaultServerInstance.of(baseURI, server);

        assertThat(cut).isNotNull();
        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getInstance()).isEqualTo(server);
        assertThat(cut.getContract()).isEmpty();
    }

    @Test
    public void givenContractOfShouldReturn() {
        cut = DefaultServerInstance.of(baseURI, server, contract);

        assertThat(cut).isNotNull();
        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getInstance()).isEqualTo(server);
        assertThat(cut.getContract()).contains(contract);
    }

    @Test
    public void callToGetBaseURIShouldReturnURI() {
        URI result = cut.getBaseURI();

        assertThat(result).isEqualTo(baseURI);
    }

    @Test
    public void callToGetServerShouldReturnServer() {
        Object result = cut.getInstance();

        assertThat(result).isEqualTo(server);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        ServerInstance<Object> instance = null;

        assertThat(cut).isNotEqualTo(instance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        ServerInstance<Object> uneuqual = DefaultServerInstance.of(baseURI, new Object());

        assertThat(cut).isNotEqualTo(uneuqual);
        assertThat(cut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);

    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ServerInstance<Object> equal
                = DefaultServerInstance.of(baseURI, server, contract);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains(
                "DefaultServerInstance",
                "baseURI",
                "server",
                "contract"
        );
    }

}
