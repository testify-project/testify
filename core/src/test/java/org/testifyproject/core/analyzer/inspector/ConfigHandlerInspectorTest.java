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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.ConfigHandler;
import org.testifyproject.core.analyzer.TestDescriptorProperties;
import org.testifyproject.fixture.inspector.TestConfigHandler;
import org.testifyproject.fixture.inspector.TestEmptyConfigHandler;

/**
 *
 * @author saden
 */
public class ConfigHandlerInspectorTest {

    ConfigHandlerInspector sut;

    @Before
    public void init() {
        sut = new ConfigHandlerInspector();
    }

    @Test(expected = TestifyException.class)
    public void givenEmptyConfigHandlerValueInspectShouldThrowException() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        ConfigHandler annotation = mock(ConfigHandler.class);
        Class[] handlers = {};

        given(annotation.value()).willReturn(handlers);

        sut.inspect(testDescriptor, annotatedType, annotation);
    }

    @Test
    public void givenParamtersInspectShouldAddProperty() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        ConfigHandler annotation = mock(ConfigHandler.class);
        Class[] handlers = {TestConfigHandler.class};

        given(annotation.value()).willReturn(handlers);

        sut.inspect(testDescriptor, annotatedType, annotation);

        verify(testDescriptor).addListElement(eq(TestDescriptorProperties.CONFIG_HANDLERS), any(MethodDescriptor.class));
        verify(testDescriptor).addProperty(TestDescriptorProperties.CONFIG_HANDLER, annotation);
        verifyNoMoreInteractions(testDescriptor);
    }

    @Test
    public void givenParamtersInspectShouldNotAddProperty() {
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Class<?> annotatedType = Object.class;
        ConfigHandler annotation = mock(ConfigHandler.class);
        Class[] handlers = {TestEmptyConfigHandler.class};

        given(annotation.value()).willReturn(handlers);

        sut.inspect(testDescriptor, annotatedType, annotation);

        verify(testDescriptor).addProperty(TestDescriptorProperties.CONFIG_HANDLER, annotation);
        verifyNoMoreInteractions(testDescriptor);
    }

}
