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
package org.testifyproject.core.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.asm.ClassReader;
import org.testifyproject.core.analyzer.DefaultSutDescriptor;
import org.testifyproject.core.analyzer.DefaultTestDescriptor;
import org.testifyproject.core.analyzer.SutClassAnalyzer;
import org.testifyproject.core.analyzer.TestClassAnalyzer;

/**
 * A utility class for analyzing classes.
 *
 * @author saden
 */
public class AnalyzerUtil {

    private static final Map<Class, TestDescriptor> TEST_DESCRIPTORS = new ConcurrentHashMap<>();
    private static final Map<Field, SutDescriptor> SUT_DESCRIPTORS = new ConcurrentHashMap<>();

    public static final AnalyzerUtil INSTANCE = new AnalyzerUtil();

    public TestDescriptor analyzeTestClass(Class<?> testClass) {
        return TEST_DESCRIPTORS.computeIfAbsent(testClass, p -> {
            try {

                TestDescriptor testDescriptor = DefaultTestDescriptor.of(testClass);
                TestClassAnalyzer testClassAnalyzer = new TestClassAnalyzer(testClass, testDescriptor);

                ClassReader testReader = new ClassReader(testClass.getName());
                testReader.accept(testClassAnalyzer, ClassReader.SKIP_DEBUG);

                return testDescriptor;
            } catch (IOException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Analysis of test class '{}' failed.",
                        e,
                        testClass.getSimpleName()
                );
            }
        });

    }

    public SutDescriptor analyzeSutField(Field field) {
        return SUT_DESCRIPTORS.computeIfAbsent(field, p -> {
            try {
                SutDescriptor sutDescriptor = DefaultSutDescriptor.of(field);
                SutClassAnalyzer sutClassAnalyzer = new SutClassAnalyzer(field, sutDescriptor);

                ClassReader sutReader = new ClassReader(field.getType().getName());
                sutReader.accept(sutClassAnalyzer, ClassReader.SKIP_DEBUG);

                return sutDescriptor;
            } catch (IOException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Analysis of system under test '{}' failed.",
                        e,
                        field.getType().getSimpleName()
                );
            }
        });
    }

}
