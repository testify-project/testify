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
package org.testifyproject.junit5.fixture.resource;

import static org.mockito.Mockito.mock;

import java.sql.Connection;

import javax.sql.DataSource;

import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.core.LocalResourceInstanceBuilder;
import org.testifyproject.trait.PropertiesReader;

/**
 * An implementation of LocalResourceProvider that provides test local resource.
 *
 * @author saden
 */
public class TestLocalResourceProvider implements
        LocalResourceProvider<Void, DataSource, Connection> {

    @Override
    public Void configure(TestContext testContext, LocalResource localResource,
            PropertiesReader configReader) {
        return null;
    }

    @Override
    public LocalResourceInstance<DataSource, Connection> start(TestContext testContext,
            LocalResource localResource,
            Void configuration)
            throws Exception {
        return LocalResourceInstanceBuilder.builder()
                .resource(mock(DataSource.class), DataSource.class)
                .client(mock(Connection.class), Connection.class)
                .build("local.test.resource", localResource);
    }

    @Override
    public void stop(TestContext testContext,
            LocalResource localResource,
            LocalResourceInstance<DataSource, Connection> instance) throws Exception {
    }
}
