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

import java.util.LinkedHashMap;
import java.util.Map;

import org.testifyproject.ApplicationInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default implementation of {@link ApplicationInstance} contract.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultApplicationInstance implements ApplicationInstance {

    private final TestContext testContext;
    private final Application application;
    private final Map<String, Object> properties;

    DefaultApplicationInstance(TestContext testContext, Application application,
            Map<String, Object> properties) {
        this.application = application;
        this.testContext = testContext;
        this.properties = properties;
    }

    /**
     * Create a new application instance.
     *
     * @param testContext the test context
     * @param application the application annotation
     * @return an application instance
     */
    public static ApplicationInstance of(TestContext testContext, Application application) {
        return new DefaultApplicationInstance(testContext, application, new LinkedHashMap<>());
    }

    /**
     * Create a new application instance.
     *
     * @param testContext the test context
     * @param application the application annotation
     * @param properties the properties
     * @return an application instance
     */
    public static ApplicationInstance of(
            TestContext testContext,
            Application application,
            Map<String, Object> properties) {
        return new DefaultApplicationInstance(testContext, application, properties);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public TestContext getTestContext() {
        return testContext;
    }

}
