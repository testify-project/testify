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
import java.util.regex.Pattern;
import org.testifyproject.apache.commons.jexl3.JexlBuilder;
import org.testifyproject.apache.commons.jexl3.JexlContext;
import org.testifyproject.apache.commons.jexl3.JexlEngine;
import org.testifyproject.apache.commons.jexl3.JexlExpression;
import org.testifyproject.apache.commons.jexl3.MapContext;
import org.testifyproject.apache.commons.jexl3.ObjectContext;

/**
 * A utility class for creating and interpolating expressions.
 *
 * @author saden
 */
public class ExpressionUtil {

    public static final ExpressionUtil INSTANCE;
    private static final Pattern PATTERN = Pattern.compile(".*\\$\\{.*\\}.*");

    static {
        JexlEngine jexlEngine = new JexlBuilder()
                .create();
        INSTANCE = new ExpressionUtil(jexlEngine);
    }

    private final JexlEngine jexlEngine;

    ExpressionUtil(JexlEngine jexlEngine) {
        this.jexlEngine = jexlEngine;
    }

    /**
     * Evaluates the expression with the variables contained in the supplied
     * {@link Map} based context.
     *
     * @param template the template
     * @param context the template context
     * @return the result of evaluating the template
     */
    public String evaluateTemplate(String template, Map context) {
        JexlContext jexlContext = new MapContext(context);
        JexlExpression jexlExpression = jexlEngine.createExpression(createTemplate(template));

        return (String) jexlExpression.evaluate(jexlContext);
    }

    /**
     * Evaluates the template with the variables contained in the supplied
     * object based context.
     *
     * @param template the template
     * @param context the template context
     * @return the result of evaluating the template
     */
    public String evaluateTemplate(String template, Object context) {
        JexlContext jexlContext = new ObjectContext(jexlEngine, context);
        JexlExpression jexlExpression = jexlEngine.createExpression(createTemplate(template));

        return (String) jexlExpression.evaluate(jexlContext);
    }

    /**
     * Evaluates the expression with the variables contained in the supplied
     * {@link Map} based context.
     *
     * @param <T> the evaluated expression return type
     * @param expression the expression
     * @param context the expression context
     * @return the result of evaluating the expression
     */
    public <T> T evaluateExpression(String expression, Map<String, Object> context) {
        JexlContext jexlContext = new MapContext(context);
        JexlExpression jexlExpression = jexlEngine.createExpression(expression);

        return (T) jexlExpression.evaluate(jexlContext);
    }

    /**
     * Evaluates the expression with the variables contained in the supplied
     * object based context.
     *
     * @param <T> the evaluated expression return type
     * @param expression the expression
     * @param context the expression context
     * @return the result of evaluating the expression
     */
    public <T> T evaluateExpression(String expression, Object context) {
        JexlContext jexlContext = new ObjectContext(jexlEngine, context);
        JexlExpression jexlExpression = jexlEngine.createExpression(expression);

        return (T) jexlExpression.evaluate(jexlContext);
    }

    /**
     * Create a template from the given string.
     *
     * @param value the string that will transformed into a template
     * @return the value transformed into a template
     */
    public String createTemplate(String value) {
        return isTemplate(value) ? value : "`" + value + "`";
    }

    /**
     * Determine if the given string is a template.
     *
     * @param value the string being inspected
     * @return true if the string is a template, false otherwise
     */
    public Boolean isTemplate(String value) {
        return value.startsWith("`") && value.endsWith("`");
    }

    /**
     * Determine if the given string has parameters that can be evaluated.
     *
     * @param value the string being inspected
     * @return true if the string is a parameterized, false otherwise
     */
    public Boolean isParameterized(String value) {
        return PATTERN.matcher(value).matches();
    }

}
