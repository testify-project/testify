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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.RequiresContainer;
import org.testifyproject.core.analyzer.TestDescriptorProperties;

/**
 *
 * @author saden
 */
public class RequiresContainerInspectorTest {

    RequiresContainerInspector cut;

    @Before
    public void init() {
        cut = new RequiresContainerInspector();
    }

    @Test(expected = NullPointerException.class)
    public void callToHandlesNullShouldReturThrowExpcetion() {
        cut.handles(null);
    }

    @Test
    public void callToHandlesRequiresContainerShouldReturnTrue() {
        boolean result = cut.handles(RequiresContainer.class);

        assertThat(result).isTrue();
    }

    @Test
    public void givenParamtersInspectShouldAddProperty() throws Exception {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        RequiresContainer annotation = mock(RequiresContainer.class);

        cut.inspect(testDescriptor, annotatedType, annotation);

        verify(testDescriptor).addListElement(TestDescriptorProperties.REQUIRES_CONTAINERS, annotation);
    }

}
