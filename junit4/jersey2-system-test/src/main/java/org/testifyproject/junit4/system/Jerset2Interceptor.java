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

import java.net.URI;
import java.util.concurrent.Callable;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.server.ApplicationHandler;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.BASE_URI;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ServiceLocatorUtil;

/**
 * A class that intercepts methods of classes that extend or implement
 * {@link org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory}
 * This class is responsible for configuring the Jersey 2 application as well as
 * extracting information useful for test reification.
 *
 * @author saden
 */
public class Jerset2Interceptor {

    private final TestContextHolder testContextHolder;

    Jerset2Interceptor(TestContextHolder testContextHolder) {
        this.testContextHolder = testContextHolder;
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

    public HttpServer createHttpServer(@SuperCall Callable<HttpServer> zuper,
            URI uri,
            GrizzlyHttpContainer handler,
            boolean secure,
            SSLEngineConfigurator sslEngineConfigurator,
            boolean start) throws Exception {

        testContextHolder.execute(testContext -> {
            ApplicationHandler applicationHandler = handler.getApplicationHandler();
            ServiceLocator serviceLocator = applicationHandler.getServiceLocator();
            serviceLocator = testContext.getTestReifier().configure(testContext, serviceLocator);

            ServiceProvider<ServiceLocator> serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);

            ServiceInstance serviceInstance = serviceProvider.configure(testContext, serviceLocator);
            serviceProvider.postConfigure(testContext, serviceInstance);
            testContext.addProperty(SERVICE_INSTANCE, serviceInstance);
            testContext.addProperty(BASE_URI, uri);
        });

        return zuper.call();
    }

}
