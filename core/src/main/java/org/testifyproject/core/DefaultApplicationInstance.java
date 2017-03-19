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
import java.util.Objects;
import org.testifyproject.ApplicationInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;

/**
 * Default implementation of {@link ApplicationInstance} contract.
 *
 * @author saden
 */
public class DefaultApplicationInstance<T> implements ApplicationInstance<T> {

    private final TestContext testContext;
    private final Application application;
    private final Map<String, Object> properties;

    /**
     * Create a new application instance.
     *
     * @param <T> application initializer type
     * @param testContext the test context
     * @param application the application annotation
     * @return an application instance
     */
    public static <T> ApplicationInstance of(TestContext testContext, Application application) {
        return new DefaultApplicationInstance(testContext, application, new LinkedHashMap<>());
    }

    /**
     * Create a new application instance.
     *
     * @param <T> application initializer type
     * @param testContext the test context
     * @param application the application annotation
     * @param properties the properties
     * @return an application instance
     */
    public static <T> ApplicationInstance of(
            TestContext testContext,
            Application application,
            Map<String, Object> properties) {
        return new DefaultApplicationInstance(testContext, application, properties);
    }

    DefaultApplicationInstance(
            TestContext testContext,
            Application application,
            Map<String, Object> properties) {
        this.application = application;
        this.testContext = testContext;
        this.properties = properties;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.application);
        hash = 23 * hash + Objects.hashCode(this.testContext);
        hash = 23 * hash + Objects.hashCode(this.properties);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultApplicationInstance other = (DefaultApplicationInstance) obj;
        if (!Objects.equals(this.application, other.application)) {
            return false;
        }
        if (!Objects.equals(this.testContext, other.testContext)) {
            return false;
        }
        return Objects.equals(this.properties, other.properties);
    }

    @Override
    public String toString() {
        return "DefaultApplicationInstance{"
                + "testContext=" + testContext
                + ", application=" + application
                + ", properties=" + properties
                + '}';
    }

}
