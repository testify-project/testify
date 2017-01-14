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
package org.testify.core.impl;

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
public class DefaultApplicationInstance implements ApplicationInstance {

    private final Application application;
    private final TestContext testContext;
    private final Object servletContainerInitializer;
    private final Set<Class<?>> servletContainerInitializerHandlers;

    public static ApplicationInstance of(Application application,
            TestContext testContext,
            Object servletContainerInitializer,
            Set<Class<?>> servletContainerInitializerHandlers) {
        return new DefaultApplicationInstance(application,
                testContext,
                servletContainerInitializer,
                servletContainerInitializerHandlers);
    }

    DefaultApplicationInstance(Application application,
            TestContext testContext,
            Object servletContainerInitializer,
            Set<Class<?>> servletContainerInitializerHandlers) {
        this.application = application;
        this.testContext = testContext;
        this.servletContainerInitializer = servletContainerInitializer;
        this.servletContainerInitializerHandlers = servletContainerInitializerHandlers;
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
    public <T> T getServletContainerInitializer() {
        return (T) servletContainerInitializer;
    }

    @Override
    public Set<Class<?>> getServletContainerInitializerHandlers() {
        return servletContainerInitializerHandlers;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.application);
        hash = 23 * hash + Objects.hashCode(this.testContext);
        hash = 23 * hash + Objects.hashCode(this.servletContainerInitializer);
        hash = 23 * hash + Objects.hashCode(this.servletContainerInitializerHandlers);
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
        if (!Objects.equals(this.servletContainerInitializer, other.servletContainerInitializer)) {
            return false;
        }
        return Objects.equals(this.servletContainerInitializerHandlers, other.servletContainerInitializerHandlers);
    }

    @Override
    public String toString() {
        return "DefaultApplicationInstance{"
                + "application=" + application
                + ", testContext=" + testContext
                + ", servletContainerInitializer=" + servletContainerInitializer
                + ", servletContainerInitializerHandlers=" + servletContainerInitializerHandlers
                + '}';
    }

}
