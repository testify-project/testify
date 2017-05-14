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

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import org.testifyproject.annotation.Sut;
import org.testifyproject.trait.FieldTrait;
import org.testifyproject.trait.MockTrait;
import org.testifyproject.trait.PropertiesReadTrait;
import org.testifyproject.trait.PropertiesWriteTrait;

/**
 * A contract that defines methods to access properties of or perform operations
 * on a system under test (SUT).
 *
 * @author saden
 */
public interface SutDescriptor extends FieldTrait, MockTrait, PropertiesReadTrait, PropertiesWriteTrait {

    /**
     * Get {@link Sut} annotation.
     *
     * @return sut annotation
     */
    default Sut getSut() {
        return getMember().getDeclaredAnnotation(Sut.class);
    }

    /**
     * Get the constructor of the system under test class.
     *
     * @return system under test constructor.
     */
    Constructor getConstructor();

    /**
     * Get a list of field descriptors for all the fields associated with the
     * system under test .
     *
     * @return a list with field descriptor, empty list otherwise
     */
    Collection<FieldDescriptor> getFieldDescriptors();

    /**
     * Get a list of parameter descriptors for all the parameters associated
     * with the system under test's constructor.
     *
     * @return a list with parameter descriptor, empty list otherwise
     */
    Collection<ParameterDescriptor> getParameterDescriptors();

    /**
     * Find the descriptor for a field with the given type and name on the
     * system under test.
     *
     * @param type the field type
     * @param name the field name
     * @return an optional with a field descriptor, empty optional otherwise
     */
    Optional<FieldDescriptor> findFieldDescriptor(Type type, String name);

    /**
     * Find the descriptor for a field with the given type on the system under
     * test.
     *
     * @param type the field type
     * @return an optional with a field descriptor, empty optional otherwise
     */
    Optional<FieldDescriptor> findFieldDescriptor(Type type);

    /**
     * Find the descriptor for a constructor parameter with the given type and
     * name on the system under test.
     *
     * @param type the parameter type
     * @param name the parameter name
     * @return an optional with a parameter descriptor, empty optional otherwise
     */
    Optional<ParameterDescriptor> findParameterDescriptor(Type type, String name);

    /**
     * Find the descriptor for a constructor parameter with the given type on
     * the system under test.
     *
     * @param type the parameter type
     * @return an optional with a parameter descriptor, empty optional otherwise
     */
    Optional<ParameterDescriptor> findParameterDescriptor(Type type);

    /**
     * Determine if the given type is the same type or super type of the system
     * under test.
     *
     * @param type the type
     * @return true if type is the same or super type, false otherwise
     */
    Boolean isSutClass(Type type);

    /**
     * Determine if the the {@link Sut} annotation is a delegated mock.
     *
     * @return true if the member is a delegated sut
     */
    default Boolean isVirtualSut() {
        return getSut().value();
    }

}
