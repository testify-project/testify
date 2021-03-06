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
 * An SPI contract to create and configure a service instance.
 *
 * @author saden
 * @param <T> the underlying context object used by the service instance.
 */
public interface ServiceProvider<T> {

    /**
     * Create and configure a dependency injection object using the given test context (i.e
     * Guice Injector, HK2 ServiceLocator, etc).
     *
     * @param testContext the test context
     * @return a the underlying dependency injection context object
     */
    T create(TestContext testContext);

    /**
     * Configure a service instance using the given test context and service context.
     *
     * @param testContext the test context
     * @param serviceContext the service instance context
     * @return a service instance
     */
    ServiceInstance configure(TestContext testContext, T serviceContext);

    /**
     * Initialize a service instance using the given test context and service context.
     *
     * @param testContext the test context
     * @param serviceInstance the service instance
     */
    default void postConfigure(TestContext testContext, ServiceInstance serviceInstance) {

    }

}
