/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.junit5.fixture;

import java.net.URI;

import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.ClientInstanceBuilder;

/**
 * TODO.
 *
 * @author saden
 */
@Discoverable
public class GenericApplicationClientProvider implements ClientProvider<Object, Object, Object> {

    @Override
    public Object configure(TestContext testContext, Application application, URI baseURI) {
        return new Object();
    }

    @Override
    public ClientInstance<Object, Object> create(TestContext testContext,
            Application application, URI baseURI, Object configuration) {
        return new ClientInstanceBuilder<>()
                .client(new Object())
                .build("testClient", application);
    }

    @Override
    public void destroy(ClientInstance<Object, Object> clientInstance) {
    }

    @Override
    public Class<Object> getClientType() {
        return Object.class;
    }

}
