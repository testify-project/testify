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
package org.testify.junit.fixture.common.impl;

import org.testify.junit.fixture.common.CustomQualifier;
import org.testify.junit.fixture.common.Greeting;

/**
 * Caio Greeting implementation.
 *
 * @author saden
 */
@CustomQualifier
public class Caio implements Greeting {

    @Override
    public String phrase() {
        return "Hello";
    }

}
