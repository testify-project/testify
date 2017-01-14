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
package org.testify.junit.fixture.web.resource;

import org.testify.junit.fixture.web.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author saden
 */
@RestController
public class GreetingResource {

    private final GreetingService greetingService;

    @Autowired
    GreetingResource(GreetingService service) {
        this.greetingService = service;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String greet() {
        return greetingService.getGreeting();
    }

    public GreetingService getGreetingService() {
        return greetingService;
    }

}
