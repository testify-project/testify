/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.core;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testify.Instance;

/**
 *
 * @author saden
 */
public class DefaultInstanceTest {

    Instance<String> cut;

    String instance;
    String name;
    Class<String> contract;

    @Before
    public void init() {
        instance = "instance";
        name = "name";
        contract = String.class;

        cut = DefaultInstance.of(instance, name, contract);
    }

    @Test
    public void validateCutInstance() {
        assertThat(cut).isNotNull();
        assertThat(cut.getInstance()).isEqualTo(instance);
        assertThat(cut.getName()).contains(name);
        assertThat(cut.getContract()).contains(contract);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        cut = DefaultInstance.of(instance);

        assertThat(cut).isNotNull();
        assertThat(cut.getInstance()).isEqualTo(instance);
        assertThat(cut.getName()).isEmpty();
        assertThat(cut.getContract()).isEmpty();
    }

    @Test
    public void givenInstanceAndNameOfShouldReturn() {
        cut = DefaultInstance.of(instance, name);

        assertThat(cut).isNotNull();
        assertThat(cut.getInstance()).isEqualTo(instance);
        assertThat(cut.getName()).contains(name);
        assertThat(cut.getContract()).isEmpty();
    }

    @Test
    public void givenInstanceAndContractOfShouldReturn() {
        cut = DefaultInstance.of(instance, contract);

        assertThat(cut).isNotNull();
        assertThat(cut.getInstance()).isEqualTo(instance);
        assertThat(cut.getContract()).contains(contract);
        assertThat(cut.getName()).isEmpty();
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(cut).isNotEqualTo(null);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        Instance<String> unequal = DefaultInstance.of(instance, contract);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        Instance<String> equal = DefaultInstance.of(instance, name, contract);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultInstance", "instance", "name", "contract");
    }

}
