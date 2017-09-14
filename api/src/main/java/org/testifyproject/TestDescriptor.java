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
package org.testifyproject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.annotation.ConfigHandler;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.extension.annotation.Hint;
import org.testifyproject.trait.AnnotationTrait;
import org.testifyproject.trait.PropertiesReader;
import org.testifyproject.trait.PropertiesWriter;

/**
 * A contract that defines methods used to access or perform operations on a test class.
 *
 * @author saden
 */
public interface TestDescriptor extends PropertiesReader, PropertiesWriter, AnnotationTrait<Class> {

    /**
     * The name of the test class.
     *
     * @return test class name
     */
    String getTestClassName();

    /**
     * Get the test class.
     *
     * @return the test class
     */
    Class<?> getTestClass();

    /**
     * The class loader associated with the test class.
     *
     * @return the test class classloader
     */
    ClassLoader getTestClassLoader();

    /**
     * The collaborator provider annotation associated with the test class.
     *
     * @return an optional with collaborator provider, empty optional otherwise
     */
    Optional<CollaboratorProvider> getCollaboratorProvider();

    /**
     * Get a collection of collaborator provider methods associated with the test class.
     *
     * @return a collection of method descriptor, empty list otherwise
     */
    Collection<MethodDescriptor> getCollaboratorProviders();

    /**
     * The application annotation associated with the test class.
     *
     * @return an optional with application annotation, empty optional otherwise
     */
    Optional<Application> getApplication();

    /**
     * Get the system under test field associated with the test class.
     *
     * @return an optional with sut class field, empty optional otherwise
     */
    Optional<Field> getSutField();

    /**
     * The config handler annotation associated with the test class.
     *
     * @return an optional with config handler, empty optional otherwise
     */
    Optional<ConfigHandler> getConfigHandler();

    /**
     * Get a collection of method handlers for all the config handlers associated with the test
     * class.
     *
     * @return a collection with method descriptors, empty list otherwise
     */
    Collection<MethodDescriptor> getConfigHandlers();

    /**
     * Get a collection of field descriptors for all the fields associated with the test class.
     *
     * @return a collection with field descriptor, empty list otherwise
     */
    Collection<FieldDescriptor> getFieldDescriptors();

    /**
     * Get a collection of modules associated with the test class.
     *
     * @return a collection with modules, empty list otherwise
     */
    Collection<Module> getModules();

    /**
     * Get a collection of scans associated with the test class.
     *
     * @return a collection with scans, empty list otherwise
     */
    Collection<Scan> getScans();

    /**
     * Get a collection of local resources associated with the test class.
     *
     * @return a collection with local resources, empty list otherwise
     */
    Collection<LocalResource> getLocalResources();

    /**
     * Get a collection of virtual resources associated with the test class.
     *
     * @return a collection with virtual resources, empty list otherwise
     */
    Collection<VirtualResource> getVirtualResources();

    /**
     * Get a collection of remote resources associated with the test class.
     *
     * @return a collection with remote resources, empty list otherwise
     */
    Collection<RemoteResource> getRemoteResources();

    /**
     * Get a collection of all known and inspected annotations including those placed on
     * {@link org.testifyproject.annotation.Bundle} annotation.
     *
     * @return a collection of inspected annotations, empty list otherwise
     */
    Collection<Annotation> getInspectedAnnotations();

    /**
     * Get guideline annotations associated with the test.
     *
     * @return a collection of guidelines, empty array otherwise
     */
    Collection<Class<? extends Annotation>> getGuidelines();

    /**
     * Get hint annotation associated with the test.
     *
     * @return the hint annotation, empty optional otherwise
     */
    Optional<Hint> getHint();

    /**
     * Find the config handler associated with the test class capable of configuring the given
     * configurable type.
     *
     * @param configurableType the configurable type
     * @return an optional with method descriptor, empty optional otherwise
     */
    Optional<MethodDescriptor> findConfigHandler(Type configurableType);

    /**
     * Find the collaborator provider for the given return type.
     *
     * @param returnType the return type
     * @return an optional with method descriptor, empty optional otherwise
     */
    Optional<MethodDescriptor> findCollaboratorProvider(Type returnType);

    /**
     * Find the collaborator provider for the given name and return type.
     *
     * @param returnType the return type
     * @param name the name associated with the collaborator provider method
     * @return an optional with method descriptor, empty optional otherwise
     */
    Optional<MethodDescriptor> findCollaboratorProvider(Type returnType, String name);

    /**
     * Find the descriptor for a field with the given type and name on the test class.
     *
     * @param type the field type
     * @param name the field name
     * @return an optional with a field descriptor, empty optional otherwise
     */
    Optional<FieldDescriptor> findFieldDescriptor(Type type, String name);

    /**
     * Find the descriptor for a field with the given type on the test class.
     *
     * @param type the field type
     * @return an optional with a field descriptor, empty optional otherwise
     */
    Optional<FieldDescriptor> findFieldDescriptor(Type type);

}
