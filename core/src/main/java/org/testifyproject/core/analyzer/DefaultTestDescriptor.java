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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.annotation.ConfigHandler;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Name;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.extension.annotation.Hint;

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
        return new DefaultTestDescriptor(testClass, new LinkedHashMap<>());
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
    public Class getAnnotatedElement() {
        return getTestClass();
    }

    @Override
    public Optional<Application> getApplication() {
        return findProperty(TestDescriptorProperties.APPLICATION);
    }

    @Override
    public Optional<Field> getSutField() {
        return findProperty(TestDescriptorProperties.SUT_FIELD);
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
    public List<RemoteResource> getRemoteResources() {
        return findList(TestDescriptorProperties.REMOTE_RESOURCES);
    }

    @Override
    public List<Annotation> getInspectedAnnotations() {
        return findList(TestDescriptorProperties.INSPECTED_ANNOTATIONS);
    }

    @Override
    public List<Class<? extends Annotation>> getGuidelines() {
        return findList(TestDescriptorProperties.GUIDELINE_ANNOTATIONS);
    }

    @Override
    public Optional<Hint> getHint() {
        return findProperty(TestDescriptorProperties.HINT_ANNOTATION);
    }

    @Override
    public Optional<CollaboratorProvider> getCollaboratorProvider() {
        return findProperty(TestDescriptorProperties.COLLABORATOR_PROVIDER);
    }

    @Override
    public List<MethodDescriptor> getCollaboratorProviders() {
        return findList(TestDescriptorProperties.COLLABORATOR_PROVIDERS);
    }

    @Override
    public Optional<ConfigHandler> getConfigHandler() {
        return findProperty(TestDescriptorProperties.CONFIG_HANDLER);
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
        Map<DescriptorKey, FieldDescriptor> fieldDescriptors
                = findMap(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type);
        FieldDescriptor foundFieldDescriptor = fieldDescriptors.get(descriptorKey);

        if (foundFieldDescriptor == null) {
            //TODO: Not sure if we should be this lose in the event we don't
            //find a matching field descriptor. Need to evaluate if this code
            //is useful or harmful.
            foundFieldDescriptor = fieldDescriptors.values().parallelStream()
                    .filter(fieldDescriptor -> fieldDescriptor.isSupertypeOf(type))
                    .findFirst()
                    .orElse(null);
        }

        return ofNullable(foundFieldDescriptor);
    }

    @Override
    public Optional<FieldDescriptor> findFieldDescriptor(Type type, String name) {
        Map<DescriptorKey, FieldDescriptor> fieldDescriptors
                = findMap(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE);

        DescriptorKey descriptorKey = DescriptorKey.of(type, name);
        FieldDescriptor foundFieldDescriptor = fieldDescriptors.get(descriptorKey);

        return ofNullable(foundFieldDescriptor);
    }

    @Override
    public Optional<MethodDescriptor> findConfigHandler(Type parameterType) {
        return getConfigHandlers()
                .parallelStream()
                .filter(methodDescriptor -> methodDescriptor.hasParameterTypes(parameterType))
                .findFirst();
    }

    @Override
    public Optional<MethodDescriptor> findCollaboratorProvider(Type returnType) {
        return getCollaboratorProviders()
                .parallelStream()
                .filter(methodDescriptor -> !methodDescriptor.hasAnyAnnotations(Name.class))
                .filter(methodDescriptor -> methodDescriptor.hasReturnType(returnType))
                .findFirst();
    }

    @Override
    public Optional<MethodDescriptor> findCollaboratorProvider(Type returnType, String name) {
        Deque<MethodDescriptor> methodDescriptors = new LinkedList<>();

        getCollaboratorProviders().forEach(methodDescriptor -> {
            Optional<Name> nameAnnotation = methodDescriptor.getAnnotation(Name.class);

            if (nameAnnotation.isPresent()
                    && nameAnnotation.get().value().equals(name)
                    && methodDescriptor.hasReturnType(returnType)) {
                methodDescriptors.addFirst(methodDescriptor);
            } else if (methodDescriptor.hasReturnType(returnType)
                    && methodDescriptor.getName().equals(name)) {
                methodDescriptors.addLast(methodDescriptor);
            }
        });

        if (methodDescriptors.size() > 1) {
            LoggingUtil.INSTANCE.warn("Multiple canidate methods found with return type '{}'"
                    + " and name '{}':\n{}",
                    returnType.getTypeName(),
                    name, methodDescriptors.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")));
        }

        return methodDescriptors.stream().findFirst();
    }

}
