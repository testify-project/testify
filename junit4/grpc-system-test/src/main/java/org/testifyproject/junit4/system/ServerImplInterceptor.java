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
package org.testifyproject.junit4.system;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.Callable;

import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Origin;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.util.LoggingUtil;

import io.grpc.Server;

/**
 * GRPC Server operation interceptor. This class intercepts certain GRPC Server initialization
 * calls to configure the test case.
 *
 * @author saden
 */
public class ServerImplInterceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(@SuperCall Callable<?> zuper,
            @Origin Method method,
            @This(optional = true) Object object)
            throws Exception {
        Object result = zuper.call();

        if (method.getName().equals("start")) {
            TestContextHolder.INSTANCE.command(testContext -> {
                int port = ((Server) object).getPort();
                testContext.addProperty(TestContextProperties.APP_PORT, port);
                testContext.addProperty(TestContextProperties.SERVER, object);

                URI baseURI = URI.create(format("grpc://localhost:%d", port));
                testContext.addProperty(TestContextProperties.SERVER_BASE_URI, baseURI);
            });
        }

        return result;
    }

    public void awaitTermination(@SuperCall Callable<?> zuper, @AllArguments Object[] args)
            throws InterruptedException {
        //for testing purpose trap calls for waitawaitTermination and do nothing
        LoggingUtil.INSTANCE.debug("awaitTermination intercepted and trapped");
    }
}
