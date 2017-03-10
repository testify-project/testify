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
 * A contract that defines methods to configure, start an stop a required
 * resource for testing.
 *
 * @author saden
 * @param <T> the configuration type
 * @param <S> the server type
 * @param <C> the client type
 */
public interface ResourceProvider<T, S, C> {

    /**
     * <p>
     * A method to configure a required resource for testing. Configuring a
     * required resource for testing typically involves creating a configuration
     * object that can be further configured by a test class method annotated
     * with {@link org.testifyproject.annotation.ConfigHandler}.
     * </p>
     * <p>
     * Note that implementation of this method should not do any work beyond
     * returning configuration object. That is to is to say it should be
     * stateless and should not perform instantiation of the required resource
     * for testing as that should be handled in
     * {@link #start(TestContext, java.lang.Object)} method.
     * </p>
     *
     * @param testContext the test context
     * @return the required resource for testing configuration object
     */
    T configure(TestContext testContext);

    /**
     * Start the required resource for testing with the given testContext and
     * configuration.
     *
     * @param testContext the test context
     * @param config the required resource for testing configuration
     * @return a required resource for testing instance
     */
    ResourceInstance<S, C> start(TestContext testContext, T config);

    /**
     * Stop the required resource.
     */
    void stop();

}
