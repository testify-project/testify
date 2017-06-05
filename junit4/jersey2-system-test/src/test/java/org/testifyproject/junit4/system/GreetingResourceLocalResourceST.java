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
package org.testifyproject.junit4.system;

import java.sql.Connection;
import java.util.Optional;
import javax.sql.DataSource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Real;
import org.testifyproject.junit4.fixture.resource.TestLocalResourceProvider;
import org.testifyproject.junit4.fixture.web.GreetingApplication;

/**
 *
 * @author saden
 */
@LocalResource(TestLocalResourceProvider.class)
@Application(GreetingApplication.class)
@RunWith(Jersey2SystemTest.class)
public class GreetingResourceLocalResourceST {

    @Real
    LocalResourceInstance<DataSource, Connection> instance;

    @Real
    DataSource resource;

    @Real
    Connection client;

    @Test
    public void givenLocalResourceVerifyInjectionOfLocalResourceAndClient() {
        assertThat(instance).isNotNull();
        assertThat(instance.getFqn()).isEqualTo("test");
        assertThat(resource).isNotNull();
        assertThat(client).isNotNull();

        Instance<DataSource> resourceInstance = instance.getResource();
        assertThat(resourceInstance).isNotNull();
        assertThat(resourceInstance.getValue()).isEqualTo(resource);;

        Optional<Instance<Connection>> foundClient = instance.getClient();
        assertThat(foundClient).isNotEmpty();

        Instance<Connection> clientInstance = foundClient.get();
        assertThat(clientInstance.getValue()).isEqualTo(client);
    }
}
