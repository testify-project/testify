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
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that test dependency requirements are met.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@SystemTest
@Discoverable
public class DependencyConfigurationVerifier implements ConfigurationVerifier {

    @Override
    public void verify(TestContext testContext) {
        testContext.getDependencies().forEach((key, value) -> {
            try {
                Class.forName(key);
            } catch (ClassNotFoundException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Required {} dependency class '{}' not found in the classpath.",
                        e, value, key
                );
            }
        });
    }

}