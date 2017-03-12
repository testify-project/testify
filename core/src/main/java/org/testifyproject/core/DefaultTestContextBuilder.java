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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;

public class DefaultTestContextBuilder {

    private StartStrategy resourceStartStrategy;
    private Object testInstance;
    private TestDescriptor testDescriptor;
    private MethodDescriptor methodDescriptor;
    private TestReifier testReifier;
    private MockProvider mockProvider;
    private Map<String, Object> properties = new ConcurrentHashMap<>();
    private Map<String, String> dependencies = Collections.EMPTY_MAP;

    public DefaultTestContextBuilder resourceStartStrategy(StartStrategy resourceStartStrategy) {
        this.resourceStartStrategy = resourceStartStrategy;
        return this;
    }

    public DefaultTestContextBuilder testInstance(Object testInstance) {
        this.testInstance = testInstance;
        return this;
    }

    public DefaultTestContextBuilder testDescriptor(TestDescriptor testDescriptor) {
        this.testDescriptor = testDescriptor;
        return this;
    }

    public DefaultTestContextBuilder methodDescriptor(MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
        return this;
    }

    public DefaultTestContextBuilder testReifier(TestReifier testReifier) {
        this.testReifier = testReifier;
        return this;
    }

    public DefaultTestContextBuilder mockProvider(MockProvider mockProvider) {
        this.mockProvider = mockProvider;
        return this;
    }

    public DefaultTestContextBuilder properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public DefaultTestContextBuilder dependencies(Map<String, String> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public TestContext build() {
        DefaultTestContext testContext = new DefaultTestContext();

        testContext.setDependencies(dependencies);
        testContext.setMethodDescriptor(methodDescriptor);
        testContext.setMockProvider(mockProvider);
        testContext.setProperties(properties);
        testContext.setResourceStartStrategy(resourceStartStrategy);
        testContext.setTestDescriptor(testDescriptor);
        testContext.setTestInstance(testInstance);
        testContext.setTestReifier(testReifier);

        return testContext;
    }

}
