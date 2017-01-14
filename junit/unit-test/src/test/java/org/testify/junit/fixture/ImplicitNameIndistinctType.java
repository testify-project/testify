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
package org.testify.junit.fixture;

import org.testify.junit.fixture.collaborator.Hello;

/**
 *
 * @author saden
 */
public class ImplicitNameIndistinctType {

    private final Hello english;
    private final Hello spanish;

    ImplicitNameIndistinctType(Hello english, Hello spanish) {
        this.english = english;
        this.spanish = spanish;
    }

    public String execute() {
        return english.greet() + " " + spanish.greet();

    }

    public Hello getEnglish() {
        return english;
    }

    public Hello getSpanish() {
        return spanish;
    }

}
