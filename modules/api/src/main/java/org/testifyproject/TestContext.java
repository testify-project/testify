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
package org.testifyproject;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

import org.testifyproject.trait.PropertiesReader;
import org.testifyproject.trait.PropertiesWriter;

/**
 * A small context class that contains reference to the test testInstance, the test
 * testDescriptor, and helper methods.
 *
 * @author saden
 */
public interface TestContext extends PropertiesReader, PropertiesWriter {

    /**
     * Get a unique name to identify the test context.
     *
     * @return a unique test context name
     */
    String getName();

    /**
     * Get the simple name of the test class.
     *
     * @return the test class simple name
     */
    String getTestName();

    /**
     * Get the name of the test method associated with the test context.
     *
     * @return test method name
     */
    String getMethodName();

    /**
     * Get the test method descriptor associated with the test context.
     *
     * @return test method descriptor
     */
    MethodDescriptor getTestMethodDescriptor();

    /**
     * Get test class associated with the test context.
     *
     * @return the test class instance
     */
    Class<?> getTestClass();

    /**
     * The class loader associated with the test class.
     *
     * @return the test class classloader
     */
    ClassLoader getTestClassLoader();

    /**
     * Get the test class descriptor.
     *
     * @return the test class descriptor.
     */
    TestDescriptor getTestDescriptor();

    /**
     * Get the system under test descriptor.
     *
     * @return an optional with sut descriptor, empty optional otherwise
     */
    Optional<SutDescriptor> getSutDescriptor();

    /**
     * Get the test class instance.
     *
     * @return an instance of the test class.
     */
    Object getTestInstance();

    /**
     * get the sut class instance.
     *
     * @param <T> sut instance type
     * @return an instance of the sut class.
     */
    <T> Optional<T> getSutInstance();

    /**
     * Get the test runner associated with the test context.
     *
     * @return test runner instance
     */
    TestRunner getTestRunner();

    /**
     * Get the test configurer associated with the test context.
     *
     * @return test configurer instance
     */
    TestConfigurer getTestConfigurer();

    /**
     * Get the mock provider associated with the test context.
     *
     * @return mock provider instance
     */
    MockProvider getMockProvider();

    /**
     * Get the service instance associated with the test context.
     *
     * @return an optional with service instance, empty optional otherwise
     */
    Optional<ServiceInstance> getServiceInstance();

    /**
     * Get the test category associated with the test. Note that the annotation returned will be
     * {@code UnitCategory, IntegrationCategory, or SystemCategory}.
     *
     * @return the test category annotation
     */
    Class<? extends Annotation> getTestCategory();

    /**
     * Get the local resource instances associated with the test.
     *
     * @return a collection of local resource instances, empty list otherwise
     */
    Collection<LocalResourceInfo> getLocalResources();

    /**
     * Get the virtual resource instances associated with the test.
     *
     * @return a collection of virtual resource instances, empty list otherwise
     */
    Collection<VirtualResourceInfo> getVirtualResources();

    /**
     * Get the remote resource instances associated with the test.
     *
     * @return a collection of remote resource instances, empty list otherwise
     */
    Collection<RemoteResourceInfo> getRemoteResources();

    /**
     * Add an error message to the test context.
     *
     * @param messageFormat the message format
     * @param args message format arguments
     * @return this object
     */
    TestContext addError(String messageFormat, Object... args);

    /**
     * Add an error message to the test context if the given condition is true.
     *
     * @param condition the condition that will used to determine if the condition is added
     * @param messageFormat the message format
     * @param args message format arguments
     * @return this object
     */
    TestContext addError(Boolean condition, String messageFormat, Object... args);

    /**
     * Get the error messages associated with the test context.
     *
     * @return a list of error messages, empty list otherwise.
     */
    Collection<String> getErrors();

    /**
     * Add an warning message to the test context.
     *
     * @param messageFormat the message format
     * @param args message format arguments
     * @return this object
     */
    TestContext addWarning(String messageFormat, Object... args);

    /**
     * Add an warning message to the test context if the given condition is true.
     *
     * @param condition the condition that will used to determine if the condition is added
     * @param messageFormat the message format
     * @param args message format arguments
     * @return this object
     */
    TestContext addWarning(Boolean condition, String messageFormat, Object... args);

    /**
     * Get the warning messages associated with the test context.
     *
     * @return a list of warning messages, empty list otherwise.
     */
    Collection<String> getWarnings();

    /**
     * Verify the integrity test context. This method checks to see if the test has any errors
     * or warning. If errors are found they are reported and the test is terminated. If warnings
     * are found they are reported and the test continues to execute.
     */
    void verify();

}
