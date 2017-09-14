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
package org.testifyproject.junit4.fixture;

import javax.inject.Inject;

import org.glassfish.hk2.api.IterableProvider;
import org.jvnet.hk2.annotations.Service;
import org.testifyproject.junit4.fixture.common.Greeting;

/**
 *
 * @author saden
 */
@Service
public class IterableProviderGreeter {

    private final IterableProvider<Greeting> greetings;

    @Inject
    IterableProviderGreeter(IterableProvider<Greeting> greetings) {
        this.greetings = greetings;
    }

    public String greet() {
        return greetings.get().phrase();
    }

    public IterableProvider<Greeting> getGreetings() {
        return greetings;
    }

}
