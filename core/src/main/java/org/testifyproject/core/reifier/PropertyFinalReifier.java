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

import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExpressionUtil;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies test class property fields.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class PropertyFinalReifier implements FinalReifier {

    private final ExpressionUtil expressionUtil;

    public PropertyFinalReifier() {
        this(ExpressionUtil.INSTANCE);
    }

    PropertyFinalReifier(ExpressionUtil expressionUtil) {
        this.expressionUtil = expressionUtil;
    }

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        testDescriptor.getFieldDescriptors().parallelStream().forEach(fieldDescriptor -> {
            fieldDescriptor.getProperty().ifPresent(property -> {
                String propertyValue = property.value();
                Object value = testContext.getProperty(propertyValue);

                if (value == null && property.expression()) {
                    value = expressionUtil.evaluateExpression(propertyValue, testContext
                            .getProperties());
                }

                fieldDescriptor.setValue(testInstance, value);
            });

        });
    }
}
