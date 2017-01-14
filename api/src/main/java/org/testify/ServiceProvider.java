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

/**
 * An SPI contract to create and configure a service instance. Please note that
 * for integration tests all three methods will be called in the order they are
 * defined (create, configure, initialize).
 *
 * @author saden
 * @param <T> the underlying context object used by the service instance.
 */
public interface ServiceProvider<T> {

    /**
     * Create a service instance using the given test context.
     *
     * @param testContext the test context
     * @return a service instance context
     */
    T create(TestContext testContext);

    /**
     * Configure a service instance using the given test context and service
     * context.
     *
     * @param testContext the test context
     * @param serviceContext the service instance context
     * @return a service instance
     */
    ServiceInstance configure(TestContext testContext, T serviceContext);

    /**
     * Initialize a service instance using the given test context and service
     * context.
     *
     * @param testContext the test context
     * @param serviceInstance the service instance
     */
    void postConfigure(TestContext testContext, ServiceInstance serviceInstance);

}
