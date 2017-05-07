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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.trait.PropertiesTrait;

/**
 * A contract that defines methods used to access or perform operations on a
 * test class.
 *
 * @author saden
 */
public interface TestDescriptor extends PropertiesTrait {

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
     * Get the system under test collaborator provider associated with the test
     * class.
     *
     * @return an optional with method descriptor, empty optional otherwise
     */
    Optional<MethodDescriptor> getCollaboratorProvider();

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
     * Get a list method handlers for all the config handlers associated with
     * the test class.
     *
     * @return a list with method descriptors, empty list otherwise
     */
    List<MethodDescriptor> getConfigHandlers();

    /**
     * Get a list of field descriptors for all the fields associated with the
     * test class.
     *
     * @return a list with field descriptor, empty list otherwise
     */
    Collection<FieldDescriptor> getFieldDescriptors();

    /**
     * Get a list of modules associated with the test class.
     *
     * @return a list with modules, empty list otherwise
     */
    List<Module> getModules();

    /**
     * Get a list of scans associated with the test class.
     *
     * @return a list with scans, empty list otherwise
     */
    List<Scan> getScans();

    /**
     * Get a list of virtual resources associated with the test class.
     *
     * @return a list with virtual resources, empty list otherwise
     */
    List<VirtualResource> getVirtualResources();

    /**
     * Get a list of local resources associated with the test class.
     *
     * @return a list with local resources, empty list otherwise
     */
    List<LocalResource> getLocalResources();

    /**
     * Find the config handler associated with the test class capable of
     * configuring the given configurable type.
     *
     * @param configurableType the configurable type
     * @return an optional with method descriptor, empty optional otherwise
     */
    Optional<MethodDescriptor> findConfigHandler(Type configurableType);

    /**
     * Find the descriptor for a field with the given type and name on the test
     * class.
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
