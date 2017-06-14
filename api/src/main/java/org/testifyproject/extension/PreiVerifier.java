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
 * A contract that defines a method to verify that the test class is initialized
 * correctly before it is executed. Note test class verification is executed in
 * the following order:
 * <ul>
 * <li>{@link PreVerifier} - Verify test class is configured correctly before is
 * executed</li>
 * <li>{@link PreiVerifier} - Verify test class is initialized correctly before
 * is executed</li>
 * <li>{@link PostVerifier} - Verify test class produces the correct result
 * after it is executed</li>
 * </ul>
 *
 * @author saden
 */
@FunctionalInterface
public interface PreiVerifier {

    /**
     * Verify the test class using the given test context.
     *
     * @param testContext the test context
     */
    void verify(TestContext testContext);
}
