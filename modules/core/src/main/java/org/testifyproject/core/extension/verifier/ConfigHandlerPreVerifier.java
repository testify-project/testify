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
package org.testifyproject.core.extension.verifier;

import java.util.List;

import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.Lenient;
import org.testifyproject.extension.annotation.Loose;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * Insure that the config handler methods are configured correctly.
 *
 * @author saden
 */
@Strict
@Lenient
@Loose
@IntegrationCategory
@SystemCategory
@Discoverable
public class ConfigHandlerPreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        testDescriptor.getConfigHandlers().parallelStream().forEach(configHandler -> {
            List<Class> parameterTypes = configHandler.getParameterTypes();
            int size = parameterTypes.size();
            String name = configHandler.getName();
            String declaringClassName = configHandler.getDeclaringClassName();
            Class returnType = configHandler.getReturnType();

            testContext.addError(size != 1,
                    "Configuration Handler method '{}' in class '{}' has {} paramters. "
                    + "Please insure the configuration handler has one and only one paramter.",
                    name, declaringClassName, size);

            if (size != 0) {
                Class paramterType = parameterTypes.get(0);
                boolean condition =
                        !(void.class.equals(returnType)
                        || Void.class.equals(returnType)
                        || returnType.isAssignableFrom(paramterType));

                testContext.addError(condition,
                        "Configuration Handler method '{}' in class '{}' has return type "
                        + "returns '{}'. Please insure the configuration handler returns a "
                        + "void or a super type of '{}'.",
                        name, declaringClassName, returnType.getSimpleName(), paramterType
                        .getSimpleName());
            }
        });
    }

}
