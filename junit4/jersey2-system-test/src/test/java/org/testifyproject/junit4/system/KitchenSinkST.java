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

import java.util.Map;
import javax.ws.rs.client.WebTarget;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.ClientInstance;
import org.testifyproject.ServerInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Property;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit4.fixture.web.GreetingApplication;
import org.testifyproject.junit4.fixture.web.service.GreetingService;

/**
 *
 * @author saden
 */
@Application(GreetingApplication.class)
@RunWith(Jersey2SystemTest.class)
public class KitchenSinkST {

    @Sut
    WebTarget sut;

    @Real
    GreetingService greetingService;

    @Real
    ClientInstance<WebTarget> clientInstance;

    @Real
    TestContext testContext;

    @Real
    ServerInstance<HttpServer> serverInstance;

    @Real
    HttpServer httpServer;

    @Property("jersey")
    Map<String, Object> properties;
    
    @Property("jersey.app")
    GreetingApplication application;

    @Property("jersey.app.applicationName")
    String applicationName;

    @Test
    public void verifyInjections() {
        assertThat(sut).isNotNull();
        assertThat(clientInstance).isNotNull();
        assertThat(greetingService).isNotNull();
        assertThat(testContext).isNotNull();
        assertThat(serverInstance).isNotNull();
        assertThat(httpServer).isNotNull();
        assertThat(application).isNotNull();
        assertThat(applicationName).isNotNull();
        assertThat(properties).isNotEmpty();
    }
}
