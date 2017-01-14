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

import org.testify.asm.ClassReader;
import org.testify.CutDescriptor;
import org.testify.core.analyzer.CutClassAnalyzer;
import org.testify.core.analyzer.CutDescriptorBuilder;
import org.testify.fixture.analyzer.AnalyzedTestClass;
import java.io.IOException;
import java.lang.reflect.Field;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author saden
 */
public class CutClassAnalyzerTest {

    @Test
    public void verifyCutClassAnalysis() throws IOException, NoSuchFieldException {
        Field cutField = AnalyzedTestClass.class.getDeclaredField("cut");
        CutDescriptorBuilder descriptorBuilder = new CutDescriptorBuilder(cutField);
        CutClassAnalyzer cutClassAnalyzer = new CutClassAnalyzer(cutField, descriptorBuilder);

        ClassReader cutReader = new ClassReader(cutField.getType().getName());
        cutReader.accept(cutClassAnalyzer, ClassReader.SKIP_DEBUG);

        CutDescriptor cutDescriptor = descriptorBuilder.build();

        assertThat(cutDescriptor.getParameterDescriptors()).hasSize(1);
        assertThat(cutDescriptor.getFieldDescriptors()).hasSize(1);

    }

}
