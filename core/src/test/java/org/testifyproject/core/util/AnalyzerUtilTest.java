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
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.fixture.analyzer.AnalyzedModule;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;

/**
 *
 * @author saden
 */
public class AnalyzerUtilTest {

    AnalyzerUtil sut;

    @Before
    public void init() {
        sut = new AnalyzerUtil();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullAnalyzeTestClassShouldThrowException() {
        sut.analyzeTestClass(null);
    }

    @Test
    public void givenTestClassAnalyzeTestClassShouldReturnTestDescriptor() {
        TestDescriptor result = sut.analyzeTestClass(AnalyzedTestClass.class);

        assertThat(result).isNotNull();
        assertThat(result.getSutField()).isNotEmpty();
        assertThat(result.getConfigHandlers()).isNotEmpty();
        assertThat(result.getFieldDescriptors())
                .allMatch(p -> p.getName().equals("store")
                && p.getType().isAssignableFrom(Map.class)
                && p.getFake().isPresent());
        assertThat(result.getModules())
                .allMatch(p -> p.value().equals(AnalyzedModule.class));
        assertThat(result.getScans())
                .allMatch(p -> p.value().equals("org.testifyproject.fixture.analyzer"));
        assertThat(result.getVirtualResources()).hasSize(1);
        assertThat(result.getCollaboratorProvider()).isPresent();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullAnalyzeSutClassShouldThrowException() {
        sut.analyzeSutField(null);
    }

    @Test
    public void givenSutFieldAnalyzeSutClassShouldReturnSutDescriptor() throws NoSuchFieldException {
        Field sutField = AnalyzedTestClass.class.getDeclaredField("sut");
        SutDescriptor result = sut.analyzeSutField(sutField);

        assertThat(result).isNotNull();
        assertThat(result.getParameterDescriptors()).hasSize(1);
        assertThat(result.getFieldDescriptors()).hasSize(1);
    }
}
