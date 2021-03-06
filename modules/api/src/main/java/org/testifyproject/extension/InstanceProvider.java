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

import java.util.List;

import org.testifyproject.Instance;
import org.testifyproject.TestContext;

/**
 * A contract that defines a method to provider instances that will be added to a
 * {@link org.testifyproject.ServiceInstance}.
 *
 * @author saden
 */
public interface InstanceProvider {

    /**
     * Provide one or more instances.
     *
     * @param testContext the test context
     * @return a list of instances, null otherwise
     */
    List<Instance> get(TestContext testContext);
}
