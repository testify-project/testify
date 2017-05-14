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
 * A class that defines a list of common
 * {@link org.testifyproject.TestContext test context} properties.
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
    public static final String APP_NAME = "app.name";
    /**
     * The application arguments property key.
     */
    public static final String APP_ARGUMENTS = "app.arguments";
    /**
     * The application port property key.
     */
    public static final String APP_PORT = "app.port";
    /**
     * The application context path property key.
     */
    public static final String APP_CONTEXT_PATH = "app.contextPath";
    /**
     * The application servlet container property key.
     */
    public static final String APP_SERVLET_CONTAINER = "app.servlet.container";
    /**
     * The application servlet context property key.
     */
    public static final String APP_SERVLET_CONTEXT = "app.servlet.context";
    /**
     * The application service instance property key.
     */
    public static final String SERVICE_INSTANCE = "service.instance";
    /**
     * The application base URI property key.
     */
    public static final String BASE_URI = "base.uri";
    /**
     * The application system under test instance property key.
     */
    public static final String SUT_INSTANCE = "sut.instance";
    /**
     * The application system under test descriptor property key.
     */
    public static final String SUT_DESCRIPTOR = "sut.descriptor";

    private TestContextProperties() {
    }

}
