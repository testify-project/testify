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
package org.testifyproject.junit4.core;

import java.lang.reflect.Method;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;

/**
 * A container class for test unit that describes a test run.
 *
 * @author saden
 */
public class TestUnit {

    private TestContext testContext;
    private TestDescriptor testDescriptor;
    private TestRunner testRunner;
    private Object testInstance;
    private Method testMethod;

    public TestContext getTestContext() {
        return testContext;
    }

    void setTestContext(TestContext testContext) {
        this.testContext = testContext;
    }

    public TestDescriptor getTestDescriptor() {
        return testDescriptor;
    }

    void setTestDescriptor(TestDescriptor testDescriptor) {
        this.testDescriptor = testDescriptor;
    }

    public TestRunner getTestRunner() {
        return testRunner;
    }

    void setTestRunner(TestRunner testRunner) {
        this.testRunner = testRunner;
    }

    public Object getTestInstance() {
        return testInstance;
    }

    void setTestInstance(Object testInstance) {
        this.testInstance = testInstance;
    }

    void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    @Override
    public String toString() {
        return "TestUnit{"
                + "testContext=" + testContext
                + ", testDescriptor=" + testDescriptor
                + ", testRunner=" + testRunner
                + ", testInstance=" + testInstance
                + ", testMethod=" + testMethod
                + '}';
    }

}
