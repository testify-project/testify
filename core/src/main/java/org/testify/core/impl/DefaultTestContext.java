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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.testify.CutDescriptor;
import org.testify.MethodDescriptor;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;

/**
 * A small context class that contains reference to the test testInstance, the
 * test testDescriptor, and helper methods.
 *
 * @author saden
 */
public class DefaultTestContext implements TestContext {

    private final Boolean startResources;
    private final Object testInstance;
    private final MethodDescriptor methodDescriptor;
    private final TestDescriptor testDescriptor;
    private final CutDescriptor cutDescriptor;
    private final Map<String, String> dependencies;

    public DefaultTestContext(
            Boolean startResources,
            Object testInstance,
            MethodDescriptor methodDescriptor,
            TestDescriptor descriptor,
            CutDescriptor cutDescriptor,
            Map<String, String> dependencies) {
        this.methodDescriptor = methodDescriptor;
        this.startResources = startResources;
        this.testInstance = testInstance;
        this.testDescriptor = descriptor;
        this.cutDescriptor = cutDescriptor;
        this.dependencies = dependencies;
    }

    /**
     * Get a unique name to identify the test context.
     *
     * @return a unqiue name
     */
    @Override
    public String getName() {
        return getClassName() + "." + getMethodName();
    }

    /**
     * The name of the test method associated with the test context.
     *
     * @return test method name.
     */
    @Override
    public String getMethodName() {
        return methodDescriptor.getName();
    }

    /**
     * Get test class associated with the test context.
     *
     * @return the test class instance.
     */
    @Override
    public Class<?> getTestClass() {
        return methodDescriptor.getDeclaringClass();
    }

    /**
     * The simple name of the test class.
     *
     * @return the test class simple name.
     */
    @Override
    public String getClassName() {
        return methodDescriptor.getDeclaringClassName();
    }

    /**
     * Determine whether test resources such as required resources and container
     * resources should be eagerly started. Note that in certain instances the
     * start of resources has to be delayed until
     *
     * @return true if the resources should be started, false otherwise.
     */
    @Override
    public Boolean getStartResources() {
        return startResources;
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
    public Optional<CutDescriptor> getCutDescriptor() {
        return Optional.ofNullable(cutDescriptor);
    }

    @Override
    public TestReifier getTestReifier() {
        return new DefaultTestReifier(testInstance, testDescriptor, cutDescriptor);
    }

    @Override
    public Map<String, String> getDependencies() {
        return dependencies;
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

}
