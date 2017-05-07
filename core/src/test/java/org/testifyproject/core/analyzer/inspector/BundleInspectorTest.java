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
package org.testifyproject.core.analyzer.inspector;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Bundle;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;
import org.testifyproject.fixture.common.SutClass;

/**
 *
 * @author saden
 */
public class BundleInspectorTest {

    BundleInspector sut;

    @Before
    public void init() {
        sut = new BundleInspector();
    }

    @Test
    public void givenClassWithoutBundleMetaAnnotationInspectShouldInspectClass() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = SutClass.class;
        Bundle bundle = mock(Bundle.class);

        sut.inspect(testDescriptor, annotatedType, bundle);
    }

    @Test
    public void givenClassWithBundleMetaAnnotationInspectShouldInspectClass() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = AnalyzedTestClass.class;
        Bundle bundle = mock(Bundle.class);

        sut.inspect(testDescriptor, annotatedType, bundle);
    }

}
