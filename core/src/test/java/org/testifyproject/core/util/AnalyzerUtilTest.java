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

import java.lang.reflect.Field;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.CutDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.fixture.analyzer.AnalyzedModule;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;

/**
 *
 * @author saden
 */
public class AnalyzerUtilTest {

    AnalyzerUtil cut;

    @Before
    public void init() {
        cut = new AnalyzerUtil();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullAnalyzeTestClassShouldThrowException() {
        cut.analyzeTestClass(null);
    }

    @Test
    public void givenTestClassAnalyzeTestClassShouldReturnTestDescriptor() {
        TestDescriptor result = cut.analyzeTestClass(AnalyzedTestClass.class);

        assertThat(result).isNotNull();
        assertThat(result.getCutField()).isNotEmpty();
        assertThat(result.getConfigHandlers()).isNotEmpty();
        assertThat(result.getFieldDescriptors())
                .allMatch(p -> p.getName().equals("store")
                && p.getType().isAssignableFrom(Map.class)
                && p.getFake().isPresent());
        assertThat(result.getModules())
                .allMatch(p -> p.value().equals(AnalyzedModule.class));
        assertThat(result.getScans())
                .allMatch(p -> p.value().equals("org.testifyproject.fixture.analyzer"));
        assertThat(result.getRequiresContainers()).hasSize(1);
        assertThat(result.getCollaboratorProvider()).isPresent();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullAnalyzeCutClassShouldThrowException() {
        cut.analyzeCutField(null);
    }

    @Test
    public void givenCutFieldAnalyzeCutClassShouldReturnCutDescriptor() throws NoSuchFieldException {
        Field cutField = AnalyzedTestClass.class.getDeclaredField("cut");
        CutDescriptor result = cut.analyzeCutField(cutField);

        assertThat(result).isNotNull();
        assertThat(result.getParameterDescriptors()).hasSize(1);
        assertThat(result.getFieldDescriptors()).hasSize(1);
    }
}
