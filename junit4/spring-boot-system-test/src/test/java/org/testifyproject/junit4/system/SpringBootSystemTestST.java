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

import java.net.InetAddress;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.ws.rs.client.WebTarget;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.testifyproject.ClientInstance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.ServerInstance;
import org.testifyproject.TestContext;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.annotation.Property;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Sut;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.junit4.fixture.resource.TestLocalResourceProvider;
import org.testifyproject.junit4.fixture.web.GreetingServletApplication;
import org.testifyproject.junit4.fixture.web.service.GreetingService;

@VirtualResource("test")
@LocalResource(TestLocalResourceProvider.class)
@Application(GreetingServletApplication.class)
@RunWith(SpringBootSystemTest.class)
public class SpringBootSystemTestST {

    @Sut
    WebTarget sut;

    @Real
    GreetingService greetingService;

    @Real
    ClientInstance<WebTarget> clientInstance;

    @Real
    TestContext testContext;

    @Real
    ServerInstance<EmbeddedServletContainer> serverInstance;

    @Real
    EmbeddedServletContainer container;

    @Real
    VirtualResourceInstance<InetAddress> virtualResourceInstance;

    @Real
    InetAddress virtualResource;

    @Qualifier("resource:/virtual.test.resource/resource")
    @Real
    InetAddress qualifiedVirtualResource;

    @Real
    LocalResourceInstance<DataSource, Connection> localResourceInstance;

    @Real
    DataSource localResource;

    @Named("resource:/local.test.resource/resource")
    @Real
    DataSource namedLocalResource;

    @Real
    Connection localClient;

    @Resource(name = "resource:/local.test.resource/client")
    @Real
    Connection resourceLocalClient;

    @Property("springboot")
    Map<String, Object> properties;

    @Property("springboot.app")
    Object application;

    @Property("springboot.app.sources")
    Set<Object> sources;

    @Test
    public void verifyInjections() {
        assertThat(sut).as("injection of application client as the SUT").isNotNull();
        assertThat(clientInstance).as("injection of application ClientInstance").isNotNull();
        assertThat(greetingService).as("injection of an application service").isNotNull();
        assertThat(testContext).as("injection of TestContext").isNotNull();
        assertThat(serverInstance).as("injection of ServerInstance").isNotNull();
        assertThat(container).as("injection of application server instance").isNotNull();

        assertThat(virtualResourceInstance).as("injection of VirtualResourceInstance").isNotNull();
        assertThat(virtualResource).as("injection of Virtual Resource resource").isNotNull();
        assertThat(qualifiedVirtualResource).as("injection of Virtual Resource resource by @Qualifier").isNotNull();

        assertThat(localResourceInstance).as("injection of LocalResourceInstance").isNotNull();
        assertThat(localResource).as("injection of Local Resource resource").isNotNull();
        assertThat(namedLocalResource).as("injection of Local Resource resource by @Named").isNotNull();
        assertThat(localClient).as("injection of Local Resource client").isNotNull();
        assertThat(resourceLocalClient).as("injection of Local Resource client by @Resource").isNotNull();

        assertThat(properties).as("injection of application server properties map").isNotEmpty();
        assertThat(application).as("injection of application server property by expression").isNotNull();
        assertThat(sources).as("injection of application server property by sub-expression").isNotNull();
    }

}
