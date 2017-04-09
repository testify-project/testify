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

import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.tools.Discoverable;

/**
 * Insure local resource providers have default constructors.
 *
 * @author saden
 */
@SystemTest
@IntegrationTest
@Discoverable
public class LocalResourceConfigurationVerifier implements ConfigurationVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        testDescriptor.getLocalResources()
                .parallelStream()
                .map(LocalResource::value)
                .forEach(p -> {
                    try {
                        p.getDeclaredConstructor();
                    } catch (NoSuchMethodException e) {
                        ExceptionUtil.INSTANCE.raise(
                                "Local Resource '{}' defined in test class '{}' does not have a zero "
                                + "argument default constructor. Please insure that the local resource "
                                + "provider defines an accessible zero argument default constructor.",
                                testClassName, p.getSimpleName()
                        );
                    }
                });
    }

}
