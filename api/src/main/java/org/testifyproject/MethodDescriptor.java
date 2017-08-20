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
import org.testifyproject.trait.MethodTrait;

/**
 * A contract that defines methods to access properties of or perform operations
 * on a method.
 *
 * @author saden
 */
public interface MethodDescriptor extends MethodTrait {

    /**
     * The instance object associated with the method descriptor.
     *
     * @return an optional containing the instance, empty optional otherwise
     */
    Optional<Object> getInstance();

    /**
     * <p>
     * Get the explicitly defined method name. Please note that method names are
     * explicitly defined by annotating the method with
     * {@link org.testifyproject.annotation.Name}.
     * </p>
     * <p>
     * In the event a method name is not explicitly defined the value returned
     * by calling {@link java.lang.reflect.Method#getName()} will be returned.
     * </p>
     *
     * @return the method name
     */
    String getDefinedName();

}
