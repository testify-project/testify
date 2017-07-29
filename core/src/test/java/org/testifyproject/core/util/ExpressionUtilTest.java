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

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.apache.commons.jexl3.JexlBuilder;
import org.testifyproject.apache.commons.jexl3.JexlEngine;
import org.testifyproject.apache.commons.jexl3.JexlException;
import org.testifyproject.fixture.common.User;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class ExpressionUtilTest {

    ExpressionUtil sut;
    JexlEngine jexlEngine;

    @Before
    public void init() {
        jexlEngine = new JexlBuilder().create();

        sut = new ExpressionUtil(jexlEngine);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTemplateEvaluateTemplateShouldThrowException() {
        String template = null;
        Map<String, Object> context = mock(Map.class);

        sut.evaluateTemplate(template, context);
    }

    @Test
    public void givenEmptyMapContextEvaluateTemplateShouldReturnValue() {
        String template = "value";
        Map<String, Object> context = ImmutableMap.of();

        String resut = sut.evaluateTemplate(template, context);
        assertThat(resut).isEqualTo(template);
    }

    @Test
    public void givenMapContextWithoutBackQuotesEvaluateTemplateShouldReturn() {
        String template = "${value}";
        String key = "value";
        String value = "test";

        String resut = sut.evaluateTemplate(template, ImmutableMap.of(key, value));
        assertThat(resut).isEqualTo(value);
    }

    @Test
    public void givenMapContextWithBackQuotesTemplateEvaluateTemplateShouldReturn() {
        String template = "`${value}`";
        String key = "value";
        String value = "test";

        String resut = sut.evaluateTemplate(template, ImmutableMap.of(key, value));
        assertThat(resut).isEqualTo(value);
    }

    @Test
    public void givenObjectContextWithoutBackQuotesEvaluateTemplateShouldReturn() {
        String name = "test";
        Integer age = 10;
        User context = new User(name, age);
        String template = "${name} - ${age}";

        String resut = sut.evaluateTemplate(template, context);
        assertThat(resut).contains(name, age.toString());
    }

    @Test
    public void givenObjectContextWithBackQuotesTemplateEvaluateTemplateShouldReturn() {
        String name = "test";
        Integer age = 10;
        User context = new User(name, age);
        String template = "`${name} - ${age}`";

        String resut = sut.evaluateTemplate(template, context);
        assertThat(resut).contains(name, age.toString());
    }

    @Test(expected = NullPointerException.class)
    public void givenNullExpressionEvaluateExpressionShouldThrowException() {
        String expression = null;
        Map<String, Object> context = mock(Map.class);

        sut.evaluateExpression(expression, context);
    }

    @Test(expected = JexlException.class)
    public void givenEmptyMapContextEvaluateExpressionShouldReturnValue() {
        String expression = "value";
        Map<String, Object> context = ImmutableMap.of();

        Object resut = sut.evaluateExpression(expression, context);
        assertThat(resut).isEqualTo(expression);
    }

    @Test
    public void givenMapContextEvaluateExpressionShouldReturn() {
        String expression = "value";
        String key = "value";
        String value = "test";

        String resut = sut.evaluateExpression(expression, ImmutableMap.of(key, value));
        assertThat(resut).isEqualTo(value);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullContextObjectEvaluateExpressionShouldThrowException() {
        User context = null;
        String expression = "age";

        sut.evaluateExpression(expression, context);
    }

    @Test
    public void givenObjectContextEvaluateExpressionShouldReturn() {
        String name = "test";
        Integer age = 10;
        User context = new User(name, age);
        String expression = "age";

        Integer resut = sut.evaluateExpression(expression, context);
        assertThat(resut).isEqualTo(age);
    }

    @Test
    public void givenNonTemplateStringCreateTemplateShouldReturnTemplate() {
        String value = "hello!";

        String result = sut.createTemplate(value);
        assertThat(result).startsWith("`").endsWith("`");
    }

    @Test
    public void givenTemplateStringCreateTemplateShouldReturnTemplateString() {
        String value = "`Hello ${firstName} ${lastName}!`";

        String result = sut.createTemplate(value);
        assertThat(result).isEqualTo(value);
    }

    @Test
    public void givenNonTemplateStringIsTemplateShouldReturnFalse() {
        String value = "hello!";

        Boolean result = sut.isTemplate(value);
        assertThat(result).isFalse();
    }

    @Test
    public void givenTemplateStringIsTemplateShouldReturnTrue() {
        String value = "`Hello ${firstName} ${lastName}!`";

        Boolean result = sut.isTemplate(value);
        assertThat(result).isTrue();
    }

    @Test
    public void givenNonParameterizedStringIsParameterizedShouldReturnFalse() {
        String value = "hello!";

        Boolean result = sut.isParameterized(value);
        assertThat(result).isFalse();
    }

    @Test
    public void givenParameterizedStringIsParameterizedShouldReturnTrue() {
        String value = "Hello ${firstName} ${lastName}!";

        Boolean result = sut.isParameterized(value);
        assertThat(result).isTrue();
    }
}
