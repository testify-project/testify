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
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 * A class that reifies test classes with virtual sut.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class SutFinalReifier implements FinalReifier {

    @Override
    public void reify(TestContext testContext) {
        testContext.getSutDescriptor().ifPresent(sutDescriptor -> {
            Object testInstance = testContext.getTestInstance();

            sutDescriptor.getValue(testInstance).ifPresent(sutValue -> {
                //If the sut is a virtual sut then create a virtual instance
                //of the sut class and set it to the sut field
                if (sutDescriptor.isVirtualSut()) {
                    sutValue = testContext.getMockProvider()
                            .createVirtualSut(sutDescriptor.getType(), sutValue);
                    sutDescriptor.setValue(testInstance, sutValue);
                }

                sutDescriptor.init(testInstance);
                testContext.addProperty(TestContextProperties.SUT_INSTANCE, sutValue);
            });
        });
    }
}
