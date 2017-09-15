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
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.SutDescriptor;
import org.testifyproject.fixture.analyzer.AnalyzedSutClass;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class DefaultSutDescriptorTest {

    SutDescriptor sut;
    Field field;
    Map properties;

    @Before
    public void init() throws NoSuchFieldException {
        field = AnalyzedTestClass.class.getDeclaredField("sut");
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        sut = DefaultSutDescriptor.of(field, properties);
    }

    @Test
    public void givenFieldOfShouldReturnSutDescriptor() {
        sut = DefaultSutDescriptor.of(field);

        assertThat(sut).isNotNull();
        assertThat(sut.getMember()).isEqualTo(field);
    }

    @Test
    public void callToGetPropertiesShouldReturnProperties() {
        Map<String, Object> result = sut.getProperties();

        assertThat(result).isEqualTo(properties);
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
    public void callToGetClassLoaderShouldReturnClassLoader() {
        ClassLoader result = sut.getClassLoader();

        assertThat(result).isEqualTo(field.getType().getClassLoader());
    }

    @Test
    public void callToGetConstructorShouldReturnConstructor() throws NoSuchMethodException {
        Constructor constructor = field.getType().getDeclaredConstructor(Map.class);
        properties.put(SutDescriptorProperties.CONSTRUCTOR, constructor);

        Constructor result = sut.getConstructor();

        assertThat(result).isEqualTo(constructor);
    }

    @Test
    public void givenNonSutTypeIsSutClassShouldReturnFalse() {
        Boolean result = sut.isSutClass(String.class);

        assertThat(result).isFalse();
    }

    @Test
    public void givenSutTypeIsSutClassShouldReturnFalse() {
        Boolean result = sut.isSutClass(AnalyzedSutClass.class);

        assertThat(result).isTrue();
    }

    @Test
    public void callToGetFieldDescriptorsShouldReturn() {
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        List<FieldDescriptor> value = ImmutableList.of(fieldDescriptor);
        properties.put(SutDescriptorProperties.FIELD_DESCRIPTORS, value);

        Collection<FieldDescriptor> result = sut.getFieldDescriptors();

        assertThat(result).containsExactly(fieldDescriptor);
    }

    @Test
    public void givenTypeFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey,
                fieldDescriptor);
        properties.put(SutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Optional<FieldDescriptor> result = sut.findFieldDescriptor(type);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void givenSubtypeFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<Collection> type = Collection.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey,
                fieldDescriptor);
        properties.put(SutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);
        Class<List> searchType = List.class;

        given(fieldDescriptor.isSupertypeOf(searchType)).willReturn(true);

        Optional<FieldDescriptor> result = sut.findFieldDescriptor(searchType);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void givenTypeAndNameFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<Object> type = Object.class;
        String name = "name";
        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey,
                fieldDescriptor);
        properties.put(SutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Optional<FieldDescriptor> result = sut.findFieldDescriptor(type, name);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void callToGetParameterDescriptorsShouldReturnParameterDescriptors() {
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        List< ParameterDescriptor> value = ImmutableList.of(parameterDescriptor);
        properties.put(SutDescriptorProperties.PARAMETER_DESCRIPTORS, value);

        Collection<ParameterDescriptor> result = sut.getParameterDescriptors();

        assertThat(result).containsExactly(parameterDescriptor);
    }

    @Test
    public void givenTypeFindParameterDescriptorShouldReturnParameterDescriptor() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        Map<DescriptorKey, ParameterDescriptor> value = ImmutableMap.of(descriptorKey,
                parameterDescriptor);
        properties.put(SutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, value);

        Optional<ParameterDescriptor> result = sut.findParameterDescriptor(type);

        assertThat(result).contains(parameterDescriptor);
    }

    @Test
    public void givenSubtypeFindParameterDescriptorShouldReturnParameterDescriptor() {
        Class<Collection> type = Collection.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        Map<DescriptorKey, ParameterDescriptor> value = ImmutableMap.of(descriptorKey,
                parameterDescriptor);
        properties.put(SutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, value);
        Class<List> searchType = List.class;

        given(parameterDescriptor.isSupertypeOf(searchType)).willReturn(true);

        Optional<ParameterDescriptor> result = sut.findParameterDescriptor(searchType);

        assertThat(result).contains(parameterDescriptor);
    }

    @Test
    public void givenTypeAndNameFindParameterDescriptorShouldReturnParameterDescriptor() {
        Class<Object> type = Object.class;
        String name = "name";
        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        Map<DescriptorKey, ParameterDescriptor> value = ImmutableMap.of(descriptorKey,
                parameterDescriptor);
        properties.put(SutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, value);

        Optional<ParameterDescriptor> result = sut.findParameterDescriptor(type, name);

        assertThat(result).contains(parameterDescriptor);
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
        DefaultSutDescriptor unequal = DefaultSutDescriptor.of(null);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        DefaultSutDescriptor equal = DefaultSutDescriptor.of(field, properties);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultSutDescriptor", "field");
    }

}
