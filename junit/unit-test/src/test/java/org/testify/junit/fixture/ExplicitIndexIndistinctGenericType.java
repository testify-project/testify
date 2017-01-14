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

import javax.inject.Provider;
import org.testify.junit.fixture.collaborator.Hello;

/**
 *
 * @author saden
 */
public class ExplicitIndexIndistinctGenericType {

    private final Provider<Hello> hello1;
    private final Provider<Hello> hello2;

    public ExplicitIndexIndistinctGenericType(Provider<Hello> hello1, Provider<Hello> hello2) {
        this.hello1 = hello1;
        this.hello2 = hello2;
    }

    public String execute() {
        return hello1.get().greet() + " " + hello2.get().greet();

    }

    public Provider<Hello> getHello1() {
        return hello1;
    }

    public Provider<Hello> getHello2() {
        return hello2;
    }

}
