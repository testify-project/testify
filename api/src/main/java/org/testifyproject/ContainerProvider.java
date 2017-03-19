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
 * container.
 *
 * @author saden
 * @param <S> the type of the required container instance
 * @param <T> the type required container configuration object
 */
public interface ContainerProvider<S, T> {

    /**
     * <p>
     * A method to configure a required container. Configuring a required
     * container typically involves creating a configuration object so it can be
     * further configured by a test class method annotated with
     * {@link org.testifyproject.annotation.ConfigHandler}.
     * </p>
     * <p>
     * Note that implementation of this method should not do any work beyond
     * returning configuration object. That is to is to say it should be
     * stateless and should not perform instantiation of the required container
     * as that should be handled in {@link #start
     * (org.testifyproject.TestContext, java.lang.Object, java.lang.Object)}
     * method.
     * </p>
     *
     * @param testContext the test context
     * @return the required container configuration object
     */
    T configure(TestContext testContext);

    /**
     * Start the required container with the given testContext and
     * configuration.
     *
     * @param testContext the test context
     * @param requiredContainer test class required container annotation
     * @param configuration the required container configuration object
     * @return a required container instance
     */
    ContainerInstance start(TestContext testContext, S requiredContainer, T configuration);

    /**
     * Stop the required container.
     */
    void stop();

}
