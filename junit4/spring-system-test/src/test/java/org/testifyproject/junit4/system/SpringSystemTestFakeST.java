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

import static javax.ws.rs.core.Response.Status.OK;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit4.fixture.web.GreetingServletApplication;
import org.testifyproject.junit4.fixture.web.service.GreetingService;

@Application(GreetingServletApplication.class)
@RunWith(SpringSystemTest.class)
public class SpringSystemTestFakeST {

    @Sut
    WebTarget sut;

    @Fake
    GreetingService greetingService;

    @Test
    public void givenClientInstanceGetGreetingResourceShouldReturn() {
        String greeting = "Hi";
        given(greetingService.getGreeting()).willReturn(greeting);

        Response result = sut.path("/").request().get();

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(result.readEntity(String.class)).isEqualTo(greeting);

        verify(greetingService).getGreeting();
    }

}
