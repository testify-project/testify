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

import java.util.Optional;
import static java.util.Optional.empty;

/**
 * A contract that defines an instance object. An instance represents the
 * properties of an object in the dependency injection framework in use and how
 * the object is made available for injection into test classes and fixtures.
 *
 * @author saden
 * @param <T> the instance object type
 */
public interface Instance<T> {

    /**
     * Get the instance value.
     *
     * @return the instance value
     */
    T getValue();

    /**
     * Get the name of instance. If present it represents the service name
     * associated with the instance and may be used to qualify the service.
     *
     * @return optional with instance name, empty optional otherwise
     */
    default Optional<String> getName() {
        return empty();
    }

    /**
     * The contract implemented by the instance. If present any existing
     * implementations of the contract in the dependency injection framework
     * will be replaced by this instance.
     *
     * @return optional with instance contract type, empty optional otherwise
     */
    default Optional<Class<? extends T>> getContract() {
        return empty();
    }

}
