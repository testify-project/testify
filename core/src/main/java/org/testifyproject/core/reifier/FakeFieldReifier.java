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

import java.util.Optional;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies test fields annotated with
 * {@link  org.testifyproject.annotation.Fake}.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@SystemTest
@Discoverable
public class FakeFieldReifier implements FieldReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        MockProvider mockProvider = testContext.getMockProvider();

        testDescriptor.getFieldDescriptors()
                .parallelStream()
                .filter(p -> p.getFake().isPresent())
                .forEach(fieldDescriptor -> {
                    Class<?> fieldType = fieldDescriptor.getType();
                    Optional foundValue = fieldDescriptor.getValue(testInstance);
                    Object value = null;

                    if (!foundValue.isPresent()) {
                        value = mockProvider.createFake(fieldType);
                    } else if (!mockProvider.isMock(foundValue.get())) {
                        value = mockProvider.createVirtual(fieldType, foundValue.get());
                    }

                    fieldDescriptor.setValue(testInstance, value);
                });
    }

}
