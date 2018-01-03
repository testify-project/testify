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

import org.testifyproject.annotation.Application;

/**
 * A contract that defines methods for configuring, initializing and destroying a client used to
 * communicate with the server.
 *
 * @author saden
 * @param <T> the client configuration type
 * @param <C> the client type
 * @param <P> the client supplier type, should be if not applicable {@link Void}
 */
public interface ClientProvider<T, C, P> {

    /**
     * Configure the client using the given server instance.
     *
     * @param testContext the test context
     * @param application the application annotation
     * @param baseURI the base server URI
     * @return client configuration object
     */
    T configure(TestContext testContext, Application application, URI baseURI);

    /**
     * Create and initialize the client instance using the given base URI and configuration
     * object.
     *
     * @param testContext the test context
     * @param application the application annotation
     * @param baseURI the base server URI
     * @param configuration client configuration object
     * @return a client instance.
     */
    ClientInstance<C, P> create(TestContext testContext,
            Application application,
            URI baseURI,
            T configuration);

    /**
     * This method will dispose of client instance that was created.
     *
     * @param clientInstance the client instance to dispose of
     */
    void destroy(ClientInstance<C, P> clientInstance);

    /**
     * The type of the client.
     *
     * @return the client type.
     */
    Class<C> getClientType();

    /**
     * The type of client supplier. If a client supplier is not applicable this method returns
     * {@code null};
     *
     * @return the client supplier type.
     */
    default Class<P> getClientSupplierType() {
        return null;
    }
}
