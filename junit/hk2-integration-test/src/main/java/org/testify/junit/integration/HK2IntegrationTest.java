/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.junit.integration;

import org.testify.level.integration.IntegrationTestRunner;
import org.testify.junit.core.BaseJUnitTestRunner;
import java.util.HashMap;
import java.util.Map;
import org.junit.runners.model.InitializationError;

/**
 * A JUnit HK2 integration test class runner. This class is the main entry point
 * for running Spring integration test using {@link org.junit.runner.RunWith}.
 * It provides means of creating your class under test, faking certain
 * collaborators or using real collaborators in the Spring application context.
 *
 * @author saden
 */
public class HK2IntegrationTest extends BaseJUnitTestRunner {

    public static final Map<String, String> DEPENDENCIES = new HashMap<>();

    static {
        DEPENDENCIES.put("org.glassfish.hk2.api.ServiceLocator", "HK2");
    }

    /**
     * Create a new test runner instance for the class under test.
     *
     * @param testClass the test class type
     *
     * @throws InitializationError thrown if the test class is malformed.
     */
    public HK2IntegrationTest(Class<?> testClass) throws InitializationError {
        super(testClass, IntegrationTestRunner.class, true, DEPENDENCIES);
    }

}
