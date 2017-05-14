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

import org.testifyproject.annotation.Application;
import org.testifyproject.trait.PropertiesReadTrait;
import org.testifyproject.trait.PropertiesWriteTrait;

/**
 * A contract that defines methods for retrieving information about an
 * application.
 *
 * @author saden
 */
public interface ApplicationInstance extends PropertiesReadTrait, PropertiesWriteTrait {

    /**
     * Get the application annotation.
     *
     * @return the application annotation.
     */
    Application getApplication();

    /**
     * Get the test context associated with the application.
     *
     * @return the test context instance.
     */
    TestContext getTestContext();

}
