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

import java.lang.instrument.Instrumentation;
import java.util.ServiceLoader;

import org.testifyproject.bytebuddy.agent.ByteBuddyAgent;
import org.testifyproject.bytebuddy.agent.builder.AgentBuilder;
import org.testifyproject.bytebuddy.matcher.ElementMatchers;
import org.testifyproject.extension.InstrumentInstance;
import org.testifyproject.extension.InstrumentProvider;

/**
 * An agent implementation that enables the rebasing of classes to enable the ability to
 * intercept method calls.
 *
 * @author saden
 */
public class InstrumentAgent {

    private InstrumentAgent() {
    }

    public static void main(String[] args) {
        premain("", ByteBuddyAgent.install());

    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        AgentBuilder agentBuilder = new AgentBuilder.Default();

        ServiceLoader<InstrumentProvider> serviceLoader =
                ServiceLoader.load(InstrumentProvider.class);

        for (InstrumentProvider instrumentProvider : serviceLoader) {
            InstrumentInstance instance = instrumentProvider.get();

            agentBuilder = agentBuilder
                    .type(ElementMatchers.named(instance.getClassName()))
                    .transform(InstrumentTransformer.of(instance))
                    .asDecorator();
        }

        agentBuilder.installOn(instrumentation);
    }

}
