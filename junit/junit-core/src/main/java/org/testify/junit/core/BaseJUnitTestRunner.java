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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.MDC;
import org.testify.CutDescriptor;
import org.testify.MethodDescriptor;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.TestRunner;
import org.testify.core.analyzer.DefaultMethodDescriptor;
import org.testify.core.impl.DefaultTestContext;
import org.testify.core.util.AnalyzerUtil;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.guava.common.base.Throwables;
import org.testify.trait.LoggingTrait;

/**
 * Base class for all Testify Unit Runners. This class analyzes the test class,
 * looks for specified test runner, starts and stops the tests.
 *
 * @author saden
 */
public abstract class BaseJUnitTestRunner extends BlockJUnit4ClassRunner implements LoggingTrait {

    private static final Map<Class, TestDescriptor> TEST_DESCRIPTORS;
    private static final Map<Field, CutDescriptor> CUT_DESCRIPTORS;

    static {
        TEST_DESCRIPTORS = new ConcurrentHashMap<>();
        CUT_DESCRIPTORS = new ConcurrentHashMap<>();
    }

    private final Class<? extends TestRunner> testRunnterClass;
    private final Boolean startResources;
    private final Map<String, String> dependencies;

    /**
     * Create a new test runner instance for the class under test.
     *
     * @param testClass the test class type
     * @param testRunnterClass the test runner class type
     * @param startResources whether to start required resources
     * @param dependencies dependencies required to run the tests
     *
     * @throws InitializationError thrown if the test class is malformed.
     */
    public BaseJUnitTestRunner(Class<?> testClass,
            Class<? extends TestRunner> testRunnterClass,
            Boolean startResources,
            Map<String, String> dependencies) throws InitializationError {
        super(testClass);
        this.testRunnterClass = testRunnterClass;
        this.startResources = startResources;
        this.dependencies = dependencies;
    }

    public TestDescriptor getTestDescriptor(Class<?> testClass) {
        return TEST_DESCRIPTORS.computeIfAbsent(testClass, p -> {
            return AnalyzerUtil.INSTANCE.analyzeTestClass(testClass);
        });
    }

    public CutDescriptor getCutDescriptor(Field cutField) {
        return CUT_DESCRIPTORS.computeIfAbsent(cutField, p -> {
            return AnalyzerUtil.INSTANCE.analyzeCutField(cutField);
        });
    }

    @Override
    public void run(RunNotifier notifier) {
        Description description = getDescription();
        JUnitTestNotifier testNotifier = new JUnitTestNotifier(notifier, description);

        try {
            debug("Creating Statement");
            Statement statement = classBlock(testNotifier);
            debug("Evaluating Statement");
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.addFailedAssumption(e);
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            Throwable cause = Throwables.getRootCause(e);
            testNotifier.addFailure(cause);
            testNotifier.pleaseStop();
        } finally {
        }
    }

    @Override
    protected Statement methodBlock(FrameworkMethod frameworkMethod) {
        TestClass testClass = getTestClass();
        Class<?> javaClass = testClass.getJavaClass();
        TestUnit testUnit = TestUnitHolder.INSTANCE.get().get();

        Object testInstance;

        try {
            debug("Creating instance of test class {}", javaClass.getName());
            testInstance = createTest();
            testUnit.setTestInstance(testInstance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        debug("Creating test decriptor for test class {}", javaClass.getName());
        TestDescriptor testDescriptor = getTestDescriptor(javaClass);
        testUnit.setTestDescriptor(testDescriptor);
        CutDescriptor cutDescriptor = null;

        if (testDescriptor.getCutField().isPresent()) {
            cutDescriptor = getCutDescriptor(testDescriptor.getCutField().get());
        }

        Method method = frameworkMethod.getMethod();
        testUnit.setTestMethod(method);
        MDC.put("test", javaClass.getSimpleName());
        MDC.put("method", method.getName());

        debug("Creating method decriptor for test method {}#{}", javaClass.getName(), method.getName());
        MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method);

        debug("Creating test context for test run {}#{}", javaClass.getName(), method.getName());
        TestReifier testReifier = ServiceLocatorUtil.INSTANCE.getOne(TestReifier.class);
        TestContext testContext = new DefaultTestContext(
                startResources,
                testInstance,
                methodDescriptor,
                testDescriptor,
                cutDescriptor,
                testReifier,
                dependencies
        );

        testUnit.setTestContext(testContext);

        debug("Getting test runner implementation '{}' from the class path", testRunnterClass.getName());
        TestRunner testRunner = ServiceLocatorUtil.INSTANCE.getOne(TestRunner.class, testRunnterClass);
        testUnit.setTestRunner(testRunner);

        debug("Starting test runner '{}'", testRunnterClass.getName());
        testRunner.start(testContext);

        debug("Executing test method {}#{}'", javaClass.getName(), method.getName());
        Statement statement = methodInvoker(frameworkMethod, testInstance);
        statement = possiblyExpectingExceptions(frameworkMethod, testInstance, statement);
        debug("Executing statement before lifecycle methods for {}#{}'", javaClass.getName(), method.getName());
        statement = withBefores(frameworkMethod, testInstance, statement);
        debug("Executing statement after lifecycle methods for {}#{}'", javaClass.getName(), method.getName());
        statement = withAfters(frameworkMethod, testInstance, statement);
        debug("Executing statement rules lifecycle methods for {}#{}'", javaClass.getName(), method.getName());
        statement = withRules(frameworkMethod, testInstance, statement);

        return statement;

    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        TestClass testClass = getTestClass();
        Class<?> javaClass = testClass.getJavaClass();
        TestUnitHolder.INSTANCE.create();

        MDC.put("test", javaClass.getSimpleName());
        MDC.put("method", method.getName());

        try {
            super.runChild(method, notifier);
        } catch (Throwable t) {
            Description description = getDescription();
            Failure failure = new Failure(description, t);
            notifier.fireTestFailure(failure);
            error("Test method '{}' failed.", method.getName(), t);
        } finally {
            if (!isIgnored(method)) {
                Optional<TestUnit> result = TestUnitHolder.INSTANCE.get();
                if (result.isPresent()) {
                    debug("Performing cleanup {}#{}'", javaClass.getName(), method.getName());
                    TestUnit testUnit = result.get();
                    debug("TestUnit: {}", testUnit);
                    TestRunner testRunner = testUnit.getTestRunner();
                    testRunner.stop();

                    TestContext testContext = testUnit.getTestContext();
                    if (testContext.hasErrors()) {
                        Description description = getDescription();

                        testContext.getErrors()
                                .stream()
                                .map(message -> new IllegalStateException(message))
                                .map(exception -> new Failure(description, exception))
                                .forEach(failure -> {
                                    notifier.fireTestFailure(failure);
                                });
                    }
                }

                debug("Remving TestUnit instance {}#{}'", javaClass.getName(), method.getName());
                TestUnitHolder.INSTANCE.remove();
            }
        }
    }

    private Statement withRules(FrameworkMethod method, Object target,
            Statement statement) {
        List<TestRule> testRules = getTestRules(target);
        Statement result = statement;
        result = withMethodRules(method, testRules, target, result);
        result = withTestRules(method, testRules, result);

        return result;
    }

    private Statement withMethodRules(FrameworkMethod method, List<TestRule> testRules,
            Object target, Statement result) {
        for (org.junit.rules.MethodRule each : getMethodRules(target)) {
            if (!testRules.contains(each)) {
                result = each.apply(result, method, target);
            }
        }
        return result;
    }

    private List<org.junit.rules.MethodRule> getMethodRules(Object target) {
        return rules(target);
    }

    private Statement withTestRules(FrameworkMethod method, List<TestRule> testRules,
            Statement statement) {
        return testRules.isEmpty() ? statement
                : new RunRules(statement, testRules, describeChild(method));
    }

}
