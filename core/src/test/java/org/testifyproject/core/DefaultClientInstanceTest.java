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
import org.testifyproject.ClientInstance;

/**
 *
 * @author saden
 */
public class DefaultClientInstanceTest {

    ClientInstance<Object> cut;
    URI baseURI;
    Object client;
    String name;
    Class<Object> contract;

    @Before
    public void init() {
        baseURI = URI.create("uri://test.server");
        client = new Object();
        name = "name";
        contract = Object.class;

        cut = DefaultClientInstance.of(baseURI, client, name, contract);

        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getClient()).isEqualTo(client);
        assertThat(cut.getName()).contains(name);
        assertThat(cut.getContract()).contains(contract);
    }

    @Test
    public void givenBaseURIAndClientOfShouldReturn() {
        cut = DefaultClientInstance.of(baseURI, client);

        assertThat(cut).isNotNull();
        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getClient()).isEqualTo(client);
        assertThat(cut.getName()).isEmpty();
        assertThat(cut.getContract()).isEmpty();
    }

    @Test
    public void givenNameOfShouldReturn() {
        cut = DefaultClientInstance.of(baseURI, client, name);

        assertThat(cut).isNotNull();
        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getClient()).isEqualTo(client);
        assertThat(cut.getName()).contains(name);
        assertThat(cut.getContract()).isEmpty();
    }

    @Test
    public void givenContractOfShouldReturn() {
        cut = DefaultClientInstance.of(baseURI, client, contract);

        assertThat(cut).isNotNull();
        assertThat(cut.getBaseURI()).isEqualTo(baseURI);
        assertThat(cut.getClient()).isEqualTo(client);
        assertThat(cut.getName()).isEmpty();
        assertThat(cut.getContract()).contains(contract);
    }

    @Test
    public void callToGetBaseURIShouldReturnURI() {
        URI result = cut.getBaseURI();

        assertThat(result).isEqualTo(baseURI);
    }

    @Test
    public void callToGetClientShouldReturnClient() {
        Object result = cut.getClient();

        assertThat(result).isEqualTo(client);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        ClientInstance<Object> instance = null;

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
        ClientInstance<Object> uneuqual = DefaultClientInstance.of(baseURI, new Object());

        assertThat(cut).isNotEqualTo(uneuqual);
        assertThat(cut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);

    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ClientInstance<Object> equal
                = DefaultClientInstance.of(baseURI, client, name, contract);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains(
                "DefaultClientInstance",
                "baseURI",
                "client",
                "name",
                "contract"
        );
    }
}
