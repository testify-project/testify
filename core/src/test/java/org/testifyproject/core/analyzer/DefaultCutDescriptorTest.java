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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class DefaultCutDescriptorTest {

    CutDescriptor cut;
    Field field;
    Map properties;

    @Before
    public void init() throws NoSuchFieldException {
        field = AnalyzedTestClass.class.getDeclaredField("cut");
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        cut = DefaultCutDescriptor.of(field, properties);
    }

    @Test
    public void givenFieldOfShouldReturnCutDescriptor() {
        cut = DefaultCutDescriptor.of(field);

        assertThat(cut).isNotNull();
        assertThat(cut.getMember()).isEqualTo(field);
    }

    @Test
    public void callToGetPropertiesShouldReturnProperties() {
        Map<String, Object> result = cut.getProperties();

        assertThat(result).isEqualTo(properties);
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
    public void callToGetClassLoaderShouldReturnClassLoader() {
        ClassLoader result = cut.getClassLoader();

        assertThat(result).isEqualTo(field.getType().getClassLoader());
    }

    @Test
    public void callToGetConstructorShouldReturnConstructor() throws NoSuchMethodException {
        Constructor constructor = field.getType().getDeclaredConstructor(Map.class);
        properties.put(CutDescriptorProperties.CONSTRUCTOR, constructor);

        Constructor result = cut.getConstructor();

        assertThat(result).isEqualTo(constructor);
    }

    @Test
    public void callToGetFieldDescriptorsShouldReturn() {
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        List<FieldDescriptor> value = ImmutableList.of(fieldDescriptor);
        properties.put(CutDescriptorProperties.FIELD_DESCRIPTORS, value);

        Collection<FieldDescriptor> result = cut.getFieldDescriptors();

        assertThat(result).containsExactly(fieldDescriptor);
    }

    @Test
    public void givenTypeFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey, fieldDescriptor);
        properties.put(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Optional<FieldDescriptor> result = cut.findFieldDescriptor(type);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void givenTypeAndNameFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<Object> type = Object.class;
        String name = "name";
        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey, fieldDescriptor);
        properties.put(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Optional<FieldDescriptor> result = cut.findFieldDescriptor(type, name);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void callToGetParameterDescriptorsShouldReturnParameterDescriptors() {
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        List< ParameterDescriptor> value = ImmutableList.of(parameterDescriptor);
        properties.put(CutDescriptorProperties.PARAMETER_DESCRIPTORS, value);

        Collection<ParameterDescriptor> result = cut.getParameterDescriptors();

        assertThat(result).containsExactly(parameterDescriptor);
    }

    @Test
    public void givenTypeFindParameterDescriptorShouldReturnParameterDescriptor() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        Map<DescriptorKey, ParameterDescriptor> value = ImmutableMap.of(descriptorKey, parameterDescriptor);
        properties.put(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, value);

        Optional<ParameterDescriptor> result = cut.findParameterDescriptor(type);

        assertThat(result).contains(parameterDescriptor);
    }

    @Test
    public void givenTypeAndNameFindParameterDescriptorShouldReturnParameterDescriptor() {
        Class<Object> type = Object.class;
        String name = "name";
        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        ParameterDescriptor parameterDescriptor = mock(ParameterDescriptor.class);
        Map<DescriptorKey, ParameterDescriptor> value = ImmutableMap.of(descriptorKey, parameterDescriptor);
        properties.put(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, value);

        Optional<ParameterDescriptor> result = cut.findParameterDescriptor(type, name);

        assertThat(result).contains(parameterDescriptor);
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
        DefaultCutDescriptor unequal = DefaultCutDescriptor.of(null);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        DefaultCutDescriptor equal = DefaultCutDescriptor.of(field, properties);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultCutDescriptor", "field");
    }

}
