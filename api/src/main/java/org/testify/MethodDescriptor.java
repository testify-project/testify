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
import org.testify.trait.MethodTrait;

/**
 * A contract that defines methods to access properties of or perform operations
 * on a method.
 *
 * @author saden
 */
public interface MethodDescriptor extends MethodTrait {

    /**
     * The instance object.
     *
     * @return an optional containing the instance, empty optional otherwise
     */
    Optional<Object> getInstance();

    /**
     * Invoke the method using the underlying instance and the given arguments.
     *
     * @param args method arguments
     *
     * @return optional with method return value, empty optional otherwise
     */
    default Optional<Object> invokeMethod(Object... args) {
        return invoke(getInstance().get(), args);
    }

}
