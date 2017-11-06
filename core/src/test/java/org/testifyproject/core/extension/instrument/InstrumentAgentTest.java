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
package org.testifyproject.core.extension.instrument;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.instrument.Instrumentation;

import org.testifyproject.bytebuddy.agent.ByteBuddyAgent;
import org.testifyproject.fixture.instrument.Greeter;

/**
 * TODO.
 *
 * @author saden
 */
public class InstrumentAgentTest {

    private InstrumentAgentTest() {
    }

    public static void main(String[] args) {
        premain("", ByteBuddyAgent.install());

        Greeter greeter = new Greeter();
        String result = greeter.modifiedGreeting();

        assertThat(result).isEqualTo("HELLO");
    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        InstrumentAgent.premain(arguments, instrumentation);
    }

}
