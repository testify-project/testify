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

/**
 * An interface that defines methods for verifying test class dependencies,
 * configuration and wiring.
 *
 * @author saden
 */
public interface TestVerifier {

    /**
     * Verify test dependencies are present in the classpath.
     */
    void dependency();

    /**
     * Verify the test is configured correctly.
     */
    void configuration();

    /**
     * Verify the test is wired and initialized correctly.
     */
    void wiring();
}