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
package org.testifyproject.junit4;

import org.junit.runners.model.InitializationError;
import org.testifyproject.core.TestCategory;
import org.testifyproject.core.setting.TestSettingsBuilder;
import org.testifyproject.junit4.core.TestifyJUnit4TestRunner;

/**
 * A JUnit generic integration test runner. This class is the main entry point for running a
 * integration tests.
 *
 * @author saden
 */
public class IntegrationTest extends TestifyJUnit4TestRunner {

    /**
     * Create a new test runner instance for the system under test.
     *
     * @param testClass the test class type
     *
     * @throws InitializationError thrown if the test class is malformed.
     */
    public IntegrationTest(Class<?> testClass) throws InitializationError {
        super(testClass, TestSettingsBuilder.builder()
                .level(TestCategory.Level.INTEGRATION)
                .build()
        );
    }

}
