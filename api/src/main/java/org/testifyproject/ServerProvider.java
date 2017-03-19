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
 * A contract that defines methods to configure, start and stop a server.
 *
 * @author saden
 * @param <T> the server configuration type
 * @param <S> the server instance type
 */
public interface ServerProvider<T, S> {

    /**
     * Configure the server using the given test context.
     *
     * @param testContext the test context
     * @return the server configuration object
     */
    T configure(TestContext testContext);

    /**
     * Start the server using the given configuration object.
     *
     * @param configuration server configuration object
     * @return a server instance
     */
    ServerInstance<S> start(T configuration);

    /**
     * Stop the server.
     */
    void stop();

}
