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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.testifyproject.TestContext;

/**
 * A utility class for holding inheritable thread local instances of a {@link TestContext}.
 *
 * @author saden
 */
public class TestContextHolder {

    private final InheritableThreadLocal<TestContext> threadLocal;

    /**
     * An instance of the TestContextHolder.
     */
    public static final TestContextHolder INSTANCE =
            new TestContextHolder(new InheritableThreadLocal<>());

    TestContextHolder(InheritableThreadLocal<TestContext> threadLocal) {
        this.threadLocal = threadLocal;
    }

    /**
     * Create a new instance of TestContextHolder.
     *
     * @param threadLocal the underlying thread local instance holder
     * @return a new instance
     */
    public static TestContextHolder of(InheritableThreadLocal<TestContext> threadLocal) {
        return new TestContextHolder(threadLocal);
    }

    /**
     * Set the test context in current thread.
     *
     * @param testContext the value to be stored
     */
    public void set(TestContext testContext) {
        threadLocal.set(testContext);
    }

    /**
     * Get the test context in the current thread.
     *
     * @return the current thread's value, empty optional otherwise
     */
    public Optional<TestContext> get() {
        return Optional.ofNullable(threadLocal.get());
    }

    /**
     * Removes the test context in current thread.
     */
    public void remove() {
        threadLocal.remove();
    }

    /**
     * Determine if the test context is set in the current thread.
     *
     * @return returns true the test context has been set, false otherwise
     */
    public boolean isPresent() {
        return threadLocal.get() != null;
    }

    /**
     * Execute the given consumer function if the the current thread has a test context.
     *
     * @param consumer the consumer function
     */
    public void command(Consumer<TestContext> consumer) {
        TestContext testContext = threadLocal.get();

        if (testContext != null) {
            consumer.accept(testContext);
        }
    }

    /**
     * Execute the given function if the the current thread has a test context.
     *
     * @param <R> the function result type
     * @param function the function that will be called to get the results
     * @return the function result
     */
    public <R> R query(Function<TestContext, R> function) {
        TestContext testContext = threadLocal.get();
        R result = null;

        if (testContext != null) {
            result = function.apply(testContext);
        }

        return result;
    }

}
