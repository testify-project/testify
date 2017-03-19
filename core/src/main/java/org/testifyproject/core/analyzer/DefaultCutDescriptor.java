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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * A descriptor class used to access properties of or perform operations on an
 * analyzed class under test (CUT) class.
 *
 * @author saden
 */
public class DefaultCutDescriptor implements CutDescriptor {

    private final Field field;
    private final Map<String, Object> properties;

    public static DefaultCutDescriptor of(Field field) {
        return new DefaultCutDescriptor(field, new HashMap<>());
    }

    DefaultCutDescriptor(Field field, Map<String, Object> properties) {
        this.field = field;
        this.properties = properties;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public Field getMember() {
        return field;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public ClassLoader getClassLoader() {
        return getType().getClassLoader();
    }

    @Override
    public Constructor<?> getConstructor() {
        Optional<Constructor<?>> result = findProperty(CutDescriptorProperties.CONSTRUCTOR);

        return result.get();
    }

    @Override
    public Boolean isCutClass(Type type) {
        return TypeToken.of(getType()).isSupertypeOf(type);
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type) {
        Map<DescriptorKey, FieldDescriptor> fieldDescriptors
                = findMap(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = fieldDescriptors.get(descriptorKey);

        if (fieldDescriptor == null) {
            fieldDescriptor = fieldDescriptors.values()
                    .parallelStream()
                    .filter(p -> p.isSupertypeOf(type))
                    .findFirst()
                    .orElse(null);
        }

        return ofNullable(fieldDescriptor);
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type, String name) {
        Map<DescriptorKey, FieldDescriptor> fieldDescriptors
                = findMap(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        FieldDescriptor fieldDescriptor = fieldDescriptors.get(descriptorKey);

        return ofNullable(fieldDescriptor);
    }

    @Override
    public Collection<FieldDescriptor> getFieldDescriptors() {
        return findList(CutDescriptorProperties.FIELD_DESCRIPTORS);
    }

    @Override
    public Optional<ParameterDescriptor> findParameterDescriptor(Type type) {
        Map<DescriptorKey, ParameterDescriptor> paramterDescriptors
                = findMap(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type);
        ParameterDescriptor parameterDescriptor = paramterDescriptors.get(descriptorKey);

        if (parameterDescriptor == null) {
            parameterDescriptor = paramterDescriptors.values()
                    .parallelStream()
                    .filter(p -> p.isSupertypeOf(type))
                    .findFirst()
                    .orElse(null);
        }

        return ofNullable(parameterDescriptor);
    }

    @Override
    public Optional<ParameterDescriptor> findParameterDescriptor(Type type, String name) {
        Map<DescriptorKey, ParameterDescriptor> paramterDescriptors
                = findMap(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        ParameterDescriptor parameterDescriptor = paramterDescriptors.get(descriptorKey);

        return ofNullable(parameterDescriptor);
    }

    @Override
    public Collection<ParameterDescriptor> getParameterDescriptors() {
        return findList(CutDescriptorProperties.PARAMETER_DESCRIPTORS);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.field);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultCutDescriptor other = (DefaultCutDescriptor) obj;

        return Objects.equals(this.field, other.field);
    }

    @Override
    public String toString() {
        return "DefaultCutDescriptor{" + "field=" + field + '}';
    }

}
