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
package org.testify.core.analyzer.inspector;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testify.MethodDescriptor;
import org.testify.TestDescriptor;
import org.testify.annotation.CollaboratorProvider;
import org.testify.core.analyzer.TestDescriptorProperties;
import org.testify.fixture.inspector.TestCollaboratorProvider;
import org.testify.fixture.inspector.TestEmptyCollaboratorProvider;

/**
 *
 * @author saden
 */
public class CollaboratorProviderInspectorTest {

    CollaboratorProviderInspector cut;

    @Before
    public void init() {
        cut = new CollaboratorProviderInspector();
    }

    @Test(expected = NullPointerException.class)
    public void callToHandlesNullShouldReturThrowExpcetion() {
        cut.handles(null);
    }

    @Test
    public void callToHandlesModuleShouldReturnTrue() {
        boolean result = cut.handles(CollaboratorProvider.class);

        assertThat(result).isTrue();
    }

    @Test
    public void givenParamtersInspectShouldAddProperty() throws Exception {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        CollaboratorProvider annotation = mock(CollaboratorProvider.class);
        Class providerType = TestCollaboratorProvider.class;

        given(annotation.value()).willReturn(providerType);

        cut.inspect(testDescriptor, annotatedType, annotation);

        verify(testDescriptor).addProperty(eq(TestDescriptorProperties.COLLABORATOR_PROVIDER), any(MethodDescriptor.class));
    }

    @Test
    public void givenParamtersInspectShouldNotAddProperty() throws Exception {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        CollaboratorProvider annotation = mock(CollaboratorProvider.class);
        Class providerType = TestEmptyCollaboratorProvider.class;

        given(annotation.value()).willReturn(providerType);

        cut.inspect(testDescriptor, annotatedType, annotation);

        verifyNoMoreInteractions(testDescriptor);
    }

}
