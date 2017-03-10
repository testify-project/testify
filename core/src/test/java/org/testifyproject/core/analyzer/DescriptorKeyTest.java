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

    DescriptorKey cut;

    Class type;
    String name;

    @Before
    public void init() {
        type = Object.class;
        name = "name";

        cut = DescriptorKey.of(type, name);
    }

    @Test
    public void validateCut() {
        assertThat(cut).isNotNull();
        assertThat(cut.getType()).isEqualTo(type);
        assertThat(cut.getName()).isEqualTo(name);
    }

    @Test
    public void givenDescriptorKeyOfTypeShouldReturn() {
        cut = DescriptorKey.of(type);

        assertThat(cut).isNotNull();
        assertThat(cut.getType()).isEqualTo(type);
        assertThat(cut.getName()).isNull();
    }

    @Test
    public void givenDescriptorKeyOfTypeAndNameShouldReturn() {
        cut = DescriptorKey.of(type, name);

        assertThat(cut).isNotNull();
        assertThat(cut.getType()).isEqualTo(type);
        assertThat(cut.getName()).isEqualTo(name);
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
        DescriptorKey unequal = DescriptorKey.of(type, null);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        DescriptorKey equal = DescriptorKey.of(type, name);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DescriptorKey", "type", "name");
    }

}
