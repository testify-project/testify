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

/**
 * A contract that defines methods for configuring, initializing and destroying
 * a client used to communicate with the server.
 *
 * @author saden
 * @param <T> the client configuration type
 * @param <S> the client type
 */
public interface ClientProvider<S, T> {

    /**
     * Configure the client using the given server instance.
     *
     * @param testContext the test context
     * @param serverInstance the server instance
     * @return client configuration object.
     */
    S configure(TestContext testContext, ServerInstance serverInstance);

    /**
     * Create and initialize the client instance using the given configuration
     * object.
     *
     * @param testContext the test context
     * @param configuration client configuration object
     * @return a client instance.
     */
    ClientInstance<T> create(TestContext testContext, S configuration);

    /**
     * Destroy the client.
     */
    void destroy();

}
