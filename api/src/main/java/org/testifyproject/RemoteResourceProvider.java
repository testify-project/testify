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

import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods to configure, start an stop a remote
 * resource.
 *
 * @author saden
 * @param <T> the configuration type
 * @param <C> the client type
 */
public interface RemoteResourceProvider<T, C> {

    /**
     * <p>
     * A method to configure a remote resource. Configuring a remote resource
     * typically involves creating a default configuration object that can be
     * further configured by a
     * {@link org.testifyproject.annotation.ConfigHandler} method. Please note:
     * </p>
     * <ul>
     * <li>Implementation of this method should not do any work beyond returning
     * configuration object. It should be stateless and should not perform
     * instantiation of the remote resource as that should be handled in
     * {@link #start(org.testifyproject.TestContext, org.testifyproject.annotation.RemoteResource, java.lang.Object) }
     * method.
     * </li>
     * <li>
     * The value of PropertiesReader by default encapsulates
     * {@code .testify.yml} configuration properties. A specific section in
     * {@code .testify.yml} can be specified through {@link RemoteResource#configKey()
     * }.
     * </li>
     * <li>
     * The configuration object returned by this method is simply default
     * configuration. It can be updated or replaced with entirely new
     * configuration object by the
     * {@link org.testifyproject.annotation.ConfigHandler} method before it is
     * passed to {@link #start(org.testifyproject.TestContext, org.testifyproject.annotation.RemoteResource, java.lang.Object)
     * } method
     * </li>
     * </ul>
     *
     * @param testContext the test context
     * @param remoteResource test class remote resource annotation
     * @param configReader a configuration properties reader
     * @return the remote resource configuration object
     */
    T configure(TestContext testContext, RemoteResource remoteResource, PropertiesReader configReader);

    /**
     * Start the remote resource with the given testContext and configuration.
     *
     * @param testContext the test context
     * @param remoteResource test class remote resource annotation
     * @param config the remote resource configuration
     * @return a remote resource instance
     * @throws java.lang.Exception an exception thrown while starting
     */
    RemoteResourceInstance<C> start(TestContext testContext, RemoteResource remoteResource, T config)
            throws Exception;

    /**
     * Stop the remote resource.
     *
     * @param testContext the test context
     * @param remoteResource test class remote resource annotation
     * @param instance the remote resource instance
     * @throws java.lang.Exception an exception thrown while stopping
     */
    void stop(TestContext testContext, RemoteResource remoteResource, RemoteResourceInstance<C> instance)
            throws Exception;

}
