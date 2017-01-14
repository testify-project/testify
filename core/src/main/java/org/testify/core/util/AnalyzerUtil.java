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
package org.testify.core.util;

import java.io.IOException;
import java.lang.reflect.Field;
import org.testify.CutDescriptor;
import org.testify.TestDescriptor;
import org.testify.asm.ClassReader;
import org.testify.core.analyzer.CutClassAnalyzer;
import org.testify.core.analyzer.CutDescriptorBuilder;
import org.testify.core.analyzer.TestClassAnalyzer;
import org.testify.core.analyzer.TestDescriptorBuilder;

/**
 * A utility class for analyzing classes.
 *
 * @author saden
 */
public class AnalyzerUtil {

    public static final AnalyzerUtil INSTANCE = new AnalyzerUtil();

    public TestDescriptor analyzeTestClass(Class<?> testClass) {
        try {
            TestDescriptorBuilder testDescriptorBuilder = new TestDescriptorBuilder(testClass);
            TestClassAnalyzer testClassAnalyzer = new TestClassAnalyzer(testClass, testDescriptorBuilder);

            ClassReader testReader = new ClassReader(testClass.getName());
            testReader.accept(testClassAnalyzer, ClassReader.SKIP_DEBUG);

            return testDescriptorBuilder.build();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Analysis of test class '%s' failed.", testClass.getSimpleName()), e);
        }
    }

    public CutDescriptor analyzeCutField(Field cutField) {
        try {
            CutDescriptorBuilder builder = new CutDescriptorBuilder(cutField);
            CutClassAnalyzer cutClassAnalyzer = new CutClassAnalyzer(cutField, builder);

            ClassReader cutReader = new ClassReader(cutField.getType().getName());
            cutReader.accept(cutClassAnalyzer, ClassReader.SKIP_DEBUG);

            return builder.build();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Analysis of class under test '%s' failed.",
                    cutField.getType().getSimpleName()), e);
        }
    }

}
