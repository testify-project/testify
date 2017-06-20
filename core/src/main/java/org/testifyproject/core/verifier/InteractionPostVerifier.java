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
package org.testifyproject.core.verifier;

import java.util.Optional;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 * Verify all interaction between the SUT and its collaborators based on
 * {@link org.testifyproject.annotation.Sut#verify() } annotation value.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class InteractionPostVerifier implements PostVerifier {

    @Override
    public void verify(TestContext testContext) {
        testContext.getSutDescriptor().ifPresent(sutDescriptor -> {
            if (sutDescriptor.getSut().verify()) {
                Object testInstance = testContext.getTestInstance();
                MockProvider mockProvider = testContext.getMockProvider();

                Object[] collaborators = testContext.getTestDescriptor().getFieldDescriptors()
                        .stream()
                        .filter(FieldDescriptor::isMock)
                        .map(p -> p.getValue(testInstance))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(mockProvider::isMock)
                        .toArray(Object[]::new);

                mockProvider.verifyAllInteraction(collaborators);
            }
        });
    }

}
