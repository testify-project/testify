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
import org.testify.FieldDescriptor;
import org.testify.MethodDescriptor;
import org.testify.TestDescriptor;
import org.testify.annotation.Application;
import org.testify.annotation.Module;
import org.testify.annotation.RequiresContainer;
import org.testify.annotation.RequiresResource;
import org.testify.annotation.Scan;
import org.testify.fixture.analyzer.AnalyzedTestClass;
import org.testify.guava.common.collect.ImmutableList;
import org.testify.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class DefaultTestDescriptorTest {

    TestDescriptor cut;
    Class<AnalyzedTestClass> testClass;
    Map<String, Object> properties;

    @Before
    public void init() {
        testClass = AnalyzedTestClass.class;
        properties = mock(Map.class, delegatesTo(new HashMap<>()));

        cut = new DefaultTestDescriptor(testClass, properties);
    }

    @Test
    public void givenInstanceOfShouldReturn() {
        TestDescriptor result = DefaultTestDescriptor.of(testClass);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetPropertiesShouldReturnProperties() {
        Map<String, Object> result = cut.getProperties();

        assertThat(result).isEqualTo(properties);
    }

    @Test
    public void callToGetTestClassShouldReturn() {
        Class<?> result = cut.getTestClass();

        assertThat(result).isEqualTo(testClass);
    }

    @Test
    public void callToGetTestClassLoaderShouldReturn() {
        ClassLoader result = cut.getTestClassLoader();

        assertThat(result).isEqualTo(testClass.getClassLoader());
    }

    @Test
    public void callToGetTestClassNameShouldReturn() {
        String result = cut.getTestClassName();

        assertThat(result).isEqualTo(testClass.getSimpleName());
    }

    @Test
    public void callToGetApplicationShouldReturn() {
        Optional<Application> result = cut.getApplication();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetCutFieldShouldReturn() {
        Optional<Field> result = cut.getCutField();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetModulesShouldReturn() {
        Module value = mock(Module.class);
        properties.put(TestDescriptorProperties.MODULES, ImmutableList.of(value));

        List<Module> result = cut.getModules();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetScansShouldReturn() {
        Scan value = mock(Scan.class);
        properties.put(TestDescriptorProperties.SCANS, ImmutableList.of(value));

        List<Scan> result = cut.getScans();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetRequiresResourcesShouldReturn() {
        RequiresResource value = mock(RequiresResource.class);
        properties.put(TestDescriptorProperties.REQUIRES_RESOURCES, ImmutableList.of(value));

        List<RequiresResource> result = cut.getRequiresResources();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetRequiresContainersShouldReturn() {
        RequiresContainer value = mock(RequiresContainer.class);
        properties.put(TestDescriptorProperties.REQUIRES_CONTAINERS, ImmutableList.of(value));

        List<RequiresContainer> result = cut.getRequiresContainers();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetCollaboratorProviderShouldReturn() {
        Optional<MethodDescriptor> result = cut.getCollaboratorProvider();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetConfigHandlersShouldReturn() {
        MethodDescriptor value = mock(MethodDescriptor.class);
        properties.put(TestDescriptorProperties.CONFIG_HANDLERS, ImmutableList.of(value));

        List<MethodDescriptor> result = cut.getConfigHandlers();

        assertThat(result).containsExactly(value);
    }

    @Test
    public void callToGetFieldDescriptorsShouldReturn() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        List<FieldDescriptor> value = ImmutableList.of(fieldDescriptor);
        properties.put(TestDescriptorProperties.FIELD_DESCRIPTORS, value);

        Collection<FieldDescriptor> result = cut.getFieldDescriptors();

        assertThat(result).containsExactly(fieldDescriptor);
    }

    @Test
    public void givenTypeFindFieldDescriptorShouldReturnFieldDescriptor() {
        Class<Object> type = Object.class;
        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Map<DescriptorKey, FieldDescriptor> value = ImmutableMap.of(descriptorKey, fieldDescriptor);
        properties.put(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

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
        properties.put(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE, value);

        Optional<FieldDescriptor> result = cut.findFieldDescriptor(type, name);

        assertThat(result).contains(fieldDescriptor);
    }

    @Test
    public void givenParamterTypeFindConfigHandlerShouldReturn() {
        Class<Object> parameterType = Object.class;
        MethodDescriptor value = mock(MethodDescriptor.class);
        properties.put(TestDescriptorProperties.CONFIG_HANDLERS, ImmutableList.of(value));
        given(value.hasParameterTypes(parameterType)).willReturn(Boolean.TRUE);

        Optional<MethodDescriptor> result = cut.findConfigHandler(parameterType);

        assertThat(result).contains(value);
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
        TestDescriptor unequal = DefaultTestDescriptor.of(Class.class);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        TestDescriptor equal = DefaultTestDescriptor.of(testClass);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultTestDescriptor", "testClass");
    }

}
