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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.ParameterDescriptor;
import org.testify.guava.common.collect.ImmutableList;
import org.testify.guava.common.collect.ImmutableMap;

/**
 * A builder class that facilitates the construction of a {@link CutDescriptor}
 * instance.
 *
 * @author saaden
 */
public class CutDescriptorBuilder {

    private final Field field;
    private Constructor<?> constructor;
    private final ImmutableList.Builder<FieldDescriptor> fieldDescriptors = ImmutableList.builder();
    private final ImmutableMap.Builder<DescriptorKey, FieldDescriptor> fieldCache = ImmutableMap.builder();
    private final ImmutableList.Builder<ParameterDescriptor> parameterDescriptors = ImmutableList.builder();
    private final ImmutableMap.Builder<DescriptorKey, ParameterDescriptor> parameterCache = ImmutableMap.builder();

    public CutDescriptorBuilder(Field field) {
        this.field = field;
    }

    public CutDescriptorBuilder constructor(Constructor<?> constructor) {
        this.constructor = constructor;

        return this;
    }

    public CutDescriptorBuilder addFieldDescriptor(FieldDescriptor descriptor) {
        Type type = descriptor.getGenericType();
        String name = descriptor.getName();

        this.fieldCache.put(new DescriptorKey(type, name), descriptor);
        this.fieldDescriptors.add(descriptor);

        return this;
    }

    public CutDescriptorBuilder addParameterDescriptor(ParameterDescriptor descriptor) {
        Type type = descriptor.getGenericType();
        String name = descriptor.getName();

        this.parameterDescriptors.add(descriptor);
        this.parameterCache.put(new DescriptorKey(type, name), descriptor);

        return this;
    }

    public CutDescriptor build() {
        DefaultCutDescriptor descriptor = new DefaultCutDescriptor(field);
        descriptor.setConstructor(constructor);
        descriptor.setFieldDescriptors(fieldDescriptors.build());
        descriptor.setFieldCache(fieldCache.build());
        descriptor.setParameterDescriptors(parameterDescriptors.build());
        descriptor.setParameterCache(parameterCache.build());

        return descriptor;
    }

}
