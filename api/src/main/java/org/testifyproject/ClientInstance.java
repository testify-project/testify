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

import org.testifyproject.annotation.Application;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods for retrieving information about a client and communicating
 * with the application under test.
 *
 * @author saden
 * @param <C> the client
 */
public interface ClientInstance<C> extends PropertiesReader {

    /**
     * Get a unique fully qualified name associated with the virtual resource.
     *
     * @return the virtual resource's fully qualified name
     */
    String getFqn();

    /**
     * Get the application annotation associated with the client instance.
     *
     * @return the application annotation
     */
    Application getApplication();

    /**
     * Get client instance.
     *
     * @return the client instance
     */
    Instance<C> getClient();

    /**
     * Get the client provider instance.
     *
     * @param <P> the client provider
     * @return optional with the client provider, empty optional otherwise
     */
    <P> Optional<Instance<P>> getClientProvider();
}
