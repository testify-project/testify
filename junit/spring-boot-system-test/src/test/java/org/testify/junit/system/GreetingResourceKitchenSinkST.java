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
import org.testify.ServerInstance;
import org.testify.annotation.Application;
import org.testify.annotation.Cut;
import org.testify.annotation.Real;
import org.testify.junit.fixture.servlet.GreeterServletApplication;
import org.testify.junit.fixture.web.resource.GreetingResource;
import org.testify.junit.fixture.web.service.GreetingService;
import javax.ws.rs.client.WebTarget;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

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
