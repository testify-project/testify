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
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ClientInstance;

/**
 *
 * @author saden
 */
public class DefaultClientInstanceTest {

    ClientInstance<Object> cut;
    Object client;
    Class<Object> contract;

    @Before
    public void init() {
        client = new Object();
        contract = Object.class;

        cut = DefaultClientInstance.of(client, contract);

        assertThat(cut.getInstance()).isEqualTo(client);
        assertThat(cut.getContract()).contains(contract);
    }

    @Test
    public void givenBaseURIAndClientOfShouldReturn() {
        cut = DefaultClientInstance.of(client);

        assertThat(cut).isNotNull();
        assertThat(cut.getInstance()).isEqualTo(client);
        assertThat(cut.getContract()).isEmpty();
    }

    @Test
    public void callToGetClientShouldReturnClient() {
        Object result = cut.getInstance();

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
        ClientInstance<Object> uneuqual = DefaultClientInstance.of(new Object());

        assertThat(cut).isNotEqualTo(uneuqual);
        assertThat(cut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);

    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ClientInstance<Object> equal = DefaultClientInstance.of(client, contract);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains(
                "DefaultClientInstance",
                "client",
                "contract"
        );
    }
}
