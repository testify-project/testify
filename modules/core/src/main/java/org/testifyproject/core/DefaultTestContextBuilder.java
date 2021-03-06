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

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;

public class DefaultTestContextBuilder {

    private Class<? extends Annotation> testCategory;
    private Object testInstance;
    private TestDescriptor testDescriptor;
    private MethodDescriptor methodDescriptor;
    private TestConfigurer testConfigurer;
    private TestRunner testRunner;
    private MockProvider mockProvider;

    private final Map<String, Object> properties = new LinkedHashMap<>();

    /**
     * Create a new instance of DefaultTestContextBuilder.
     *
     * @return a new instance
     */
    public static DefaultTestContextBuilder builder() {
        return new DefaultTestContextBuilder();
    }

    public DefaultTestContextBuilder testCategory(Class<? extends Annotation> testCategory) {
        this.testCategory = testCategory;
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

    public DefaultTestContextBuilder testMethodDescriptor(MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
        return this;
    }

    public DefaultTestContextBuilder testRunner(TestRunner testRunner) {
        this.testRunner = testRunner;
        return this;
    }

    public DefaultTestContextBuilder testConfigurer(TestConfigurer testConfigurer) {
        this.testConfigurer = testConfigurer;
        return this;
    }

    public DefaultTestContextBuilder mockProvider(MockProvider mockProvider) {
        this.mockProvider = mockProvider;
        return this;
    }

    public DefaultTestContextBuilder properties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public TestContext build() {
        DefaultTestContext testContext = new DefaultTestContext();

        testContext.setTestMethodDescriptor(methodDescriptor);
        testContext.setMockProvider(mockProvider);
        testContext.setProperties(properties);
        testContext.setTestCategory(testCategory);
        testContext.setTestDescriptor(testDescriptor);
        testContext.setTestInstance(testInstance);
        testContext.setTestConfigurer(testConfigurer);
        testContext.setTestRunner(testRunner);

        return testContext;
    }

}
