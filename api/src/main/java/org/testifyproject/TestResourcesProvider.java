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
 * An SPI contract for starting and stopping test resources.
 *
 * @author saden
 */
public interface TestResourcesProvider {

    /**
     * Start the test resources.
     *
     * @param testContext the test context
     * @param serviceInstance the service instance
     */
    void start(TestContext testContext, ServiceInstance serviceInstance);

    /**
     * Stop the test resources.
     *
     * @param testContext the test context
     */
    void stop(TestContext testContext);

}
