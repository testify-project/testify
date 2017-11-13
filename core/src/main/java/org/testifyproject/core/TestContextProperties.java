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
package org.testifyproject.core;

/**
 * A class that defines a list of common {@link org.testifyproject.TestContext test context}
 * properties.
 *
 * @author saden
 */
public class TestContextProperties {

    /**
     * The underlying application instance property key.
     */
    public static final String APP = "app";
    /**
     * The application name property key.
     */
    public static final String APP_NAME = "appName";
    /**
     * The application fully qualified name property key.
     */
    public static final String APP_FQN = "appFQN";
    /**
     * The application arguments property key.
     */
    public static final String APP_ARGUMENTS = "appArguments";
    /**
     * The application port property key.
     */
    public static final String APP_PORT = "appPort";
    /**
     * The application server property key.
     */
    public static final String SERVER = "appServer";
    /**
     * The application base URI property key.
     */
    public static final String SERVER_BASE_URI = "baseURI";
    /**
     * The application context path property key.
     */
    public static final String SERVER_CONTEXT_PATH = "appContextPath";
    /**
     * The application client instance property key.
     */
    public static final String CLIENT_INSTANCE = "appClientInstance";
    /**
     * The application client property key.
     */
    public static final String CLIENT = "appClient";
    /**
     * The application client supplier property key.
     */
    public static final String CLIENT_SUPPLIER = "appClientSupplier";
    /**
     * The application client provider property key.
     */
    public static final String CLIENT_PROVIDER = "appClientProvider";
    /**
     * The application server instance property key.
     */
    public static final String SERVER_INSTANCE = "appServerInstance";
    /**
     * The application server provider property key.
     */
    public static final String SERVER_PROVIDER = "appServerProvider";
    /**
     * The application service instance property key.
     */
    public static final String SERVICE_INSTANCE = "serviceInstance";

    /**
     * The application system under test instance property key.
     */
    public static final String SUT_INSTANCE = "sutInstance";
    /**
     * The application system under test descriptor property key.
     */
    public static final String SUT_DESCRIPTOR = "sutDescriptor";
    /**
     * The local resource instances property key.
     */
    public static final String LOCAL_RESOURCE_INSTANCES = "localResourceInstances";
    /**
     * The virtual resource instances property key.
     */
    public static final String VIRTUAL_RESOURCE_INSTANCES = "virtualResourceInstances";
    /**
     * The remote resource instances property key.
     */
    public static final String REMOTE_RESOURCE_INSTANCES = "remoteResourceInstances";
    /**
     * The resource providers property key.
     */
    public static final String RESOURCE_PROVIDERS = "resourceProviders";
    /**
     * Application instance property key.
     */
    public static final String APPLICATION_INSTANCE = "applicationInstance";

    private TestContextProperties() {
    }

}
