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
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies test classes with virtual sut.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@SystemTest
@Discoverable
public class SutTestReifier implements TestReifier {

    @Override
    public void reify(TestContext testContext) {
        testContext.getSutDescriptor().ifPresent(sutDescriptor -> {
            Object testInstance = testContext.getTestInstance();

            sutDescriptor.getValue(testInstance).ifPresent(sutInstance -> {
                //If the sut is a virtual sut then create a virtual instance
                //of the sut class and set it to the sut field
                if (sutDescriptor.isVirtualSut()) {
                    sutInstance = testContext.getMockProvider()
                            .createVirtual(sutDescriptor.getType(), sutInstance);
                }

                sutDescriptor.setValue(testInstance, sutInstance);
                sutDescriptor.init(sutInstance);
                testContext.addProperty(TestContextProperties.SUT_INSTANCE, sutInstance);
            });
        });
    }
}
