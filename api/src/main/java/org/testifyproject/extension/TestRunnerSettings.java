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
package org.testifyproject.extension;

import java.util.Map;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestRunner;

/**
 * A contract that specifies configurations that must be provided to run tests.
 *
 * @author saden
 */
public interface TestRunnerSettings {

    /**
     * Get the dependencies required to run the tests. Note that the key
     * represent the name of the dependency and the value the fully qualified
     * class name that will be searched for in the classpath.
     *
     * @return the test dependencies, empty map otherwise
     */
    Map<String, String> getDependencies();

    /**
     * Get the test resource start strategy which indicates when test resources
     * are started.
     *
     * @return the test resource strategy
     */
    StartStrategy getResourceStartStrategy();

    /**
     * Get the test runner used to run the tests.
     *
     * @return the test runner
     */
    Class<? extends TestRunner> getTestRunnerClass();
}
