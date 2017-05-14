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

    ClientInstance<Object> sut;
    Object client;
    Class<Object> contract;

    @Before
    public void init() {
        client = new Object();
        contract = Object.class;

        sut = DefaultClientInstance.of(client, contract);

        assertThat(sut.getInstance()).isEqualTo(client);
        assertThat(sut.getContract()).contains(contract);
    }

    @Test
    public void givenBaseURIAndClientOfShouldReturn() {
        sut = DefaultClientInstance.of(client);

        assertThat(sut).isNotNull();
        assertThat(sut.getInstance()).isEqualTo(client);
        assertThat(sut.getContract()).isEmpty();
    }

    @Test
    public void callToGetClientShouldReturnClient() {
        Object result = sut.getInstance();

        assertThat(result).isEqualTo(client);
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        ClientInstance<Object> instance = null;

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
        ClientInstance<Object> uneuqual = DefaultClientInstance.of(new Object());

        assertThat(sut).isNotEqualTo(uneuqual);
        assertThat(sut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);

    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ClientInstance<Object> equal = DefaultClientInstance.of(client, contract);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains(
                "DefaultClientInstance",
                "instance",
                "contract"
        );
    }
}
