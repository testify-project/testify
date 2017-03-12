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
import java.util.Optional;
import java.util.function.Consumer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;

/**
 * A utility class for holding inheritable thread local instances of a
 * {@link TestUnit}.
 *
 * @author saden
 */
public class TestUnitHolder {

    private static final InheritableThreadLocal<TestUnit> THREAD_LOCAL = new InheritableThreadLocal<>();
    public static final TestUnitHolder INSTANCE = new TestUnitHolder();

    public TestUnit create() {
        TestUnit testUnit = new TestUnit();
        THREAD_LOCAL.set(testUnit);

        return testUnit;
    }

    public Optional<TestUnit> get() {
        return Optional.ofNullable(THREAD_LOCAL.get());
    }

    public void remove() {
        THREAD_LOCAL.remove();
    }

    public void setTestInstance(Object testInstance) {
        set(testUnit -> testUnit.setTestInstance(testInstance));
    }

    public void setTestContext(TestContext testContext) {
        set(testUnit -> testUnit.setTestContext(testContext));
    }

    public void setTestRunner(TestRunner testRunner) {
        set(testUnit -> testUnit.setTestRunner(testRunner));
    }

    public void setTestDescriptor(TestDescriptor testDescriptor) {
        set(testUnit -> testUnit.setTestDescriptor(testDescriptor));
    }

    public void setTestMethod(Method testMethod) {
        set(testUnit -> testUnit.setTestMethod(testMethod));
    }

    private void set(Consumer<TestUnit> consumer) {
        TestUnit testUnit = THREAD_LOCAL.get();
        if (testUnit != null) {
            consumer.accept(testUnit);
        }
    }
}
