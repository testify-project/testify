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
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.VirtualResource;

/**
 * A descriptor class used to access or perform operations on a test class.
 *
 * @author saden
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class DefaultTestDescriptor implements TestDescriptor {

    private final Map<String, Object> properties;
    private final Class<?> testClass;

    DefaultTestDescriptor(Class<?> testClass, Map<String, Object> properties) {
        this.testClass = testClass;
        this.properties = properties;
    }

    /**
     * Create a new test descriptors for the given test class.
     *
     * @param testClass the test class the test descriptor describes
     * @return a test descriptor instance
     */
    public static TestDescriptor of(Class<?> testClass) {
        return new DefaultTestDescriptor(testClass, new HashMap<>());
    }

    /**
     * Create a new test descriptors for the given test class.
     *
     * @param testClass the test class the test descriptor describes
     * @param properties the underlying properties
     * @return a test descriptor instance
     */
    public static TestDescriptor of(Class<?> testClass, Map<String, Object> properties) {
        return new DefaultTestDescriptor(testClass, properties);
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
    public List<LocalResource> getLocalResources() {
        return findList(TestDescriptorProperties.LOCAL_RESOURCES);
    }

    @Override
    public List<VirtualResource> getVirtualResources() {
        return findList(TestDescriptorProperties.VIRTUAL_RESOURCES);
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
            //TODO: Not sure if we should be this lose in the event we don't
            //find a matching field descriptor. Need to evaluate if this code
            //is useful or harmful.
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

}
