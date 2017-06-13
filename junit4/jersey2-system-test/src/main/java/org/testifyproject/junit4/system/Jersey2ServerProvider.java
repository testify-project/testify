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
import java.net.URI;
import java.util.Optional;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.ServerInstanceBuilder;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.APP;
import static org.testifyproject.core.TestContextProperties.APP_NAME;
import static org.testifyproject.core.TestContextProperties.APP_SERVLET_CONTAINER;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;

/**
 * A SpringBoot implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class Jersey2ServerProvider implements ServerProvider<ResourceConfig, HttpServer> {

    private static final String DEFAULT_URI_FORMAT = "%s://%s:%d%s";
    private static final String DEFAULT_SCHEME = "http";
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_PORT = 0;
    private static final String DEFAULT_PATH = "/";

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public ResourceConfig configure(TestContext testContext) {
        TestContextHolder.INSTANCE.set(testContext);

        String className = "org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Jerset2Interceptor interceptor = new Jerset2Interceptor(TestContextHolder.INSTANCE);
        ReflectionUtil.INSTANCE.rebase(className, classLoader, interceptor);

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        Optional<Application> foundApplication = testDescriptor.getApplication();
        ResourceConfig resourceConfig = null;

        if (foundApplication.isPresent()) {
            Application application = foundApplication.get();

            resourceConfig = (ResourceConfig) ReflectionUtil.INSTANCE.newInstance(application.value());
            Jersey2ApplicationListener listener = new Jersey2ApplicationListener(TestContextHolder.INSTANCE);
            resourceConfig.register(listener);
            resourceConfig.setApplicationName(testContext.getName());
        }

        return testConfigurer.configure(testContext, resourceConfig);
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public ServerInstance<HttpServer> start(TestContext testContext, ResourceConfig resourceConfig) {
        URI uri = URI.create(format(DEFAULT_URI_FORMAT, DEFAULT_SCHEME, DEFAULT_HOST, DEFAULT_PORT, DEFAULT_PATH));
        // create and start a new instance of grizzly http server
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig, true);

        Optional<NetworkListener> foundListener = server.getListeners().stream().findFirst();
        ServerInstance serverInstance = null;

        if (foundListener.isPresent()) {
            NetworkListener networkListener = foundListener.get();
            String host = networkListener.getHost();
            int port = networkListener.getPort();

            URI baseURI = URI.create(format(DEFAULT_URI_FORMAT, DEFAULT_SCHEME, host, port, DEFAULT_PATH));

            serverInstance = ServerInstanceBuilder.builder()
                    .baseURI(baseURI)
                    .server(server)
                    .property(APP, resourceConfig)
                    .property(APP_NAME, testContext.getName())
                    .property(APP_SERVLET_CONTAINER, server)
                    .build("jersey");
        }

        return serverInstance;
    }

    @Override
    public void stop(TestContext testContext, ServerInstance<HttpServer> serverInstance) {
        if (serverInstance != null) {
            LoggingUtil.INSTANCE.debug("Stopping Jersey server");
            serverInstance.getServer().getValue().stop();
        }
    }

}
