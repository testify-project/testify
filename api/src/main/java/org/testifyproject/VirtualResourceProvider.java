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

import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods to configure, start an stop a virtual
 * resource.
 *
 * @author saden
 * @param <T> the type virtual resource configuration object
 */
public interface VirtualResourceProvider<T> {

    /**
     * <p>
     * A method to configure a virtual resource. Configuring a virtual resource
     * typically involves creating a configuration object so it can be further
     * configured by a test class method annotated with
     * {@link org.testifyproject.annotation.ConfigHandler}.
     * </p>
     * <p>
     * Note that implementation of this method should not do any work beyond
     * returning configuration object. That is to is to say it should be
     * stateless and should not perform instantiation of the virtual resource as
     * that should be handled in {@link #start
     * (org.testifyproject.TestContext, java.lang.Object, java.lang.Object)}
     * method.
     * </p>
     *
     * @param testContext the test context
     * @param virtualResource test class virtual resource annotation
     * @param configReader the value of configReader
     * @return the virtual resource configuration object
     */
    T configure(TestContext testContext, VirtualResource virtualResource, PropertiesReader configReader);

    /**
     * Start the virtual resource with the given testContext and configuration.
     *
     * @param testContext the test context
     * @param virtualResource test class virtual resource annotation
     * @param configuration the virtual resource configuration object
     * @return a virtual resource instance
     * @throws java.lang.Exception an exception thrown while starting
     */
    VirtualResourceInstance start(TestContext testContext, VirtualResource virtualResource, T configuration)
            throws Exception;

    /**
     * Stop the virtual resource.
     *
     * @param testContext the test context
     * @param virtualResource test class virtual resource annotation
     * @param instance the value of instance
     * @throws java.lang.Exception an exception thrown while stopping
     */
    void stop(TestContext testContext, VirtualResource virtualResource, VirtualResourceInstance instance)
            throws Exception;

}
