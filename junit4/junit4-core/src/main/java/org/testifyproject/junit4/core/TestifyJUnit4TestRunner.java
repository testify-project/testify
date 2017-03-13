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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.MDC;
import org.testifyproject.CutDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.TestRunner;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.DefaultTestReifier;
import org.testifyproject.core.TestCategory;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.analyzer.DefaultMethodDescriptor;
import org.testifyproject.core.util.AnalyzerUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.mock.MockitoMockProvider;
import org.testifyproject.trait.LoggingTrait;

/**
 * Base class for all Testify Unit Runners. This class analyzes the test class,
 * looks for specified test runner, starts and stops the tests.
 *
 * @author saden
 */
public abstract class TestifyJUnit4TestRunner extends BlockJUnit4ClassRunner implements LoggingTrait {

    /**
     * Create a new test runner instance for the class under test.
     *
     * @param testClass the test class type
     * @param level test category level
     *
     * @throws InitializationError thrown if the test class is malformed
     */
    public TestifyJUnit4TestRunner(Class<?> testClass, TestCategory.Level level) throws InitializationError {
        super(testClass);
        try {
            filter(TestifyJUnit4CategoryFilter.of(level));
        }
        catch (NoTestsRemainException e) {
            //we can ignore this exception
            debug("No test remain");
        }
    }

    /**
     * Get the dependencies required to run the tests.
     *
     * @return the test dependencies, empty map otherwise
     */
    protected abstract Map<String, String> getDependencies();

    /**
     * Get the test resource start strategy which indicates when test resources
     * are started.
     *
     * @return the test resource strategy
     */
    protected abstract StartStrategy getResourceStartStrategy();

    /**
     * Get the test runner used to run the tests.
     *
     * @return the test runner
     */
    protected abstract Class<? extends TestRunner> getTestRunnerClass();

    @Override
    public void run(RunNotifier runNotifier) {
        Description description = getDescription();
        TestifyJUnit4RunNotifier notifier = new TestifyJUnit4RunNotifier(runNotifier, description);

        try {
            debug("Creating Statement");
            Statement statement = classBlock(notifier);
            debug("Evaluating Statement");
            statement.evaluate();
        }
        catch (AssumptionViolatedException e) {
            notifier.addFailedAssumption(e);
        }
        catch (StoppedByUserException e) {
            throw e;
        }
        catch (Throwable t) {
            notifier.addFailure(t);
            notifier.pleaseStop();
        }
        finally {
        }
    }

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier runNotifier) {
        Description description = describeChild(method);
        TestClass testClass = getTestClass();
        Class<?> javaClass = testClass.getJavaClass();

        TestUnitHolder.INSTANCE.create();
        TestifyJUnit4RunNotifier notifier = (TestifyJUnit4RunNotifier) runNotifier;

        MDC.put("test", javaClass.getSimpleName());
        MDC.put("method", method.getName());

        if (isIgnored(method)) {
            notifier.fireTestIgnored(description);
        } else {
            try {
                runLeaf(methodBlock(method), description, notifier);
            }
            catch (Throwable t) {
                notifier.addFailure(t);
                notifier.pleaseStop();
            }
            finally {
                if (!isIgnored(method)) {
                    Optional<TestUnit> result = TestUnitHolder.INSTANCE.get();

                    if (result.isPresent()) {
                        TestUnit testUnit = result.get();
                        TestContext testContext = testUnit.getTestContext();

                        debug("performing cleanup of '{}'", testContext.getName());

                        if (testContext.hasErrors()) {
                            debug("reporting test context errors");

                            testContext.getErrors()
                                    .stream()
                                    .forEach(failure -> {
                                        notifier.addFailure(new IllegalStateException(failure));
                                    });
                        }

                        TestRunner testRunner = testUnit.getTestRunner();
                        testRunner.stop();
                        TestUnitHolder.INSTANCE.remove();
                    }
                }
            }
        }
    }

    @Override
    protected Statement methodBlock(FrameworkMethod frameworkMethod) {
        TestClass testClass = getTestClass();
        Class<?> javaClass = testClass.getJavaClass();
        debug("test class classloader: {}", javaClass.getClassLoader());

        TestUnit testUnit = TestUnitHolder.INSTANCE.get().get();

        Object testInstance;

        try {
            debug("creating instance of test class {}", javaClass.getName());
            testInstance = createTest();
            testUnit.setTestInstance(testInstance);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }

        debug("creating test decriptor for test class {}", javaClass.getName());
        TestDescriptor testDescriptor = AnalyzerUtil.INSTANCE.analyzeTestClass(javaClass);
        testUnit.setTestDescriptor(testDescriptor);

        Method method = frameworkMethod.getMethod();
        testUnit.setTestMethod(method);
        MDC.put("test", javaClass.getSimpleName());
        MDC.put("method", method.getName());

        debug("creating method decriptor for test method {}#{}", javaClass.getName(), method.getName());
        MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method);

        debug("creating test context for test run {}#{}", javaClass.getName(), method.getName());

        TestReifier testReifier = ServiceLocatorUtil.INSTANCE
                .getOneOrDefault(TestReifier.class, DefaultTestReifier.class);
        MockProvider mockProvider = ServiceLocatorUtil.INSTANCE
                .getOneOrDefault(MockProvider.class, MockitoMockProvider.class);

        Map<String, Object> properties = new HashMap<>();

        TestContext testContext = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(getResourceStartStrategy())
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .methodDescriptor(methodDescriptor)
                .testReifier(testReifier)
                .mockProvider(mockProvider)
                .properties(properties)
                .dependencies(getDependencies())
                .build();

        Optional<Field> cutField = testDescriptor.getCutField();

        if (cutField.isPresent()) {
            CutDescriptor cutDescriptor = AnalyzerUtil.INSTANCE.analyzeCutField(cutField.get());
            testContext.addProperty(TestContextProperties.CUT_DESCRIPTOR, cutDescriptor);
        }

        testUnit.setTestContext(testContext);
        Class<? extends TestRunner> testRunnerClass = getTestRunnerClass();

        debug("getting test runner implementation '{}' from the class path", testRunnerClass.getName());
        TestRunner testRunner = ServiceLocatorUtil.INSTANCE.getOne(TestRunner.class, testRunnerClass);
        testUnit.setTestRunner(testRunner);

        debug("starting test runner '{}'", testRunnerClass.getName());
        testRunner.start(testContext);

        debug("executing test method {}#{}'", javaClass.getName(), method.getName());
        Statement statement = methodInvoker(frameworkMethod, testInstance);
        statement = possiblyExpectingExceptions(frameworkMethod, testInstance, statement);
        debug("executing statement before lifecycle methods for {}#{}'", javaClass.getName(), method.getName());
        statement = withBefores(frameworkMethod, testInstance, statement);
        debug("executing statement after lifecycle methods for {}#{}'", javaClass.getName(), method.getName());
        statement = withAfters(frameworkMethod, testInstance, statement);
        debug("executing statement rules lifecycle methods for {}#{}'", javaClass.getName(), method.getName());
        statement = withRules(frameworkMethod, testInstance, statement);

        return statement;
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
