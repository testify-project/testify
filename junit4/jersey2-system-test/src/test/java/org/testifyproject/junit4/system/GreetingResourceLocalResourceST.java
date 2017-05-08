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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.ClientInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Sut;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.junit4.fixture.GreeterApplication;
import org.testifyproject.junit4.fixture.InMemoryHSQLResource;

/**
 * <p>
 * Test Greeter Resource inside the container from the client perspective using
 * real instance of the Greeting Service and an in-memory HSQL database. This
 * ability comes in handy when you want to test against simple or custom
 * resources quickly.
 * </p>
 * <p>
 * NOTE: This example is demo fodder. If you are writing system tests you should
 * use container based resources and test against production environment. See
 * {@link GreeterResourceRequiresContainerTest}
 * </p>
 *
 * @author saden
 */
@Application(GreeterApplication.class)
@LocalResource(InMemoryHSQLResource.class)
@RunWith(Jersey2SystemTest.class)
public class GreetingResourceLocalResourceST {

    @Sut
    ClientInstance<WebTarget> sut;

    @Test
    public void givenHelloGetShouldReturnHello() {
        //Arrange
        String phrase = "Hello";

        //Act
        Response result = sut.getInstance()
                .path("/")
                .request()
                .post(Entity.json(phrase));

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(result.hasEntity()).isFalse();
    }

}
