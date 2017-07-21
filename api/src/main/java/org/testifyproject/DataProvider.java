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

import java.util.List;

/**
 * A contract that defines methods to load data into a resource.
 *
 * @author saden
 * @param <T> the resource instance type
 */
public interface DataProvider<T> {

    /**
     * Load the given data files into the given resource instance.
     *
     * @param testContext the test context
     * @param dataFiles the data files that will be loaded
     * @param instance the resource instance
     */
    void load(TestContext testContext, List<String> dataFiles, T instance);
}
