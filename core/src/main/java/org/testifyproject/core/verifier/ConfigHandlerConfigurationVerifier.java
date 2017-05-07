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

import java.util.List;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that the config handler methods are configured correctly.
 *
 * @author saden
 */
@IntegrationTest
@SystemTest
@Discoverable
public class ConfigHandlerConfigurationVerifier implements ConfigurationVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        List<MethodDescriptor> configHandlers = testDescriptor.getConfigHandlers();

        configHandlers.parallelStream().forEach(configHandler -> {
            List<Class> parameterTypes = configHandler.getParameterTypes();
            int size = parameterTypes.size();
            String name = configHandler.getName();
            String declaringClassName = configHandler.getDeclaringClassName();
            Class returnType = configHandler.getReturnType();

            ExceptionUtil.INSTANCE.raise(size != 1,
                    "Configuration Handler method '{}' in class '{}' has {} paramters. "
                    + "Please insure the configuration handler has one and only one paramter.",
                    name, declaringClassName, size);

            Class paramterType = parameterTypes.get(0);

            ExceptionUtil.INSTANCE.raise(!(Void.TYPE.isAssignableFrom(returnType)
                    || returnType.isAssignableFrom(paramterType)),
                    "Configuration Handler method '{}' in class '{}' has return type returns '{}'."
                    + "Please insure the configuration handler returns a void or a super type of '{}'.",
                    name, declaringClassName, returnType.getSimpleName(), paramterType.getSimpleName());
        });
    }

}