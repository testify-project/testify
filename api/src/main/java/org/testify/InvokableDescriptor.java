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

import java.util.Optional;

/**
 * A contract that defines an association between an instance of an object and a
 * method descriptor. This is useful for executing a method on a specific object
 * instance (i.e. a configuration method or a method that provides a
 * collaborator defined in another class).
 *
 * @author saden
 */
public interface InvokableDescriptor {

    /**
     * The instance object.
     *
     * @return an optional containing the instance, empty optional otherwise
     */
    Optional<Object> getInstance();

    /**
     * Get the method descriptor.
     *
     * @return the method descriptor.
     */
    MethodDescriptor getMethodDescriptor();

    /**
     * Invoke the method using the underlying instance and the given arguments.
     *
     * @param args method arguments
     *
     * @return optional with method return value, empty optional otherwise
     */
    Optional<Object> invoke(Object... args);

    /**
     * Invoke the method using the given instance and arguments.
     *
     * @param instance the instance the underlying method is invoked from
     * @param args method arguments
     *
     * @return optional with method return value, empty optional otherwise
     */
    Optional<Object> invokeMethod(Object instance, Object... args);

}
