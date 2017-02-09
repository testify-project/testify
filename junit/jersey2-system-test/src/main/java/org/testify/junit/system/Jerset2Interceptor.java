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
package org.testify.junit.system;

import java.net.URI;
import java.util.concurrent.Callable;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.server.ApplicationHandler;
import org.testify.ServiceInstance;
import org.testify.ServiceProvider;
import org.testify.TestContext;
import org.testify.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testify.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testify.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testify.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testify.bytebuddy.implementation.bind.annotation.This;
import static org.testify.core.TestContextProperties.SERVICE_INSTANCE;
import org.testify.core.util.ServiceLocatorUtil;

/**
 * A class that intercepts methods of classes that extend or implement
 * {@link org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory}
 * This class is responsible for configuring the Jersey 2 application as well as
 * extracting information useful for test reification.
 *
 * @author saden
 */
public class Jerset2Interceptor {

    private final InheritableThreadLocal<TestContext> localTestContext;

    Jerset2Interceptor(InheritableThreadLocal<TestContext> localTestContext) {
        this.localTestContext = localTestContext;
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
        ApplicationHandler applicationHandler = handler.getApplicationHandler();
        ServiceLocator serviceLocator = applicationHandler.getServiceLocator();
        TestContext testContext = localTestContext.get();
        serviceLocator = testContext.getTestReifier().configure(testContext, serviceLocator);

        ServiceProvider<ServiceLocator> serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);

        ServiceInstance serviceInstance = serviceProvider.configure(testContext, serviceLocator);
        serviceProvider.postConfigure(testContext, serviceInstance);
        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);

        return zuper.call();

    }

}
