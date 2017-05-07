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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class DefaultTestDescriptorTest {

    TestDescriptor sut;
    Class<AnalyzedTestClass> testClass;
    Map<String, Object> properties;

    @Before
    public void init() {
        testClass = AnalyzedTestClass.class;
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        sut = DefaultTestDescriptor.of(testClass, properties);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        TestDescriptor result = DefaultTestDescriptor.of(testClass);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetPropertiesShouldReturnProperties() {
        Map<String, Object> result = sut.getProperties();

        assertThat(result).isEqualTo(properties);
    }

    @Test
    public void callToGetTestClassShouldReturn() {
        Class<?> result = sut.getTestClass();

        assertThat(result).isEqualTo(testClass);
    }

    @Test
    public void callToGetTestClassLoaderShouldReturn() {
        ClassLoader result = sut.getTestClassLoader();

        assertThat(result).isEqualTo(testClass.getClassLoader());
    }

    @Test
    public void callToGetTestClassNameShouldReturn() {
        String result = sut.getTestClassName();

        assertThat(result).isEqualTo(testClass.getSimpleName());
    }

    @Test
    public void callToGetApplicationShouldReturn() {
        Optional<Application> result = sut.getApplication();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetSutFieldShouldReturn() {
        Optional<Field> result = sut.getSutField();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetModulesShouldReturn() {
        Module value = mock(Module.class);
        properties.put(TestDescriptorProperties.MODULES, ImmutableList.of(value));

        List<Module> result = sut.getModules();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetScansShouldReturn() {
        Scan value = mock(Scan.class);
        properties.put(TestDescriptorProperties.SCANS, ImmutableList.of(value));

        List<Scan> result = sut.getScans();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetLocalResourcesShouldReturn() {
        LocalResource value = mock(LocalResource.class);
        properties.put(TestDescriptorProperties.LOCAL_RESOURCES, ImmutableList.of(value));

        List<LocalResource> result = sut.getLocalResources();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetVirtualResourcesShouldReturn() {
        VirtualResource value = mock(VirtualResource.class);
        properties.put(TestDescriptorProperties.VIRTUAL_RESOURCES, ImmutableList.of(value));

        List<VirtualResource> result = sut.getVirtualResources();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetCollaboratorProviderShouldReturn() {
        Optional<MethodDescriptor> result = sut.getCollaboratorProvider();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetConfigHandlersShouldReturn() {
        MethodDescriptor value = mock(MethodDescriptor.class);
        properties.put(TestDescriptorProperties.CONFIG_HANDLERS, ImmutableList.of(value));

        List<MethodDescriptor> result = sut.getConfigHandlers();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetFieldDescriptorsShouldReturn() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        List<FieldDescriptor> value = ImmutableList.of(fieldDescriptor);
        properties.put(TestDescriptorProperties.FIELD_DESCRIPTORS, value);

        Collection<FieldDescriptor> result = sut.getFieldDescriptors();

        assertThat(result).containsExactly(fieldDescriptor);
    }

    @Test
    public void givenTypeFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey, fieldDescriptor);
        properties.put(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Optional<FieldDescriptor> result = sut.findFieldDescriptor(type);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void givenSuperTypeFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<CharSequence> type = CharSequence.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey, fieldDescriptor);
        properties.put(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Class<String> searchType = String.class;
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
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey, fieldDescriptor);
        properties.put(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Optional<FieldDescriptor> result = sut.findFieldDescriptor(type, name);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void givenParamterTypeFindConfigHandlerShouldReturn() {
        Class<Object> parameterType = Object.class;
        MethodDescriptor value = mock(MethodDescriptor.class);
        properties.put(TestDescriptorProperties.CONFIG_HANDLERS, ImmutableList.of(value));
        given(value.hasParameterTypes(parameterType)).willReturn(Boolean.TRUE);

        Optional<MethodDescriptor> result = sut.findConfigHandler(parameterType);

        assertThat(result).contains(value);
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
        TestDescriptor unequal = DefaultTestDescriptor.of(Class.class);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        TestDescriptor equal = DefaultTestDescriptor.of(testClass, properties);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultTestDescriptor", "testClass");
    }

}
