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

import java.net.URI;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.testifyproject.annotation.Application;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods for retrieving information about the running server.
 *
 * @author saden
 * @param <T> the underlying server instance type.
 */
public interface ServerInstance<T> extends PropertiesReader {

    /**
     * Get a unique fully qualified name associated with the server instance.
     *
     * @return the server instance's fully qualified name
     */
    String getFqn();

    /**
     * Get the application annotation associated with the server instance.
     *
     * @return the application annotation
     */
    Application getApplication();

    /**
     * The server base URI.
     *
     * @return the base URI
     */
    URI getBaseURI();

    /**
     * Get the underlying server instance.
     *
     * @return the underlying server instance
     */
    Instance<T> getServer();

    /**
     * Execute the given function with the {@link #getServer()} value and {@link #getBaseURI()}
     * as its parameters.
     *
     * @param <R> function return type
     * @param function the function
     * @return the result of executing the function
     */
    default <R> R execute(BiFunction<T, URI, R> function) {
        return function.apply(getServer().getValue(), getBaseURI());
    }

    /**
     * Execute the given consumer function with the {@link #getServer()} value and
     * {@link #getBaseURI()} as its parameters.
     *
     * @param consumer the consumer function
     */
    default void execute(BiConsumer<T, URI> consumer) {
        consumer.accept(getServer().getValue(), getBaseURI());
    }

}
