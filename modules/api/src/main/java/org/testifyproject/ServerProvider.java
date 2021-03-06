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

import org.testifyproject.annotation.Application;

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
     * @param testContext the test context
     * @param application the application annotation
     * @param configuration server configuration object
     * @return a server instance
     * @throws java.lang.Exception an exception thrown in the event server start fails
     */
    ServerInstance<S> start(TestContext testContext, Application application, T configuration)
            throws Exception;

    /**
     * Stop the server.
     *
     * @param serverInstance the server instance
     * @throws java.lang.Exception an exceptions thrown in the event server stop fails
     */
    void stop(ServerInstance<S> serverInstance) throws Exception;

    /**
     * Get the server type. Note that due to Java type erasure we unfortunately require
     * implementors specify the server type.
     *
     * @return the server type
     */
    Class<S> getServerType();
}
