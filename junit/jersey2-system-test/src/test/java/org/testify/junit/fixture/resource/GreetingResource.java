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
package org.testify.junit.fixture.resource;

import org.testify.junit.fixture.service.GreetingService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;

/**
 *
 * @author saden
 */
@Path("/")
public class GreetingResource {

    GreetingService greetingService;

    @Inject
    GreetingResource(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response save(String phrase) {
        greetingService.save(phrase);

        return Response.ok().build();
    }
}
