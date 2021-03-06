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
package org.testifyproject.junit4.fixture.service;

import java.util.Map;

import javax.inject.Inject;

import org.testifyproject.junit4.fixture.common.Greeting;

/**
 *
 * @author saden
 */
public class MapGreeter {

    private final Map<String, Greeting> greetings;

    @Inject
    MapGreeter(Map<String, Greeting> greetings) {
        this.greetings = greetings;
    }

    public Map<String, Greeting> getGreetings() {
        return greetings;
    }

}
