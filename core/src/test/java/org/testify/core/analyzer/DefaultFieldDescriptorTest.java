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
package org.testify.core.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testify.FieldDescriptor;
import org.testify.fixture.TestFieldService;

/**
 *
 * @author saden
 */
public class DefaultFieldDescriptorTest {

    FieldDescriptor cut;
    Field field;

    @Before
    public void init() throws NoSuchFieldException {
        field = TestFieldService.class.getDeclaredField("cut");
        cut = new DefaultFieldDescriptor(field);
    }

    @Test
    public void givenFieldOfShouldReturnFieldDescriptor() {
        cut = DefaultFieldDescriptor.of(field);

        assertThat(cut).isNotNull();
        assertThat(cut.getMember()).isEqualTo(field);
    }

    @Test
    public void callToGetMemberShouldReturnField() {
        Field result = cut.getMember();

        assertThat(result).isEqualTo(field);
    }

    @Test
    public void callToGetTypeShouldReturnType() {
        Class<?> result = cut.getType();

        assertThat(result).isEqualTo(field.getType());
    }

    @Test
    public void callToGetGenericTypeShouldReturnGenericType() {
        Type result = cut.getGenericType();

        assertThat(result).isEqualTo(field.getGenericType());
    }

    @Test
    public void callToGetDefinedNameShouldReturnFieldName() {
        String result = cut.getDefinedName();

        assertThat(result).isEqualTo(field.getName());
    }

    @Test
    public void callToGetDefinedNameForRealFieldShouldReturnRealFieldName() throws NoSuchFieldException {
        String name = "real";
        field = TestFieldService.class.getDeclaredField(name);
        cut = new DefaultFieldDescriptor(field);

        String result = cut.getDefinedName();

        assertThat(result).isEqualTo(name);
    }

    @Test
    public void callToGetDefinedNameForFakeFieldShouldReturnFakeFieldName() throws NoSuchFieldException {
        String name = "fake";
        field = TestFieldService.class.getDeclaredField(name);
        cut = new DefaultFieldDescriptor(field);

        String result = cut.getDefinedName();

        assertThat(result).isEqualTo(name);
    }

    @Test
    public void callToGetDefinedNameForVirtualFieldShouldReturnVirtualFieldName() throws NoSuchFieldException {
        String name = "virtual";
        field = TestFieldService.class.getDeclaredField(name);
        cut = new DefaultFieldDescriptor(field);

        String result = cut.getDefinedName();

        assertThat(result).isEqualTo(name);
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
        FieldDescriptor unequal = DefaultFieldDescriptor.of(null);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        FieldDescriptor equal = DefaultFieldDescriptor.of(field);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultFieldDescriptor", "field");
    }
}
