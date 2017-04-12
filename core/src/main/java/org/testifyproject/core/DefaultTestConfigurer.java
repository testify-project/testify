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
package org.testifyproject.core;

import java.util.Optional;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import static org.testifyproject.guava.common.base.Preconditions.checkState;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of {@link TestConfigurer} contract.
 *
 * @author saden
 */
@Discoverable
public class DefaultTestConfigurer implements TestConfigurer {

    @Override
    public <T> T configure(TestContext testContext, T configuration) {
        Class<? extends Object> configurationType = configuration.getClass();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<MethodDescriptor> foundMatch = testDescriptor.findConfigHandler(configurationType);

        if (foundMatch.isPresent()) {
            MethodDescriptor methodDescriptor = foundMatch.get();
            Class<?> returnType = methodDescriptor.getReturnType();
            Optional<Object> methodInstance = methodDescriptor.getInstance();
            Optional<Object> result;

            if (methodInstance.isPresent()) {
                result = methodDescriptor.invoke(methodInstance.get(), configuration);
            } else {
                result = methodDescriptor.invoke(testInstance, configuration);
            }

            //if the configuration method returns null its because its return type
            //is void so we return the configuration parameter passed to the method
            //otherwise we insure the result is of the same type as the configuration
            //type and return it instead.
            if (!result.isPresent()) {
                checkState(Void.TYPE.isAssignableFrom(returnType),
                        "ConfigHandler method '%s' in class '%s' returns null. "
                        + "Please insure that the method returns a non-null value "
                        + "or its return type is void.",
                        methodDescriptor.getName(), methodDescriptor.getDeclaringClassName()
                );

                return configuration;
            } else if (result.getClass().isAssignableFrom(configurationType)) {
                return (T) result;
            }
        }

        return configuration;
    }

}
