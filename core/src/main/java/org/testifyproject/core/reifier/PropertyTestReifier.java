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
import org.testifyproject.apache.commons.jexl3.JexlBuilder;
import org.testifyproject.apache.commons.jexl3.JexlContext;
import org.testifyproject.apache.commons.jexl3.JexlEngine;
import org.testifyproject.apache.commons.jexl3.MapContext;
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies test class property fields.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@SystemTest
@Discoverable
public class PropertyTestReifier implements TestReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        JexlEngine jexlEngine = new JexlBuilder().create();
        JexlContext jexlContext = new MapContext(testContext.getProperties());

        testDescriptor.getFieldDescriptors().parallelStream().forEach(fieldDescriptor -> {
            fieldDescriptor.getProperty().ifPresent(property -> {
                String propertyValue = property.value();
                Object value = testContext.getProperty(propertyValue);

                if (value == null && property.expression()) {
                    value = jexlEngine.createExpression(propertyValue)
                            .evaluate(jexlContext);
                }

                fieldDescriptor.setValue(testInstance, value);
            });

        });
    }
}
