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

import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.RemoteResourceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.trait.PropertiesReader;

/**
 * TODO.
 *
 * @author saden
 */
@Discoverable
public class InvalidRemoteResourceProvider implements RemoteResourceProvider {

    /**
     * Private constructor is invalid.
     */
    private InvalidRemoteResourceProvider() {
    }

    @Override
    public Object configure(TestContext testContext, RemoteResource remoteResource,
            PropertiesReader configReader) {
        return null;
    }

    @Override
    public RemoteResourceInstance start(TestContext testContext, RemoteResource remoteResource,
            Object config) {
        return null;
    }

    @Override
    public void stop(TestContext testContext, RemoteResource remoteResource,
            RemoteResourceInstance instance) {
    }
}
