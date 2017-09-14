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

import java.util.Set;

import org.testifyproject.annotation.LocalResource;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods to configure, start an stop a local resource.
 *
 * @author saden
 * @param <T> the configuration type
 * @param <R> the resource type
 * @param <C> the client type
 */
public interface LocalResourceProvider<T, R, C> {

    /**
     * <p>
     * A method to configure a local resource. Configuring a local resource typically involves
     * creating a default configuration object that can be further configured by a
     * {@link org.testifyproject.annotation.ConfigHandler} method.
     * </p>
     * <p>
     * Note:
     * </p>
     * <ul>
     * <li>Implementation of this method should not do any work beyond returning configuration
     * object. It should be stateless and should not perform instantiation of the local resource as
     * that should be handled in
     * {@link #start(org.testifyproject.TestContext, org.testifyproject.annotation.LocalResource, java.lang.Object)}
     * method.
     * </li>
     * <li>
     * The value of PropertiesReader by default encapsulates {@code .testify.yml} configuration
     * properties. A specific section in {@code .testify.yml} can be specified through {@link LocalResource#configKey()
     * }.
     * </li>
     * <li>
     * The configuration object returned by this method is simply default configuration. It can be
     * updated or replaced with entirely new configuration object by the
     * {@link org.testifyproject.annotation.ConfigHandler} method before it is passed to
     * {@link #start(org.testifyproject.TestContext, org.testifyproject.annotation.LocalResource, java.lang.Object)}
     * method
     * </li>
     * </ul>
     *
     * @param testContext the test context
     * @param localResource test class local resource annotation
     * @param configReader the value of configReader
     * @return the T
     */
    T configure(TestContext testContext, LocalResource localResource,
            PropertiesReader configReader);

    /**
     * Start the local resource with the given testContext and configuration.
     *
     * @param testContext the test context
     * @param localResource test class local resource annotation
     * @param config the local resource configuration
     * @return a local resource instance
     * @throws java.lang.Exception an exception thrown while starting
     */
    LocalResourceInstance<R, C> start(TestContext testContext, LocalResource localResource,
            T config)
            throws Exception;

    /**
     * Load the given list of data file into the local resource prior to the resource being used.
     * Note that by default this method does not have to be implemented.
     *
     * @param testContext the test context
     * @param localResource test class local resource annotation
     * @param instance the local resource instance
     * @param dataFiles a data files that should be loaded
     * @throws java.lang.Exception an exception thrown while loading data
     */
    default void load(TestContext testContext,
            LocalResource localResource,
            LocalResourceInstance<R, C> instance,
            Set<String> dataFiles)
            throws Exception {
    }

    /**
     * Stop the local resource.
     *
     * @param testContext the test context
     * @param localResource test class local resource annotation
     * @param instance the local resource instance
     * @throws java.lang.Exception an exception thrown while stopping
     */
    void stop(TestContext testContext, LocalResource localResource,
            LocalResourceInstance<R, C> instance)
            throws Exception;

}
