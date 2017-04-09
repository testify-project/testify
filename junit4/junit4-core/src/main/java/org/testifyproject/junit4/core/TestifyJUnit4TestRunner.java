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
import org.junit.rules.MethodRule;
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
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.TestRunner;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.DefaultTestReifier;
import org.testifyproject.core.TestCategory;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.analyzer.DefaultMethodDescriptor;
import org.testifyproject.core.util.AnalyzerUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.TestRunnerSettings;
import org.testifyproject.guava.common.base.Throwables;
import org.testifyproject.mock.MockitoMockProvider;

/**
 * Base class for all Testify Unit Runners. This class analyzes the test class, looks for specified
 * test runner, starts and stops the tests.
 *
 * @author saden
 */
public abstract class TestifyJUnit4TestRunner extends BlockJUnit4ClassRunner implements TestRunnerSettings {

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
            filter(TestifyJUnit4CategoryFilter.of(level, System.getProperty("testify.categories")));
        } catch (NoTestsRemainException e) {
            LoggingUtil.INSTANCE.debug("No test remain", e);
        }
    }

    @Override
    public void run(RunNotifier runNotifier) {
        Description testDescription = getDescription();
        TestifyJUnit4RunNotifier notifier = TestifyJUnit4RunNotifier.of(runNotifier, testDescription);

        try {
            LoggingUtil.INSTANCE.debug("Creating Statement");
            Statement statement = classBlock(notifier);
            LoggingUtil.INSTANCE.debug("Evaluating Statement");
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            notifier.addFailedAssumption(e);
        } catch (Throwable t) {
            Throwables.propagateIfInstanceOf(t, StoppedByUserException.class);
            notifier.addFailure(t);
            notifier.pleaseStop();
        }
    }

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier runNotifier) {
        Description methodDescription = describeChild(method);
        TestClass testClass = getTestClass();
        Class<?> javaClass = testClass.getJavaClass();

        TestifyJUnit4RunNotifier notifier = (TestifyJUnit4RunNotifier) runNotifier;

        MDC.put("test", javaClass.getSimpleName());
        MDC.put("method", method.getName());

        if (isIgnored(method)) {
            notifier.fireTestIgnored(methodDescription);
        } else {
            try {
                runLeaf(methodBlock(method), methodDescription, notifier);
            } catch (Exception e) {
                notifier.addFailure(e);
                notifier.pleaseStop();
            } finally {
                if (!isIgnored(method)) {
                    Optional<TestContext> result = TestContextHolder.INSTANCE.get();

                    if (result.isPresent()) {
                        TestContext testContext = result.get();

                        LoggingUtil.INSTANCE.debug("performing cleanup of '{}'", testContext.getName());

                        TestRunner testRunner = testContext.getTestRunner();
                        testRunner.stop();
                        TestContextHolder.INSTANCE.remove();
                    }
                }
            }
        }
    }

    @Override
    protected Statement methodBlock(FrameworkMethod frameworkMethod) {
        TestClass testClass = getTestClass();
        Class<?> javaClass = testClass.getJavaClass();
        LoggingUtil.INSTANCE.debug("test class classloader: {}", javaClass.getClassLoader());

        Object testInstance;

        try {
            LoggingUtil.INSTANCE.debug("creating instance of test class {}", javaClass.getName());
            testInstance = createTest();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        LoggingUtil.INSTANCE.debug("creating test decriptor for test class {}", javaClass.getName());
        TestDescriptor testDescriptor = AnalyzerUtil.INSTANCE.analyzeTestClass(javaClass);

        Method method = frameworkMethod.getMethod();
        MDC.put("test", javaClass.getSimpleName());
        MDC.put("method", method.getName());

        LoggingUtil.INSTANCE.debug("creating method decriptor for test method {}#{}", javaClass.getName(), method.getName());
        MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method);

        Class<TestReifier> testReifierType = TestReifier.class;
        TestReifier testReifier = ServiceLocatorUtil.INSTANCE
                .getOneOrDefault(testReifierType, DefaultTestReifier.class);

        Class<MockProvider> mockProviderType = MockProvider.class;
        MockProvider mockProvider = ServiceLocatorUtil.INSTANCE
                .getOneOrDefault(mockProviderType, MockitoMockProvider.class);

        Class<? extends TestRunner> testRunnerClass = getTestRunnerClass();
        TestRunner testRunner = ServiceLocatorUtil.INSTANCE.getOne(TestRunner.class, testRunnerClass);

        Map<String, Object> properties = new HashMap<>();

        TestContext testContext = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(getResourceStartStrategy())
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .methodDescriptor(methodDescriptor)
                .testRunner(testRunner)
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

        LoggingUtil.INSTANCE.debug(
                "starting test runner '{}'",
                testRunnerClass.getName()
        );
        testRunner.start(testContext);

        LoggingUtil.INSTANCE.debug(
                "executing test method {}#{}'",
                javaClass.getName(), method.getName()
        );
        Statement statement = methodInvoker(frameworkMethod, testInstance);
        statement = possiblyExpectingExceptions(frameworkMethod, testInstance, statement);

        LoggingUtil.INSTANCE.debug(
                "executing statement before lifecycle methods for {}#{}'",
                javaClass.getName(),
                method.getName()
        );
        statement = withBefores(frameworkMethod, testInstance, statement);

        LoggingUtil.INSTANCE.debug(
                "executing statement after lifecycle methods for {}#{}'",
                javaClass.getName(),
                method.getName()
        );
        statement = withAfters(frameworkMethod, testInstance, statement);

        LoggingUtil.INSTANCE.debug(
                "executing statement rules lifecycle methods for {}#{}'",
                javaClass.getName(),
                method.getName()
        );

        statement = applyRules(frameworkMethod, testInstance, statement);

        return statement;
    }

    private Statement applyRules(FrameworkMethod method, Object target,
            Statement statement) {
        List<TestRule> testRules = getTestRules(target);
        Statement result = statement;
        result = applyMethodRules(method, testRules, target, result);
        result = applyTestRules(method, testRules, result);

        return result;
    }

    private Statement applyMethodRules(FrameworkMethod method, List<TestRule> testRules,
            Object target, Statement result) {
        Statement withMethodRules = result;

        for (MethodRule each : findMethodRules(target)) {
            if (!(each instanceof TestRule && testRules.contains((TestRule) each))) {
                withMethodRules = each.apply(withMethodRules, method, target);
            }
        }

        return withMethodRules;
    }

    private List<MethodRule> findMethodRules(Object target) {
        return rules(target);
    }

    private Statement applyTestRules(FrameworkMethod method, List<TestRule> testRules,
            Statement statement) {
        return testRules.isEmpty() ? statement
                : new RunRules(statement, testRules, describeChild(method));
    }

}
