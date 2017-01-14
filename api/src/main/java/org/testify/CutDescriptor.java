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
package org.testify;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import org.testify.trait.FieldTrait;
import org.testify.trait.MockTrait;

/**
 * A contract that defines methods to access properties of or perform operations
 * on a class under test (CUT).
 *
 * @author saden
 */
public interface CutDescriptor extends FieldTrait, MockTrait {

    /**
     * Get the constructor of the class under test class.
     *
     * @return class under test constructor.
     */
    Constructor<?> getConstructor();

    /**
     * Get a list of field descriptors for all the fields associated with the
     * class under test .
     *
     * @return a list with field descriptor, empty list otherwise
     */
    List<FieldDescriptor> getFieldDescriptors();

    /**
     * Get a list of parameter descriptors for all the parameters associated
     * with the class under test's constructor.
     *
     * @return a list with parameter descriptor, empty list otherwise
     */
    List<ParameterDescriptor> getParameterDescriptors();

    /**
     * Find the descriptor for a field with the given type and name on the class
     * under test.
     *
     * @param type the field type
     * @param name the field name
     * @return an optional with a field descriptor, empty optional otherwise
     */
    Optional<FieldDescriptor> findFieldDescriptor(Type type, String name);

    /**
     * Find the descriptor for a field with the given type on the class under
     * test.
     *
     * @param type the field type
     * @return an optional with a field descriptor, empty optional otherwise
     */
    Optional<FieldDescriptor> findFieldDescriptor(Type type);

    /**
     * Find the descriptor for a constructor parameter with the given type and
     * name on the class under test.
     *
     * @param type the parameter type
     * @param name the parameter name
     * @return an optional with a parameter descriptor, empty optional otherwise
     */
    Optional<ParameterDescriptor> findParameterDescriptor(Type type, String name);

    /**
     * Find the descriptor for a constructor parameter with the given type on
     * the class under test.
     *
     * @param type the parameter type
     * @return an optional with a parameter descriptor, empty optional otherwise
     */
    Optional<ParameterDescriptor> findParameterDescriptor(Type type);

    /**
     * Determine if the given type is the same type or super type of the class
     * under test.
     *
     * @param type the type
     * @return true if type is the same or super type, false otherwise
     */
    Boolean isCutClass(Type type);

    /**
     * Create a new instance of the class under test using the given constructor
     * arguments.
     *
     * @param args the constructor arguments
     * @return a new instance of the class under test
     */
    Object newInstance(Object... args);

}
