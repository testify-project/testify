/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.core;

import java.util.Objects;
import java.util.Set;
import org.testify.ApplicationInstance;
import org.testify.TestContext;
import org.testify.annotation.Application;

/**
 * Default implementation of {@link ApplicationInstance} contract.
 *
 * @author saden
 */
public class DefaultApplicationInstance<T> implements ApplicationInstance<T> {

    private final TestContext testContext;
    private final Application application;
    private final T initializer;
    private final Set<Class<?>> handlers;

    /**
     * Create a new application instance.
     *
     * @param <T> application initializer type
     * @param testContext the test context
     * @param application the application annotation
     * @param initializer the application initializer
     * @param handlers the application initializer handlers
     * @return an application instance
     */
    public static <T> ApplicationInstance of(TestContext testContext,
            Application application,
            T initializer,
            Set<Class<?>> handlers) {
        return new DefaultApplicationInstance(testContext, application, initializer, handlers);
    }

    DefaultApplicationInstance(TestContext testContext,
            Application application,
            T initializer,
            Set<Class<?>> handlers) {
        this.application = application;
        this.testContext = testContext;
        this.initializer = initializer;
        this.handlers = handlers;
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
    public T getInitializer() {
        return initializer;
    }

    @Override
    public Set<Class<?>> getHandlers() {
        return handlers;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.application);
        hash = 23 * hash + Objects.hashCode(this.testContext);
        hash = 23 * hash + Objects.hashCode(this.initializer);
        hash = 23 * hash + Objects.hashCode(this.handlers);
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
        if (!Objects.equals(this.initializer, other.initializer)) {
            return false;
        }
        return Objects.equals(this.handlers, other.handlers);
    }

    @Override
    public String toString() {
        return "DefaultApplicationInstance{"
                + ", testContext=" + testContext
                + "application=" + application
                + ", initializer=" + initializer
                + ", handlers=" + handlers
                + '}';
    }

}
