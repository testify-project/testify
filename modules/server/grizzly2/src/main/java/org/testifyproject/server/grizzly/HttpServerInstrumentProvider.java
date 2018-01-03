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

import java.net.URI;
import java.util.concurrent.Callable;

import org.glassfish.grizzly.http.server.HttpServer;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.extension.instrument.InstrumentInstanceBuilder;
import org.testifyproject.extension.InstrumentInstance;
import org.testifyproject.extension.InstrumentProvider;

/**
 * TODO.
 *
 * @author saden
 */
@Discoverable
public class HttpServerInstrumentProvider implements InstrumentProvider {

    @Override
    public InstrumentInstance get() {
        return InstrumentInstanceBuilder.builder()
                .build("org.glassfish.grizzly.http.server.HttpServer", this);
    }

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object,
            @AllArguments Object[] args)
            throws Exception {
        return zuper.call();
    }

    public void start(@SuperCall Callable<?> zuper,
            @This(optional = true) Object object) throws Exception {
        zuper.call();
        TestContextHolder.INSTANCE.command(testContext -> {
            HttpServer server = (HttpServer) object;
            server.getListeners().stream().findFirst().ifPresent(listener -> {
                String host = listener.getHost();
                int port = listener.getPort();
                URI uri = URI.create(String.format("http://%s:%d/", host, port));
                testContext.addProperty(TestContextProperties.SERVER_BASE_URI, uri);
            });

            testContext.addProperty(TestContextProperties.SERVER, server);
        });

    }

    public void wait(@SuperCall Callable<Void> zuper) throws Exception {
        if (!TestContextHolder.INSTANCE.isPresent()) {
            zuper.call();
        }
    }
}
