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

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.fixture.TestFieldService;

/**
 *
 * @author saden
 */
public class DefaultFieldDescriptorTest {

    FieldDescriptor sut;
    Field field;

    @Before
    public void init() throws NoSuchFieldException {
        field = TestFieldService.class.getDeclaredField("sut");
        sut = new DefaultFieldDescriptor(field);
    }

    @Test
    public void givenFieldOfShouldReturnFieldDescriptor() {
        sut = DefaultFieldDescriptor.of(field);

        assertThat(sut).isNotNull();
        assertThat(sut.getMember()).isEqualTo(field);
    }

    @Test
    public void callToGetMemberShouldReturnField() {
        Field result = sut.getMember();

        assertThat(result).isEqualTo(field);
    }

    @Test
    public void callToGetTypeShouldReturnType() {
        Class<?> result = sut.getType();

        assertThat(result).isEqualTo(field.getType());
    }

    @Test
    public void callToGetGenericTypeShouldReturnGenericType() {
        Type result = sut.getGenericType();

        assertThat(result).isEqualTo(field.getGenericType());
    }

    @Test
    public void callToGetDeclaredNameShouldReturnFieldName() {
        String result = sut.getDeclaredName();

        assertThat(result).isEqualTo(field.getName());
    }

    @Test
    public void callToGetDeclaredNameWithoutNameShouldReturnFieldName() throws
            NoSuchFieldException {
        String name = "named";
        field = TestFieldService.class.getDeclaredField(name);
        sut = new DefaultFieldDescriptor(field);

        String result = sut.getDeclaredName();

        assertThat(result).isEqualTo("test");
    }

    @Test
    public void callToGetDeclaredNameWithNameShouldReturnName() throws NoSuchFieldException {
        String name = "unnamed";
        field = TestFieldService.class.getDeclaredField(name);
        sut = new DefaultFieldDescriptor(field);

        String result = sut.getDeclaredName();

        assertThat(result).isEqualTo(name);
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
        FieldDescriptor unequal = DefaultFieldDescriptor.of(null);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        FieldDescriptor equal = DefaultFieldDescriptor.of(field);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultFieldDescriptor", "field");
    }
}
