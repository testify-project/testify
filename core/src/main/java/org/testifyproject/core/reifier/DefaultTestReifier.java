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
import org.testifyproject.CutDescriptor;
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
        testContext.getCutDescriptor().ifPresent(cutDescriptor -> {
            Object testInstance = testContext.getTestInstance();

            cutDescriptor.getValue(testInstance).ifPresent(cutInstance
                    -> testContext.getTestDescriptor().getFieldDescriptors().stream()
                            .filter(FieldDescriptor::isInjectable)
                            .forEach(testFieldDescriptor
                                    -> processTestField(
                                    testContext,
                                    testFieldDescriptor,
                                    cutDescriptor,
                                    cutInstance)
                            )
            );

        });
    }

    void processTestField(TestContext testContext,
            FieldDescriptor testFieldDescriptor,
            CutDescriptor cutDescriptor,
            Object cutInstance) {
        MockProvider mockProvider = testContext.getMockProvider();
        Object testInstance = testContext.getTestInstance();
        String testFieldName = testFieldDescriptor.getDefinedName();
        Type testFieldGenericType = testFieldDescriptor.getGenericType();

        Optional<FieldDescriptor> foundCutMatch
                = cutDescriptor.findFieldDescriptor(testFieldGenericType, testFieldName);

        if (!foundCutMatch.isPresent()) {
            foundCutMatch = cutDescriptor.findFieldDescriptor(testFieldGenericType);
        }

        foundCutMatch.ifPresent(cutFieldDescriptor
                -> processCutField(testFieldDescriptor,
                        cutFieldDescriptor,
                        testInstance,
                        cutInstance,
                        mockProvider)
        );
    }

    void processCutField(FieldDescriptor testFieldDescriptor,
            FieldDescriptor cutFieldDescriptor,
            Object testInstance,
            Object cutInstance,
            MockProvider mockProvider) {
        Class<?> testFieldType = testFieldDescriptor.getType();

        Optional<Object> testFieldValue = testFieldDescriptor.getValue(testInstance);
        Optional<Object> cutFieldValue = cutFieldDescriptor.getValue(cutInstance);
        Object value = null;
        Object cutValue;

        if (cutFieldValue.isPresent()) {
            cutValue = cutFieldValue.get();

            if (testFieldDescriptor.getVirtual().isPresent()) {
                value = mockProvider.createVirtual(testFieldType, cutValue);
            } else if (testFieldDescriptor.getReal().isPresent()) {
                value = cutValue;
            }
        }

        if (value == null && testFieldValue.isPresent()) {
            value = testFieldValue.get();
        }

        cutFieldDescriptor.setValue(cutInstance, value);
        testFieldDescriptor.setValue(testInstance, value);
    }

}
