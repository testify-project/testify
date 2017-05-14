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

import java.lang.reflect.Type;
import java.util.Optional;
import org.testifyproject.SutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies the test class.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@Discoverable
public class DefaultTestReifier implements TestReifier {

    @Override
    public void reify(TestContext testContext) {
        testContext.getSutDescriptor().ifPresent(sutDescriptor -> {
            Object testInstance = testContext.getTestInstance();

            sutDescriptor.getValue(testInstance).ifPresent(sutInstance
                    -> testContext.getTestDescriptor().getFieldDescriptors().stream()
                            .filter(FieldDescriptor::isInjectable)
                            .forEach(testFieldDescriptor
                                    -> processTestField(
                                    testContext,
                                    testFieldDescriptor,
                                    sutDescriptor,
                                    sutInstance)
                            )
            );

        });
    }

    public void processTestField(TestContext testContext,
            FieldDescriptor testFieldDescriptor,
            SutDescriptor sutDescriptor,
            Object sutInstance) {
        MockProvider mockProvider = testContext.getMockProvider();
        Object testInstance = testContext.getTestInstance();
        String testFieldName = testFieldDescriptor.getDefinedName();
        Type testFieldGenericType = testFieldDescriptor.getGenericType();

        Optional<FieldDescriptor> foundMatchingField
                = sutDescriptor.findFieldDescriptor(testFieldGenericType, testFieldName);

        if (!foundMatchingField.isPresent()) {
            foundMatchingField = sutDescriptor.findFieldDescriptor(testFieldGenericType);
        }

        foundMatchingField.ifPresent(sutFieldDescriptor
                -> processSutField(testFieldDescriptor,
                        sutFieldDescriptor,
                        testInstance,
                        sutInstance,
                        mockProvider)
        );
    }

    public void processSutField(FieldDescriptor testFieldDescriptor,
            FieldDescriptor sutFieldDescriptor,
            Object testInstance,
            Object sutInstance,
            MockProvider mockProvider) {
        Class<?> testFieldType = testFieldDescriptor.getType();

        Optional<Object> testFieldValue = testFieldDescriptor.getValue(testInstance);
        Optional<Object> sutFieldValue = sutFieldDescriptor.getValue(sutInstance);
        Object value = null;
        Object sutValue;

        if (sutFieldValue.isPresent()) {
            sutValue = sutFieldValue.get();

            if (testFieldDescriptor.getVirtual().isPresent()) {
                value = mockProvider.createVirtual(testFieldType, sutValue);
            } else if (testFieldDescriptor.getReal().isPresent()) {
                value = sutValue;
            }
        }

        if (value == null && testFieldValue.isPresent()) {
            value = testFieldValue.get();
        }

        sutFieldDescriptor.setValue(sutInstance, value);
        testFieldDescriptor.setValue(testInstance, value);
    }

}
