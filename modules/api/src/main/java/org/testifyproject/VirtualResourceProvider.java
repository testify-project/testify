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

import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods to configure, start an stop a virtual resource.
 *
 * @author saden
 * @param <T> the type virtual resource configuration object
 */
public interface VirtualResourceProvider<T> {

    /**
     * <p>
     * A method to configure a virtual resource. Configuring a virtual resource typically
     * involves creating a default configuration object that can be further configured by a
     * {@link org.testifyproject.annotation.ConfigHandler} method. Please note:
     * </p>
     * <ul>
     * <li>Implementation of this method should not do any work beyond returning configuration
     * object. It should be stateless and should not perform instantiation of the virtual
     * resource as that should be handled in
     * {@link #start(org.testifyproject.TestContext, org.testifyproject.annotation.VirtualResource, java.lang.Object)}
     * method.
     * </li>
     * <li>
     * The value of PropertiesReader by default encapsulates {@code .testify.yml} configuration
     * properties. A specific section in {@code .testify.yml} can be specified through
     * {@link VirtualResource#configKey()}.
     * </li>
     * <li>
     * The configuration object returned by this method is simply default configuration. It can
     * be updated or replaced with entirely new configuration object by the
     * {@link org.testifyproject.annotation.ConfigHandler} method before it is passed to
     * {@link #start(org.testifyproject.TestContext, org.testifyproject.annotation.VirtualResource, java.lang.Object)}
     * method
     * </li>
     * </ul>
     *
     * @param testContext the test context
     * @param virtualResource test class virtual resource annotation
     * @param configReader the value of configReader
     * @return the virtual resource configuration object
     */
    T configure(TestContext testContext, VirtualResource virtualResource,
            PropertiesReader configReader);

    /**
     * Start the virtual resource with the given testContext and configuration.
     *
     * @param testContext the test context
     * @param virtualResource test class virtual resource annotation
     * @param configuration the virtual resource configuration object
     * @return a virtual resource instance
     * @throws java.lang.Exception an exception thrown while starting
     */
    VirtualResourceInstance start(TestContext testContext, VirtualResource virtualResource,
            T configuration)
            throws Exception;

    /**
     * Load the given list of data file into the local resource prior to the resource being
     * used. Note that by default this method does not have to be implemented.
     *
     * @param testContext the test context
     * @param virtualResource test class remote resource annotation
     * @param instance the virtual resource instance
     * @param dataFiles a data files that should be loaded
     * @throws java.lang.Exception an exception thrown while loading data
     */
    default void load(TestContext testContext,
            VirtualResource virtualResource,
            VirtualResourceInstance instance,
            Set<String> dataFiles)
            throws Exception {
    }

    /**
     * Stop the virtual resource.
     *
     * @param testContext the test context
     * @param virtualResource test class virtual resource annotation
     * @param instance the value of instance
     * @throws java.lang.Exception an exception thrown while stopping
     */
    void stop(TestContext testContext, VirtualResource virtualResource,
            VirtualResourceInstance instance)
            throws Exception;

}
