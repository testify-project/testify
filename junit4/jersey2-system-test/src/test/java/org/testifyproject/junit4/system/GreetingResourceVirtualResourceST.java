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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.ClientInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.ConfigHandler;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Sut;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.junit4.fixture.GreeterApplication;
import org.testifyproject.junit4.fixture.need.PostgresModule;
import org.testifyproject.spotify.docker.client.DefaultDockerClient;

/**
 * Test Greeter Resource inside the container from the client perspective real
 * instance of the Greeting Service and Postgres database running inside of
 * Docker container. This ability comes in handy when you want to test against
 * real production environment.
 *
 * @author saden
 */
@Ignore
@Application(GreeterApplication.class)
@Module(PostgresModule.class)
@VirtualResource(value = "postgres", version = "9.4")
@RunWith(Jersey2SystemTest.class)
public class GreetingResourceVirtualResourceST {

    @Sut
    ClientInstance<WebTarget> sut;

    @ConfigHandler
    public void configure(DefaultDockerClient.Builder builder) {
        assertThat(builder).isNotNull();
    }
    
    @Test
    public void givenHelloGetShouldReturnHello() {
        //Arrange
        String phrase = "Hello";

        //Act
        Response result = sut.getValue()
                .path("/")
                .request()
                .post(Entity.json(phrase));

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(result.hasEntity()).isFalse();
    }

}
