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
import org.testifyproject.Instance;

/**
 *
 * @author saden
 */
public class DefaultInstanceTest {

    Instance<String> sut;

    String instance;
    String name;
    Class<String> contract;

    @Before
    public void init() {
        instance = "instance";
        name = "name";
        contract = String.class;

        sut = DefaultInstance.of(instance, name, contract);
    }

    @Test
    public void validateSutInstance() {
        assertThat(sut).isNotNull();
        assertThat(sut.getValue()).isEqualTo(instance);
        assertThat(sut.getName()).contains(name);
        assertThat(sut.getContract()).isEqualTo(contract);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        sut = DefaultInstance.of(instance);

        assertThat(sut).isNotNull();
        assertThat(sut.getValue()).isEqualTo(instance);
        assertThat(sut.getName()).isNull();
        assertThat(sut.getContract()).isEqualTo(contract);
    }

    @Test
    public void givenInstanceAndNameOfShouldReturn() {
        sut = DefaultInstance.of(instance, name);

        assertThat(sut).isNotNull();
        assertThat(sut.getValue()).isEqualTo(instance);
        assertThat(sut.getName()).contains(name);
        assertThat(sut.getContract()).isEqualTo(contract);
    }

    @Test
    public void givenInstanceAndContractOfShouldReturn() {
        sut = DefaultInstance.of(instance, contract);

        assertThat(sut).isNotNull();
        assertThat(sut.getValue()).isEqualTo(instance);
        assertThat(sut.getContract()).isEqualTo(contract);
        assertThat(sut.getName()).isNull();
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(sut).isNotEqualTo(null);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        Instance<String> unequal = DefaultInstance.of(instance, contract);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        Instance<String> equal = DefaultInstance.of(instance, name, contract);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultInstance", "instance", "name", "contract");
    }

}
