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
package org.testify.junit.fixture;

import org.testify.junit.core.BaseJUnitTestRunner;
import static java.util.Collections.EMPTY_MAP;
import java.util.Map;
import org.junit.runners.model.InitializationError;

/**
 *
 * @author saden
 */
public class FunctionalTest extends BaseJUnitTestRunner {

    private static final Map<String, String> DEPENDENCIES = EMPTY_MAP;

    /**
     * Create a new test runner instance for the class under test.
     *
     * @param testClass the test class type
     *
     * @throws InitializationError thrown if the test class is malformed.
     */
    public FunctionalTest(Class<?> testClass) throws InitializationError {
        super(testClass, FunctionalTestRunner.class, false, DEPENDENCIES);
    }

}
