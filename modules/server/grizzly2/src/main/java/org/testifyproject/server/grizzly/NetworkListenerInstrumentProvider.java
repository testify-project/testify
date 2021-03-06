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
package org.testifyproject.server.grizzly;

import org.testifyproject.annotation.Discoverable;
import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.extension.instrument.InstrumentInstanceBuilder;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.extension.InstrumentInstance;
import org.testifyproject.extension.InstrumentProvider;

/**
 * TODO.
 *
 * @author saden
 */
@Discoverable
public class NetworkListenerInstrumentProvider implements InstrumentProvider {

    @Override
    public InstrumentInstance get() {
        return InstrumentInstanceBuilder.builder()
                .constructor()
                .build("org.glassfish.grizzly.http.server.NetworkListener", this);
    }

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public void anyConstructor(@This Object object,
            @AllArguments Object[] args)
            throws Exception {
        if (args.length == 3) {
            ReflectionUtil.INSTANCE.setDeclaredField("host", object, "0.0.0.0");
            ReflectionUtil.INSTANCE.setDeclaredField("port", object, 0);
        }
    }

}
