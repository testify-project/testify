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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testify.FieldDescriptor;
import org.testify.MethodDescriptor;
import org.testify.TestDescriptor;
import org.testify.annotation.Application;
import org.testify.annotation.Module;
import org.testify.annotation.RequiresContainer;
import org.testify.annotation.RequiresResource;
import org.testify.annotation.Scan;

/**
 * A descriptor class used to access or perform operations on a test class.
 *
 * @author saden
 */
public class DefaultTestDescriptor implements TestDescriptor {

    private final Map<String, Object> properties;
    private final Class<?> testClass;

    /**
     * Create a new test descriptors for the given test class.
     *
     * @param testClass the test class the test descriptor describes
     * @return a test descriptor instance
     */
    public static TestDescriptor of(Class<?> testClass) {
        return new DefaultTestDescriptor(testClass, new HashMap<>());
    }

    DefaultTestDescriptor(Class<?> testClass, Map<String, Object> properties) {
        this.testClass = testClass;
        this.properties = properties;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public Class<?> getTestClass() {
        return testClass;
    }

    @Override
    public ClassLoader getTestClassLoader() {
        return testClass.getClassLoader();
    }

    @Override
    public String getTestClassName() {
        return testClass.getSimpleName();
    }

    @Override
    public Optional<Application> getApplication() {
        return findProperty(TestDescriptorProperties.APPLICATION);
    }

    @Override
    public Optional<Field> getCutField() {
        return findProperty(TestDescriptorProperties.CUT_FIELD);
    }

    @Override
    public List<Module> getModules() {
        return findList(TestDescriptorProperties.MODULES);
    }

    @Override
    public List<Scan> getScans() {
        return findList(TestDescriptorProperties.SCANS);
    }

    @Override
    public List<RequiresResource> getRequiresResources() {
        return findList(TestDescriptorProperties.REQUIRES_RESOURCES);
    }

    @Override
    public List<RequiresContainer> getRequiresContainers() {
        return findList(TestDescriptorProperties.REQUIRES_CONTAINERS);
    }

    @Override
    public Optional<MethodDescriptor> getCollaboratorProvider() {
        return findProperty(TestDescriptorProperties.COLLABORATOR_PROVIDER);
    }

    @Override
    public List<MethodDescriptor> getConfigHandlers() {
        return findList(TestDescriptorProperties.CONFIG_HANDLERS);
    }

    @Override
    public Collection<FieldDescriptor> getFieldDescriptors() {
        return findList(TestDescriptorProperties.FIELD_DESCRIPTORS);
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type) {
        Map<DescriptorKey, FieldDescriptor> fieldDescriptors = findMap(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

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
                = findMap(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        FieldDescriptor fieldDescriptor = fieldDescriptors.get(descriptorKey);

        return ofNullable(fieldDescriptor);
    }

    @Override
    public Optional<MethodDescriptor> findConfigHandler(Type parameterType) {
        return getConfigHandlers()
                .parallelStream()
                .filter(p -> p.hasParameterTypes(parameterType))
                .findFirst();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.testClass);
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
        final DefaultTestDescriptor other = (DefaultTestDescriptor) obj;
        return Objects.equals(this.testClass, other.testClass);
    }

    @Override
    public String toString() {
        return "DefaultTestDescriptor{" + "testClass=" + testClass + '}';
    }

}
