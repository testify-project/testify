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

import static org.testifyproject.core.TestContextProperties.APP;
import static org.testifyproject.core.TestContextProperties.APP_NAME;
import static org.testifyproject.core.TestContextProperties.APP_SERVER;

import java.net.URI;
import java.util.Optional;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.ServerInstanceBuilder;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.ReflectionUtil;

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

        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        Optional<Application> foundApplication = testDescriptor.getApplication();
        ResourceConfig resourceConfig = null;

        if (foundApplication.isPresent()) {
            Application application = foundApplication.get();

            resourceConfig = ReflectionUtil.INSTANCE.newInstance(application.value());
            resourceConfig.setApplicationName(testContext.getName());
        }

        return resourceConfig;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public ServerInstance<HttpServer> start(TestContext testContext,
            Application application, ResourceConfig resourceConfig) {
        URI uri = URI.create(format(DEFAULT_URI_FORMAT, DEFAULT_SCHEME, DEFAULT_HOST,
                DEFAULT_PORT, DEFAULT_PATH));
        // create and start a new instance of grizzly http server
        HttpServer server =
                GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig, true);

        Optional<NetworkListener> foundListener = server.getListeners().stream()
                .findFirst();
        ServerInstance serverInstance = null;

        if (foundListener.isPresent()) {
            NetworkListener networkListener = foundListener.get();
            String host = networkListener.getHost();
            int port = networkListener.getPort();

            URI baseURI = URI.create(
                    format(DEFAULT_URI_FORMAT, DEFAULT_SCHEME, host, port, DEFAULT_PATH));

            serverInstance = ServerInstanceBuilder.builder()
                    .baseURI(baseURI)
                    .server(server)
                    .property(APP, resourceConfig)
                    .property(APP_NAME, testContext.getName())
                    .property(APP_SERVER, server)
                    .build("jersey", application);
        }

        return serverInstance;
    }

    @Override
    public void stop(ServerInstance<HttpServer> serverInstance) {
        if (serverInstance != null) {
            serverInstance.execute((httpServer, baseURI) -> {
                httpServer.shutdown();
            });
        }
    }

    @Override
    public Class<HttpServer> getServerType() {
        return HttpServer.class;
    }

}
