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
package org.testify.analyzer;

import java.io.IOException;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.testify.TestDescriptor;
import org.testify.asm.ClassReader;
import org.testify.core.analyzer.TestClassAnalyzer;
import org.testify.core.analyzer.TestDescriptorBuilder;
import org.testify.fixture.analyzer.AnalyzedModule;
import org.testify.fixture.analyzer.AnalyzedTestClass;

/**
 *
 * @author saden
 */
public class TestClassAnalyzerTest {

    @Test
    public void verifyTestClassAnalysis() throws IOException {
        Class<AnalyzedTestClass> testClass = AnalyzedTestClass.class;

        TestDescriptorBuilder testDescriptorBuilder = new TestDescriptorBuilder(testClass);
        TestClassAnalyzer testClassAnalyzer = new TestClassAnalyzer(testClass, testDescriptorBuilder);

        ClassReader testReader = new ClassReader(testClass.getName());
        testReader.accept(testClassAnalyzer, ClassReader.SKIP_DEBUG);

        TestDescriptor testDescriptor = testDescriptorBuilder.build();

        assertThat(testDescriptor.getCutField()).isNotEmpty();

        assertThat(testDescriptor.getConfigHandlers()).isNotEmpty();

        assertThat(testDescriptor.getFieldDescriptors())
                .allMatch(p -> p.getName().equals("store")
                && p.getType().isAssignableFrom(Map.class)
                && p.getFake().isPresent());

        assertThat(testDescriptor.getModules())
                .allMatch(p -> p.value().equals(AnalyzedModule.class));

        assertThat(testDescriptor.getScans())
                .allMatch(p -> p.value().equals("org.testify.fixture.analyzer"));

        assertThat(testDescriptor.getRequiresContainers()).hasSize(1);

        assertThat(testDescriptor.getCollaboratorProvider()).isPresent();
    }

}
