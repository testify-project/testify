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

import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.extension.CollaboratorReifier;

/**
 * A class that reifies test fields annotated with
 * {@link  org.testifyproject.annotation.Fake}.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class FakeCollaboratorReifier implements CollaboratorReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        MockProvider mockProvider = testContext.getMockProvider();

        testDescriptor.getFieldDescriptors().parallelStream()
                .filter(p -> p.getFake().isPresent())
                .forEach(fieldDescriptor -> {
                    Class<?> fieldType = fieldDescriptor.getType();

                    Object fieldValue = fieldDescriptor.getValue(testInstance)
                            .map(value -> {
                                if (mockProvider.isMock(value)) {
                                    return value;
                                }

                                return mockProvider.createVirtual(fieldType, value);
                            })
                            .orElseGet(() -> mockProvider.createFake(fieldType));

                    fieldDescriptor.setValue(testInstance, fieldValue);
                });
    }

}
