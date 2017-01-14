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
 * An SPI contract for reifying a test class.
 *
 * @author saden
 */
public interface ReificationProvider {

    /**
     * Start reifying the test class using the given test context and service
     * instance.
     *
     * @param testContext the test context
     * @param serviceInstance the service instance
     */
    void start(TestContext testContext, ServiceInstance serviceInstance);

    /**
     * Clean up and destroy the test class.
     *
     * @param testContext the test context
     * @param serviceInstance the service instance
     */
    void destroy(TestContext testContext, ServiceInstance serviceInstance);

}
