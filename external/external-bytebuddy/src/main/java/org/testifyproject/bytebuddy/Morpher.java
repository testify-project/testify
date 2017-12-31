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
package org.testifyproject.bytebuddy;

/**
 * A contract that enables the substitution of parameters passed to super method using
 * {@code net.bytebuddy.implementation.bind.annotation.Morph} annotation.
 *
 * @author saden
 * @param <T> the super call return type
 */
public interface Morpher<T> {

    /**
     * Invoke the morph method using the given parameters.
     *
     * @param arguments the arguments passed to the super call
     * @return the result returned from the super call.
     */
    T morph(Object... arguments);
}
