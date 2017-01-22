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

/**
 * An interface that defines methods for reifying test and cut class.
 *
 * @author saden
 */
public interface TestReifier {

    /**
     * Given a configuration object call the appropriate method on the test
     * class annotated with {@link  org.testify.annotation.ConfigHandler}
     * annotation.
     *
     * @param <T> the configuration object type
     * @param testContext the test context
     * @param configuration the configuration object
     * @return return the original configuration or a new one in the event the
     * configuration object is immutable.
     */
    <T> T configure(TestContext testContext, T configuration);

    /**
     * Reify the test class from the cut instance.
     *
     * @param testContext the test context
     * @param cutInstance the cut instance
     */
    void reify(TestContext testContext, Object cutInstance);

    /**
     * Reify the test and cut classes from the given collaborators arguments.
     *
     * @param testContext the test context
     * @param cutInstance the cut instance
     * @param collaborators the collaborators
     */
    void reify(TestContext testContext, Object cutInstance, Object... collaborators);
}
