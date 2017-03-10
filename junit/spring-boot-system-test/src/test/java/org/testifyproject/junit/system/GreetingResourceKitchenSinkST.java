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
package org.testifyproject.junit.system;

import javax.ws.rs.client.WebTarget;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.ClientInstance;
import org.testifyproject.ServerInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Real;
import org.testifyproject.junit.fixture.servlet.GreeterServletApplication;
import org.testifyproject.junit.fixture.web.resource.GreetingResource;
import org.testifyproject.junit.fixture.web.service.GreetingService;

@RunWith(SpringBootSystemTest.class)
@Application(GreeterServletApplication.class)
public class GreetingResourceKitchenSinkST {

    @Cut
    GreetingResource cut;

    @Real
    GreetingService greetingService;

    @Real
    ClientInstance<WebTarget> clientInstance;

    @Real
    ServerInstance serverInstance;

    @Test
    public void verifyInjections() {
        assertThat(cut).isNotNull();

        assertThat(greetingService)
                .isNotNull()
                .isSameAs(cut.getGreetingService());

        assertThat(Mockito.mockingDetails(greetingService).isMock()).isFalse();
        assertThat(clientInstance).isNotNull();
        assertThat(serverInstance).isNotNull();
    }

}
