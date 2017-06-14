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

import org.testifyproject.TestContext;

/**
 * A contract that defines a method to perform reification of the test class
 * collaborator fields. Note test class reification is executed in the following
 * order:
 * <ul>
 * <li>{@link InitialReifier} - Perform initial initialization of test class
 * collaborator fields</li>
 * <li>{@link FieldReifier} - Perform standard initialization of test class
 * collaborator fields</li>
 * <li>{@link SutReifier} - Perform initialization of of the system under test
 * (SUT) field</li>
 * <li>{@link FinalReifier} - Perform final initialization of test class
 * fields</li>
 * </ul>
 *
 * @author saden
 */
@FunctionalInterface
public interface FieldReifier {

    /**
     * Reify the test class using the given test context.
     *
     * @param testContext the test context
     */
    void reify(TestContext testContext);

}
