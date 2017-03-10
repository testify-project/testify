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
package org.testifyproject.junit.fixture;

import static java.util.Collections.EMPTY_MAP;
import java.util.Map;
import org.junit.runners.model.InitializationError;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestCategory;
import org.testifyproject.TestRunner;
import org.testifyproject.junit.core.TestifyJUnit4TestRunner;

/**
 *
 * @author saden
 */
public class FunctionalTest extends TestifyJUnit4TestRunner {

    private static final Map<String, String> DEPENDENCIES = EMPTY_MAP;

    /**
     * Create a new test runner instance for the class under test.
     *
     * @param testClass the test class type
     *
     * @throws InitializationError thrown if the test class is malformed.
     */
    public FunctionalTest(Class<?> testClass) throws InitializationError {
        super(testClass, TestCategory.Level.Integration);
    }

    @Override
    protected Map<String, String> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    protected StartStrategy getResourceStartStrategy() {
        return StartStrategy.Lazy;
    }

    @Override
    protected Class<? extends TestRunner> getTestRunnerClass() {
        return FunctionalTestRunner.class;
    }

}
