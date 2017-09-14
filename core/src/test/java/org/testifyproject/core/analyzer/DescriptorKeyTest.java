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
package org.testifyproject.core.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author saden
 */
public class DescriptorKeyTest {

    DescriptorKey sut;

    Class type;
    String name;

    @Before
    public void init() {
        type = Object.class;
        name = "name";

        sut = DescriptorKey.of(type, name);
    }

    @Test
    public void validateSut() {
        assertThat(sut).isNotNull();
        assertThat(sut.getType()).isEqualTo(type);
        assertThat(sut.getName()).isEqualTo(name);
    }

    @Test
    public void givenDescriptorKeyOfTypeShouldReturn() {
        sut = DescriptorKey.of(type);

        assertThat(sut).isNotNull();
        assertThat(sut.getType()).isEqualTo(type);
        assertThat(sut.getName()).isNull();
    }

    @Test
    public void givenDescriptorKeyOfTypeAndNameShouldReturn() {
        sut = DescriptorKey.of(type, name);

        assertThat(sut).isNotNull();
        assertThat(sut.getType()).isEqualTo(type);
        assertThat(sut.getName()).isEqualTo(name);
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
        DescriptorKey unequal = DescriptorKey.of(type, null);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        DescriptorKey equal = DescriptorKey.of(type, name);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DescriptorKey", "type", "name");
    }

}
