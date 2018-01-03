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
package org.testifyproject.extension;

import java.util.function.Supplier;

/**
 * A contract that defines a proxy instance instance object. A proxy instance represents a type
 * that will be proxied and whose method calls will be delegated to the object returned by the
 * supplier.
 *
 * @author saden
 * @param <T> the proxy type
 */
public interface ProxyInstance<T> {

    /**
     * The type that will be proxied.
     *
     * @return the proxy type
     */
    Class<T> getType();

    /**
     * Get the name associated with the proxied instance.
     *
     * @return the proxy name
     */
    String getName();

    /**
     * Get the supplier that provides the object calls will be delegated to.
     *
     * @return a supplier that returns the delegate object
     */
    Supplier<T> getDelegate();

}
