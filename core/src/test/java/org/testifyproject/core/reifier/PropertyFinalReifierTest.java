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
package org.testifyproject.core.reifier;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Property;
import org.testifyproject.core.util.ExpressionUtil;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class PropertyFinalReifierTest {

    PropertyFinalReifier sut;
    ExpressionUtil expressionUtil;

    @Before
    public void init() {
        expressionUtil = mock(ExpressionUtil.class);
        sut = new PropertyFinalReifier(expressionUtil);
    }

    @Test
    public void verifyDefaultConstructor() {
        sut = new PropertyFinalReifier();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTestContextReifyShouldThrowException() {
        sut.reify(null);
    }

    @Test
    public void givenTestDescriptorWithoutFieldsReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = mock(Object.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of();

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        sut.reify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getFieldDescriptors();
    }

    @Test
    public void givenTestDescriptorWithoutPropertyFieldReifyShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = mock(Object.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Optional<Property> foundProperty = Optional.empty();

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getProperty()).willReturn(foundProperty);

        sut.reify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).getProperty();
    }

    @Test
    public void givenTestDescriptorWithPropertyFieldAndPropertyReifyShouldSetField() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = mock(Object.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Property property = mock(Property.class);
        Optional<Property> foundProperty = Optional.of(property);
        String propertyValue = "property";
        Object value = new Object();

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getProperty()).willReturn(foundProperty);
        given(property.value()).willReturn(propertyValue);
        given(testContext.getProperty(propertyValue)).willReturn(value);

        sut.reify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getFieldDescriptors();
        verify(property).value();
        verify(fieldDescriptor).getProperty();
        verify(fieldDescriptor).setValue(testInstance, value);
    }

    @Test
    public void givenTestDescriptorWithPropertyFieldAndExpressionReifyShouldSetField() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = mock(Object.class);
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        Property property = mock(Property.class);
        Optional<Property> foundProperty = Optional.of(property);
        String propertyValue = "property";
        String value = "value";
        Map<String, Object> properties = ImmutableMap.of(propertyValue, value);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getProperties()).willReturn(properties);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(fieldDescriptor.getProperty()).willReturn(foundProperty);
        given(property.value()).willReturn(propertyValue);
        given(testContext.getProperty(propertyValue)).willReturn(null);
        given(property.expression()).willReturn(true);
        given(expressionUtil.evaluateExpression(propertyValue, properties)).willReturn(value);

        sut.reify(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testContext).getProperties();
        verify(testDescriptor).getFieldDescriptors();
        verify(property).value();
        verify(fieldDescriptor).getProperty();
        verify(property).expression();
        verify(expressionUtil).evaluateExpression(propertyValue, properties);
        verify(fieldDescriptor).setValue(testInstance, value);
    }

}
