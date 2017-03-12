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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Fake;
import org.testifyproject.junit4.fixture.GreeterApplication;
import org.testifyproject.junit4.fixture.service.GreetingService;

@RunWith(Jersey2SystemTest.class)
@Application(GreeterApplication.class)
public class GreetingResourceWebTargetST {

    @Cut
    WebTarget cut;

    @Fake
    GreetingService greetingService;

    @Test
    public void verifyInjections() {
        //Arrange
        String phrase = "Hello";

        willDoNothing().given(greetingService).save(phrase);

        //Act
        Response result = cut.path("/")
                .request()
                .post(Entity.json(phrase));

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(result.hasEntity()).isFalse();

        verify(greetingService).save(phrase);
    }
}
