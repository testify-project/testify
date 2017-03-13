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

import javax.inject.Provider;
import org.testifyproject.junit4.fixture.collaborator.Hello;

/**
 *
 * @author saden
 */
public class ImplicitNameIndistinctGenericType {

    private final Provider<Hello> english;
    private final Provider<Hello> spanish;

    ImplicitNameIndistinctGenericType(Provider<Hello> english, Provider<Hello> spanish) {
        this.english = english;
        this.spanish = spanish;
    }

    public String execute() {
        return english.get().greet() + " " + spanish.get().greet();

    }

    public Provider<Hello> getEnglish() {
        return english;
    }

    public Provider<Hello> getSpanish() {
        return spanish;
    }

}