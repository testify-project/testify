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
 * An interface that defines methods for configuring test attributes.
 *
 * @author saden
 */
public interface TestConfigurer {

    /**
     * Given a configuration object call the appropriate method on the test
     * class annotated with {@link  org.testifyproject.annotation.ConfigHandler}
     * annotation.
     *
     * @param <T> the configuration object type
     * @param testContext the test context
     * @param configuration the configuration object
     * @return the configuration object
     */
    <T> T configure(TestContext testContext, T configuration);

}
