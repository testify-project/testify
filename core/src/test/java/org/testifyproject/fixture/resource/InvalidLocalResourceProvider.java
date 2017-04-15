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
package org.testifyproject.fixture.resource;

import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.tools.Discoverable;

/**
 *
 * @author saden
 */
@Discoverable
public class InvalidLocalResourceProvider implements LocalResourceProvider {

    /**
     * Private constructor is invalid.
     */
    private InvalidLocalResourceProvider() {
    }

    @Override
    public Object configure(TestContext testContext) {
        return null;
    }

    @Override
    public LocalResourceInstance start(TestContext testContext, Object config) {
        return null;
    }

    @Override
    public void stop() {
    }
}
