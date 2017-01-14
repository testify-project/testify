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
package org.testify.junit.system;

import org.testify.ClientInstance;
import org.testify.annotation.Application;
import org.testify.annotation.Cut;
import org.testify.annotation.Module;
import org.testify.annotation.RequiresContainer;
import org.testify.junit.fixture.GreeterApplication;
import org.testify.junit.fixture.need.PostgresAbstractModule;
import org.testify.tools.category.ContainerTests;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Test Greeter Resource inside the container from the client perspective real
 * instance of the Greeting Service and Postgres database running inside of
 * Docker container. This ability comes in handy when you want to test against
 * real production environment.
 *
 * @author saden
 */
@Application(GreeterApplication.class)
@Module(PostgresAbstractModule.class)
@RequiresContainer(value = "postgres", version = "9.4")
@Category(ContainerTests.class)
@RunWith(Jersey2SystemTest.class)
public class GreeterResourceRequiresContainerST {

    @Cut
    ClientInstance<WebTarget> cut;

    @Test
    public void givenHelloGetShouldReturnHello() {
        //Arrange
        String phrase = "Hello";

        //Act
        Response result = cut.getTarget()
                .path("/")
                .request()
                .post(Entity.json(phrase));

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(result.hasEntity()).isFalse();
    }

}
