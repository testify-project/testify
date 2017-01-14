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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import static java.security.AccessController.doPrivileged;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import java.util.stream.Collectors;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.ParameterDescriptor;
import org.testify.guava.common.reflect.TypeToken;

/**
 * A descriptor class used to access properties of or perform operations on an
 * analyzed class under test (CUT) class.
 *
 * @author saden
 */
public class DefaultCutDescriptor implements CutDescriptor {

    private final Field field;
    private Constructor<?> constructor;
    private List<FieldDescriptor> fieldDescriptors;
    private Map<DescriptorKey, FieldDescriptor> fieldCache;
    private List<ParameterDescriptor> parameterDescriptors;
    private Map<DescriptorKey, ParameterDescriptor> parameterCache;

    public DefaultCutDescriptor(Field field) {
        this.field = field;
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
    public Object newInstance(Object... args) {
        return doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                constructor.setAccessible(true);

                return constructor.newInstance(args);
            } catch (SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }

        });
    }

    @Override
    public Constructor<?> getConstructor() {
        return constructor;
    }

    @Override
    public ClassLoader getClassLoader() {
        return getType().getClassLoader();
    }

    @Override
    public Boolean isCutClass(Type type) {
        return TypeToken.of(getType()).isSupertypeOf(type);
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type, String name) {
        return ofNullable(fieldCache.get(new DescriptorKey(type, name)));
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type) {
        List<FieldDescriptor> matches = fieldCache.values()
                .parallelStream()
                .filter(p -> p.isSupertypeOf(type))
                .collect(Collectors.toList());

        return matches.size() == 1 ? ofNullable(matches.get(0)) : empty();
    }

    @Override
    public List<FieldDescriptor> getFieldDescriptors() {
        return fieldDescriptors;
    }

    @Override
    public Optional<ParameterDescriptor> findParameterDescriptor(Type type, String name) {
        return ofNullable(parameterCache.get(new DescriptorKey(type, name)));
    }

    @Override
    public List<ParameterDescriptor> getParameterDescriptors() {
        return parameterDescriptors;
    }

    @Override
    public Optional<ParameterDescriptor> findParameterDescriptor(Type type) {
        List<ParameterDescriptor> matches = parameterCache.values()
                .parallelStream()
                .filter(p -> p.isSupertypeOf(type))
                .collect(Collectors.toList());

        return matches.size() == 1 ? ofNullable(matches.get(0)) : empty();
    }

    void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    void setFieldDescriptors(List<FieldDescriptor> fieldDescriptors) {
        this.fieldDescriptors = fieldDescriptors;
    }

    void setFieldCache(Map<DescriptorKey, FieldDescriptor> fieldTypeAndNameCache) {
        this.fieldCache = fieldTypeAndNameCache;
    }

    void setParameterDescriptors(List<ParameterDescriptor> parameterDescriptors) {
        this.parameterDescriptors = parameterDescriptors;
    }

    void setParameterCache(Map<DescriptorKey, ParameterDescriptor> parameterCache) {
        this.parameterCache = parameterCache;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.field);
        hash = 53 * hash + Objects.hashCode(this.constructor);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultCutDescriptor other = (DefaultCutDescriptor) obj;
        if (!Objects.equals(this.field, other.field)) {
            return false;
        }

        return Objects.equals(this.constructor, other.constructor);
    }

    @Override
    public String toString() {
        return "CutDescriptor{" + "field=" + field + ", constructor=" + constructor + '}';
    }

}
