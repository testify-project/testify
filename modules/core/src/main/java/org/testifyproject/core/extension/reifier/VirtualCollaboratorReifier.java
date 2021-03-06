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
package org.testifyproject.core.extension.reifier;

import java.util.Optional;

import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 * A class that reifies test fields annotated with
 * {@link  org.testifyproject.annotation.Virtual}.
 *
 * @author saden
 */
@UnitCategory
@Discoverable
public class VirtualCollaboratorReifier implements CollaboratorReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        MockProvider mockProvider = testContext.getMockProvider();

        testDescriptor.getFieldDescriptors().parallelStream()
                .filter(p -> p.getVirtual().isPresent())
                .forEach(fieldDescriptor -> {
                    Class<?> fieldType = fieldDescriptor.getType();
                    Object fieldValue = null;

                    Optional<Object> foundFieldValue = fieldDescriptor.getValue(testInstance);

                    if (foundFieldValue.isPresent()) {
                        fieldValue = foundFieldValue.get();
                    } else if (!fieldType.isInterface()) {
                        fieldValue = ReflectionUtil.INSTANCE.newInstance(fieldType);
                    }

                    if (fieldValue != null && !mockProvider.isMock(fieldValue)) {
                        fieldValue = mockProvider.createVirtual(fieldType, fieldValue);
                    }

                    fieldDescriptor.setValue(testInstance, fieldValue);
                });
    }

}
