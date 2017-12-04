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

import static java.util.stream.Collectors.joining;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.testifyproject.LocalResourceInfo;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.RemoteResourceInfo;
import org.testifyproject.ServiceInstance;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.TestifyException;
import org.testifyproject.VirtualResourceInfo;
import org.testifyproject.core.util.LoggingUtil;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A small context class that contains reference to the test testInstance, the test
 * testDescriptor, and helper methods.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultTestContext implements TestContext {

    private static final LoggingUtil LOGGING_UTIL = LoggingUtil.INSTANCE;

    private Object testInstance;
    private TestDescriptor testDescriptor;
    private MethodDescriptor methodDescriptor;
    private TestRunner testRunner;
    private TestConfigurer testConfigurer;
    private MockProvider mockProvider;
    private Map<String, Object> properties;
    private Class<? extends Annotation> testCategory;

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public String getTestName() {
        return testDescriptor.getTestClassName();
    }

    @Override
    public String getMethodName() {
        return methodDescriptor.getName();
    }

    @Override
    public MethodDescriptor getTestMethodDescriptor() {
        return methodDescriptor;
    }

    @Override
    public String getName() {
        return getTestName() + "." + getMethodName();
    }

    @Override
    public Class<?> getTestClass() {
        return testDescriptor.getTestClass();
    }

    @Override
    public ClassLoader getTestClassLoader() {
        return testDescriptor.getTestClassLoader();
    }

    @Override
    public Class<? extends Annotation> getTestCategory() {
        return testCategory;
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
    public TestRunner getTestRunner() {
        return testRunner;
    }

    @Override
    public TestConfigurer getTestConfigurer() {
        return testConfigurer;
    }

    @Override
    public MockProvider getMockProvider() {
        return mockProvider;
    }

    @Override
    public Optional<ServiceInstance> getServiceInstance() {
        return findProperty(TestContextProperties.SERVICE_INSTANCE);
    }

    @Override
    public Optional<SutDescriptor> getSutDescriptor() {
        return findProperty(TestContextProperties.SUT_DESCRIPTOR);
    }

    @Override
    public <T> Optional<T> getSutInstance() {
        return findProperty(TestContextProperties.SUT_INSTANCE);
    }

    @Override
    public Collection<LocalResourceInfo> getLocalResources() {
        return findCollection(TestContextProperties.LOCAL_RESOURCE_INSTANCES);
    }

    @Override
    public Collection<RemoteResourceInfo> getRemoteResources() {
        return findCollection(TestContextProperties.REMOTE_RESOURCE_INSTANCES);
    }

    @Override
    public Collection<VirtualResourceInfo> getVirtualResources() {
        return findCollection(TestContextProperties.VIRTUAL_RESOURCE_INSTANCES);
    }

    void setTestCategory(Class<? extends Annotation> testCategory) {
        this.testCategory = testCategory;
    }

    void setTestInstance(Object testInstance) {
        this.testInstance = testInstance;
    }

    void setTestDescriptor(TestDescriptor testDescriptor) {
        this.testDescriptor = testDescriptor;
    }

    void setTestMethodDescriptor(MethodDescriptor methodDescriptor) {
        this.methodDescriptor = methodDescriptor;
    }

    void setTestRunner(TestRunner testRunner) {
        this.testRunner = testRunner;
    }

    void setTestConfigurer(TestConfigurer testConfigurer) {
        this.testConfigurer = testConfigurer;
    }

    void setMockProvider(MockProvider mockProvider) {
        this.mockProvider = mockProvider;
    }

    void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public TestContext addError(String messageFormat, Object... args) {
        String message = LOGGING_UTIL.formatMessage(messageFormat, args);
        addCollectionElement(TestContextProperties.TEST_ERRORS, message);

        return this;
    }

    @Override
    public TestContext addError(Boolean condition, String messageFormat, Object... args) {
        if (condition) {
            String message = LOGGING_UTIL.formatMessage(messageFormat, args);
            addCollectionElement(TestContextProperties.TEST_ERRORS, message);
        }

        return this;
    }

    @Override
    public TestContext addWarning(String messageFormat, Object... args) {
        String message = LOGGING_UTIL.formatMessage(messageFormat, args);
        addCollectionElement(TestContextProperties.TEST_WARNINGS, message);

        return this;
    }

    @Override
    public TestContext addWarning(Boolean condition, String messageFormat, Object... args) {
        if (condition) {
            String message = LOGGING_UTIL.formatMessage(messageFormat, args);
            addCollectionElement(TestContextProperties.TEST_WARNINGS, message);
        }

        return this;
    }

    @Override
    public Collection<String> getError() {
        return findCollection(TestContextProperties.TEST_ERRORS);
    }

    @Override
    public Collection<String> getWarnings() {
        return findCollection(TestContextProperties.TEST_WARNINGS);
    }

    @Override
    public void verify() {
        String warnings = getWarnings().stream().collect(joining("\n"));
        if (!warnings.isEmpty()) {
            LOGGING_UTIL.warn("Test Warnings:\n{}", warnings);
            //XXX: once printed the warnings should be cleared so they cant be printed again if
            //verify is called multiple times.
            getWarnings().clear();
        }

        String errors = getError().stream().collect(joining("\n"));
        if (!errors.isEmpty()) {
            throw TestifyException.of("Test Errors:\n" + errors);
        }
    }

}
