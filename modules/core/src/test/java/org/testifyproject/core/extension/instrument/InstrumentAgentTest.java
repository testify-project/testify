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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.Test;

/**
 *
 * @author saden
 */
public class InstrumentAgentTest {

    @Test
    public void verifyMethodInterception() throws Exception {
        InstrumentAgent.main(new String[]{});

        //avoid loading the class which makes it impossible to rebase with agent
        Class type = Class.forName("org.testifyproject.fixture.instrument.AgentGreeter");
        Method method = type.getDeclaredMethod("modifiedGreeting");
        Object greeter = type.newInstance();
        Object result = method.invoke(greeter);

        assertThat(result).isEqualTo("HELLO");
    }

    @Test
    public void verifyConstructorInterception() throws Exception {
        InstrumentAgent.main(new String[]{});

        //avoid loading the class which makes it impossible to rebase with agent
        Class type = Class.forName(
                "org.testifyproject.fixture.instrument.AgentConstructorGreeter");
        Method method = type.getDeclaredMethod("getPhrase");
        Constructor constructor = type.getDeclaredConstructor(String.class);
        Object greeter = constructor.newInstance("hello");
        Object result = method.invoke(greeter);

        assertThat(result).isEqualTo("HELLO");
    }

}
