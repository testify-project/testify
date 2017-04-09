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
package org.testifyproject.junit4.system;

import java.util.HashMap;
import java.util.Map;
import org.junit.runners.model.InitializationError;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestRunner;
import org.testifyproject.core.TestCategory;
import org.testifyproject.junit4.core.TestifyJUnit4TestRunner;
import org.testifyproject.level.system.SystemTestRunner;

/**
 * A JUnit Spring system test runner. This class is the main entry point for
 * running a Spring system tests using {@link org.junit.runner.RunWith}. It
 * provides means of creating your class under test, faking certain
 * collaborators or using real collaborators in the Spring application context.
 *
 * @author saden
 */
public class Jersey2SystemTest extends TestifyJUnit4TestRunner {

    private static final Map<String, String> DEPENDENCIES = new HashMap<>();

    static {
        DEPENDENCIES.put("org.glassfish.jersey.server.ResourceConfig", "Jersey2 Server");
    }

    /**
     * Create a new test runner instance for the class under test.
     *
     * @param testClass the test class type
     *
     * @throws InitializationError thrown if the test class is malformed.
     */
    public Jersey2SystemTest(Class<?> testClass) throws InitializationError {
        super(testClass, TestCategory.Level.SYSTEM);
    }

    @Override
    public Map<String, String> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public StartStrategy getResourceStartStrategy() {
        return StartStrategy.LAZY;
    }

    @Override
    public Class<? extends TestRunner> getTestRunnerClass() {
        return SystemTestRunner.class;
    }

}
