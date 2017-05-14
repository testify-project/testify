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
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.extension.SutReifier;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies the sut class.
 *
 * @author saden
 */
@UnitTest
@Discoverable
public class CreateSutReifier implements SutReifier {

    @Override
    public void reify(TestContext testContext) {

        testContext.getSutDescriptor().ifPresent(sutDescriptor -> {
            Object testInstance = testContext.getTestInstance();

            Object sutValue = sutDescriptor.getValue(testInstance).orElseGet(()
                    -> ReflectionUtil.INSTANCE.newInstance(sutDescriptor.getType())
            );

            sutDescriptor.setValue(testInstance, sutValue);
        });
    }

}
