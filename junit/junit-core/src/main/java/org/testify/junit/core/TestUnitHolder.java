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
package org.testify.junit.core;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestRunner;

/**
 * A utility class for holding inheritable thread local instances of a
 * {@link TestUnit}.
 *
 * @author saden
 */
public class TestUnitHolder {

    private static final InheritableThreadLocal<TestUnit> LOCAL_INSTANCE = new InheritableThreadLocal<>();
    public static final TestUnitHolder INSTANCE = new TestUnitHolder();

    public TestUnit create() {
        TestUnit testUnit = new TestUnit();
        LOCAL_INSTANCE.set(testUnit);

        return testUnit;
    }

    public Optional<TestUnit> get() {
        return Optional.ofNullable(LOCAL_INSTANCE.get());
    }

    public void remove() {
        LOCAL_INSTANCE.remove();
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
        TestUnit testUnit = LOCAL_INSTANCE.get();
        if (testUnit != null) {
            consumer.accept(testUnit);
        }
    }
}
