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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import java.util.stream.Collectors;
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

    private final Class<?> testClass;
    private Application application;
    private MethodDescriptor collaboratorMethod;
    private Field cutField;
    private List<MethodDescriptor> configHandlers;
    private List<Module> modules;
    private List<Scan> scans;
    private List<RequiresContainer> containers;
    private List<RequiresResource> resources;
    private List<FieldDescriptor> fieldDescriptors;
    private Map<DescriptorKey, FieldDescriptor> fieldCache;

    DefaultTestDescriptor(Class<?> testClass) {
        this.testClass = testClass;
    }

    public static TestDescriptor of(Class<?> testClass) {
        return new DefaultTestDescriptor(testClass);
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
        return ofNullable(application);
    }

    @Override
    public Optional<Field> getCutField() {
        return ofNullable(this.cutField);
    }

    @Override
    public Optional<MethodDescriptor> getCollaboratorProvider() {
        return ofNullable(collaboratorMethod);
    }

    @Override
    public List<RequiresResource> getRequiresResources() {
        return resources;
    }

    @Override
    public List<RequiresContainer> getRequiresContainers() {
        return containers;
    }

    @Override
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public List<Scan> getScans() {
        return scans;
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
    public List<MethodDescriptor> getConfigHandlers() {
        return configHandlers;
    }

    @Override
    public Optional<MethodDescriptor> findConfigHandler(Type parameterType) {
        return configHandlers.parallelStream()
                .filter(p -> p.hasParameterTypes(parameterType))
                .findFirst();
    }

    void setApplication(Application application) {
        this.application = application;
    }

    void setCollaboratorMethod(MethodDescriptor collaboratorMethod) {
        this.collaboratorMethod = collaboratorMethod;
    }

    void setCutField(Field cutField) {
        this.cutField = cutField;
    }

    void setConfigHandlers(List<MethodDescriptor> configHandlers) {
        this.configHandlers = configHandlers;
    }

    void setModules(List<Module> modules) {
        this.modules = modules;
    }

    void setScans(List<Scan> scans) {
        this.scans = scans;
    }

    void setContainers(List<RequiresContainer> containers) {
        this.containers = containers;
    }

    void setResources(List<RequiresResource> resources) {
        this.resources = resources;
    }

    void setFieldDescriptors(List<FieldDescriptor> fieldDescriptors) {
        this.fieldDescriptors = fieldDescriptors;
    }

    void setFieldCache(Map<DescriptorKey, FieldDescriptor> fieldCache) {
        this.fieldCache = fieldCache;
    }

    @Override
    public String toString() {
        return "TestDescriptor{"
                + "testClass=" + testClass
                + ", application=" + application
                + ", collaboratorMethod=" + collaboratorMethod
                + ", cutField=" + cutField
                + ", configHandlers=" + configHandlers
                + ", modules=" + modules
                + ", scans=" + scans
                + ", containers=" + containers
                + ", resources=" + resources
                + ", testFields=" + fieldDescriptors
                + ", fieldCache=" + fieldCache
                + '}';
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
}
