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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.testifyproject.CutDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;

/**
 * A small context class that contains reference to the test testInstance, the
 * test testDescriptor, and helper methods.
 *
 * @author saden
 */
public class DefaultTestContext implements TestContext {

    private final StartStrategy resourceStartStrategy;
    private final Object testInstance;
    private final TestDescriptor testDescriptor;
    private final MethodDescriptor methodDescriptor;
    private final TestReifier testReifier;
    private final Map<String, Object> properties;
    private final Map<String, String> dependencies;

    public DefaultTestContext(
            StartStrategy resourceStartStrategy,
            Object testInstance,
            TestDescriptor testDescriptor,
            MethodDescriptor methodDescriptor,
            TestReifier testReifier,
            Map<String, Object> properties,
            Map<String, String> dependencies) {
        this.testDescriptor = testDescriptor;
        this.methodDescriptor = methodDescriptor;
        this.resourceStartStrategy = resourceStartStrategy;
        this.testInstance = testInstance;
        this.testReifier = testReifier;
        this.properties = properties;
        this.dependencies = dependencies;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public String getTestName() {
        return methodDescriptor.getDeclaringClassName();
    }

    @Override
    public String getMethodName() {
        return methodDescriptor.getName();
    }

    @Override
    public String getName() {
        return getTestName() + "." + getMethodName();
    }

    @Override
    public Class<?> getTestClass() {
        return methodDescriptor.getDeclaringClass();
    }

    @Override
    public StartStrategy getResourceStartStrategy() {
        return resourceStartStrategy;
    }

    @Override
    public Object getTestInstance() {
        return testInstance;
    }

    @Override
    public TestDescriptor getTestDescriptor() {
        return testDescriptor;
    }

    @Override
    public TestReifier getTestReifier() {
        return testReifier;
    }

    @Override
    public Map<String, String> getDependencies() {
        return dependencies;
    }

    @Override
    public Optional<CutDescriptor> getCutDescriptor() {
        return findProperty(TestContextProperties.CUT_DESCRIPTOR);
    }

    @Override
    public <T> Optional<T> getCutInstance() {
        return findProperty(TestContextProperties.CUT_INSTANCE);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.methodDescriptor);
        hash = 59 * hash + Objects.hashCode(this.testInstance);
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
        final DefaultTestContext other = (DefaultTestContext) obj;
        if (!Objects.equals(this.methodDescriptor, other.methodDescriptor)) {
            return false;
        }

        return Objects.equals(this.testInstance, other.testInstance);
    }

    @Override
    public String toString() {
        return "DefaultTestContext{"
                + "resourceStartStrategy=" + resourceStartStrategy
                + ", testDescriptor=" + testDescriptor
                + ", methodDescriptor=" + methodDescriptor
                + '}';
    }

}
