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
package org.testify;

import java.util.Map;
import java.util.Optional;
import org.testify.trait.LoggingTrait;
import org.testify.trait.PropertiesTrait;

/**
 * A small context class that contains reference to the test testInstance, the
 * test testDescriptor, and helper methods.
 *
 * @author saden
 */
public interface TestContext extends LoggingTrait, PropertiesTrait {

    /**
     * Get a unique name to identify the test context.
     *
     * @return a unique test context name
     */
    String getName();

    /**
     * The simple name of the test class.
     *
     * @return the test class simple name.
     */
    String getClassName();

    /**
     * The name of the test method associated with the test context.
     *
     * @return test method name
     */
    String getMethodName();

    /**
     * Get test class associated with the test context.
     *
     * @return the test class instance.
     */
    Class<?> getTestClass();

    /**
     * The test class descriptor.
     *
     * @return the test class descriptor.
     */
    TestDescriptor getTestDescriptor();

    /**
     * Get the class under test descriptor.
     *
     * @return an optional with cut descriptor, empty optional otherwise
     */
    Optional<CutDescriptor> getCutDescriptor();

    /**
     * Test class instance.
     *
     * @return an instance of the test class.
     */
    Object getTestInstance();

    /**
     * The test reifier associated with the test context.
     *
     * @return test refier instance.
     */
    TestReifier getTestReifier();

    /**
     * Get dependencies required to run the tests. The fully qualified name of
     * the class required in the classpath is the key and human readable
     * description is the value.
     *
     * @return a map that contains required dependencies.
     */
    Map<String, String> getDependencies();

    /**
     * Determine whether test resources such as required resources or container
     * resources should be eagerly started. Note that during integration tests
     * required resources and containers can be started right before the test
     * case is executed but in system tests the start of resources and
     * containers must be delayed until the application server is started and
     * running.
     *
     * @return true if the resources should be started, false otherwise.
     */
    Boolean getStartResources();

}
