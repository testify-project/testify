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

import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Discoverable;

/**
 * An implementation of {@link TestConfigurer} contract.
 *
 * @author saden
 */
@Discoverable
public class DefaultTestConfigurer implements TestConfigurer {

    @Override
    public <T> T configure(TestContext testContext, T configuration) {
        if (configuration == null) {
            return null;
        }

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Class<? extends Object> configurableType = configuration.getClass();

        return testDescriptor.findConfigHandler(configurableType)
                .map(configHandler -> {
                    Object testInstance = testContext.getTestInstance();

                    //if the configuration method returns null its because its return type
                    //is void so we return the configuration parameter passed to the method
                    //otherwise we insure the result is of the same type as the configuration
                    //type and return it instead.
                    return (T) configHandler.getInstance()
                            .map(configInstance -> configHandler.invoke(configInstance,
                                    configuration))
                            .orElseGet(() -> configHandler.invoke(testInstance, configuration))
                            .map(value -> value)
                            .orElse(configuration);
                })
                .orElse(configuration);

    }
}
