/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.junit5;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.core.DefaultTestConfigurer;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.analyzer.DefaultMethodDescriptor;
import org.testifyproject.core.setting.TestSettings;
import org.testifyproject.core.setting.TestSettingsProperties;
import org.testifyproject.core.util.AnalyzerUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.core.util.SettingUtil;
import org.testifyproject.mock.MockitoMockProvider;

/**
 * TODO.
 *
 * @author saden
 */
public class TestifyExtension
        implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    public static final String TEST_METHOD_KEY = "method";
    public static final String TEST_CLASS_KEY = "test";
    private TestDescriptor testDescriptor;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (context.getTestClass().isPresent()) {
            Class<?> testClass = context.getTestClass().get();
            testDescriptor = AnalyzerUtil.INSTANCE.analyzeTestClass(testClass);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getTestClass().ifPresent(testClass -> {
            MDC.put(TEST_CLASS_KEY, testClass.getSimpleName());
        });

        context.getTestMethod().ifPresent(testMethod -> {
            MDC.put(TEST_METHOD_KEY, testMethod.getName());
        });

        Object testInstance = context.getTestInstance().get();
        Method method = context.getTestMethod().get();

        MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method);

        Class<TestConfigurer> testConfigurerType = TestConfigurer.class;
        TestConfigurer testConfigurer = ServiceLocatorUtil.INSTANCE
                .getOneOrDefault(testConfigurerType, DefaultTestConfigurer.class);

        Class<MockProvider> mockProviderType = MockProvider.class;
        MockProvider mockProvider = ServiceLocatorUtil.INSTANCE
                .getOneOrDefault(mockProviderType, MockitoMockProvider.class);

        ExtensionContext.Namespace namespace = create(TestifyExtension.class);
        ExtensionContext.Store store = context.getStore(namespace);

        TestSettings testSettings = store.get(TestSettingsProperties.class, TestSettings.class);

        if (testSettings != null) {
            TestRunner testRunner;

            if (testSettings.getTestRunnerClass() == null) {
                testRunner = ServiceLocatorUtil.INSTANCE.getOneWithFilter(
                        TestRunner.class,
                        testSettings.getTestCategory()
                );
            } else {
                testRunner = ServiceLocatorUtil.INSTANCE.getOne(
                        TestRunner.class,
                        testSettings.getTestRunnerClass()
                );
            }

            TestContext testContext = DefaultTestContextBuilder.builder()
                    .testInstance(testInstance)
                    .testDescriptor(testDescriptor)
                    .testCategory(testSettings.getTestCategory())
                    .testMethodDescriptor(methodDescriptor)
                    .testRunner(testRunner)
                    .testConfigurer(testConfigurer)
                    .mockProvider(mockProvider)
                    .properties(SettingUtil.INSTANCE.getSettings())
                    .build();

            store.put(TestContext.class, testContext);

            TestContextHolder.INSTANCE.set(testContext);

            Optional<Field> sutField = testDescriptor.getSutField();

            if (sutField.isPresent()) {
                SutDescriptor sutDescriptor
                        = AnalyzerUtil.INSTANCE.analyzeSutField(sutField.get());
                testContext.addProperty(TestContextProperties.SUT_DESCRIPTOR, sutDescriptor);
            }

            LoggingUtil.INSTANCE.debug(
                    "Starting test runner '{}'",
                    testRunner.getClass().getName()
            );

            testRunner.start(testContext);
        }

    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        TestContextHolder.INSTANCE.command(testContext -> {
            LoggingUtil.INSTANCE.debug("performing cleanup of '{}'",
                    testContext.getName());

            TestRunner testRunner = testContext.getTestRunner();
            testRunner.stop(testContext);
            TestContextHolder.INSTANCE.remove();
        });
    }

}
