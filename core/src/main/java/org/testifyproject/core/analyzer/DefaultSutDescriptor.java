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
import java.util.Optional;
import static java.util.Optional.ofNullable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.SutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * A descriptor class used to access properties of or perform operations on an
 * analyzed system under test (SUT) class.
 *
 * @author saden
 */
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class DefaultSutDescriptor extends DefaultFieldDescriptor implements SutDescriptor {

    private final Map<String, Object> properties;

    DefaultSutDescriptor(Field field, Map<String, Object> properties) {
        super(field);
        this.properties = properties;
    }

    /**
     * Create a new sut descriptor instance from the given field.
     *
     * @param field the underlying field
     * @return a sut descriptor instance
     */
    public static DefaultSutDescriptor of(Field field) {
        return new DefaultSutDescriptor(field, new HashMap<>());
    }

    /**
     * Create a new sut descriptor instance from the given field and properties.
     *
     * @param field the underlying field
     * @param properties the underlying properties
     * @return a sut descriptor instance
     */
    public static DefaultSutDescriptor of(Field field, Map<String, Object> properties) {
        return new DefaultSutDescriptor(field, properties);
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public ClassLoader getClassLoader() {
        return getType().getClassLoader();
    }

    @Override
    public Constructor getConstructor() {
        return getProperty(SutDescriptorProperties.CONSTRUCTOR);
    }

    @Override
    public Boolean isSutClass(Type type) {
        return TypeToken.of(getType()).isSupertypeOf(type);
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type) {
        Map<DescriptorKey, FieldDescriptor> fieldDescriptors
                = findMap(SutDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor fieldDescriptor = fieldDescriptors.get(descriptorKey);

        //XXX: maybe the type is a subtype of a field? maybe we are trying to be
        //too smart for our own good here?
        if (fieldDescriptor == null) {
            fieldDescriptor = fieldDescriptors.values().parallelStream()
                    .filter(p -> p.isSupertypeOf(type))
                    .findFirst()
                    .orElse(null);
        }

        return ofNullable(fieldDescriptor);
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type, String name) {
        Map<DescriptorKey, FieldDescriptor> fieldDescriptors
                = findMap(SutDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        FieldDescriptor fieldDescriptor = fieldDescriptors.get(descriptorKey);

        return ofNullable(fieldDescriptor);
    }

    @Override
    public Collection<FieldDescriptor> getFieldDescriptors() {
        return findList(SutDescriptorProperties.FIELD_DESCRIPTORS);
    }

    @Override
    public Optional<ParameterDescriptor> findParameterDescriptor(Type type) {
        Map<DescriptorKey, ParameterDescriptor> paramterDescriptors
                = findMap(SutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type);
        ParameterDescriptor parameterDescriptor = paramterDescriptors.get(descriptorKey);

        //XXX: maybe the type is a subtype of a field? maybe we are trying to be
        //too smart for our own good here?
        if (parameterDescriptor == null) {
            parameterDescriptor = paramterDescriptors.values().parallelStream()
                    .filter(p -> p.isSupertypeOf(type))
                    .findFirst()
                    .orElse(null);
        }

        return ofNullable(parameterDescriptor);
    }

    @Override
    public Optional<ParameterDescriptor> findParameterDescriptor(Type type, String name) {
        Map<DescriptorKey, ParameterDescriptor> paramterDescriptors
                = findMap(SutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        ParameterDescriptor parameterDescriptor = paramterDescriptors.get(descriptorKey);

        return ofNullable(parameterDescriptor);
    }

    @Override
    public Collection<ParameterDescriptor> getParameterDescriptors() {
        return findList(SutDescriptorProperties.PARAMETER_DESCRIPTORS);
    }

}
