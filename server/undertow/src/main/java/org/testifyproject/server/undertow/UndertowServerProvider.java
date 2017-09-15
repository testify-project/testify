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
package org.testifyproject.server.undertow;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import static org.testifyproject.core.ApplicationInstanceProperties.APPLICATION_INSTANCE;
import static org.testifyproject.core.ApplicationInstanceProperties.SERVLET_CONTAINER_INITIALIZER;
import static org.testifyproject.core.ApplicationInstanceProperties.SERVLET_HANDLERS;

import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;

import org.testifyproject.ApplicationInstance;
import org.testifyproject.ApplicationProvider;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.ServerInstanceBuilder;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;
import org.xnio.StreamConnection;
import org.xnio.channels.AcceptingChannel;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.handlers.DefaultServlet;
import io.undertow.servlet.util.ImmediateInstanceFactory;

/**
 * An Undertow implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class UndertowServerProvider implements ServerProvider<DeploymentInfo, Undertow> {

    private static final String DEFAULT_SCHEME = "http";
    private static final String DEFAULT_URI = "http://0.0.0.0:0/";
    private static final int DEFAULT_PORT = 0;

    private ApplicationProvider applicationProvider;

    @Override
    public DeploymentInfo configure(TestContext testContext) {
        applicationProvider = ServiceLocatorUtil.INSTANCE
                .getOne(ApplicationProvider.class);
        ApplicationInstance applicationInstance = applicationProvider.start(testContext);
        testContext.addProperty(APPLICATION_INSTANCE, applicationInstance);

        try {
            String name = testContext.getName();

            Optional<Set<Class<?>>> foundHandlers = applicationInstance.findProperty(
                    SERVLET_HANDLERS);
            Optional<ServletContainerInitializer> foundInitializer = applicationInstance
                    .findProperty(SERVLET_CONTAINER_INITIALIZER);
            DeploymentInfo deploymentInfo = null;

            if (foundHandlers.isPresent() && foundInitializer.isPresent()) {
                Set<Class<?>> handles = foundHandlers.get();
                ServletContainerInitializer initializerInstance = foundInitializer.get();

                ImmediateInstanceFactory<ServletContainerInitializer> factory =
                        new ImmediateInstanceFactory<>(initializerInstance);
                URI uri = URI.create(DEFAULT_URI);
                Class<? extends ServletContainerInitializer> initializerClass =
                        initializerInstance.getClass();
                ServletContainerInitializerInfo initInfo =
                        new ServletContainerInitializerInfo(initializerClass, factory,
                                handles);

                ServletInfo servletInfo = Servlets.servlet(name, DefaultServlet.class);
                TestDescriptor testDescriptor = testContext.getTestDescriptor();

                ClassLoader classLoader = testDescriptor.getTestClassLoader();

                deploymentInfo = Servlets.deployment()
                        .addServletContainerInitalizer(initInfo)
                        .setClassLoader(classLoader)
                        .setHostName(uri.getHost())
                        .setContextPath(uri.getPath())
                        .setDeploymentName(name)
                        .addServlet(servletInfo);
            }

            return deploymentInfo;
        } catch (Exception e) {
            throw ExceptionUtil.INSTANCE.propagate(e);
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public ServerInstance<Undertow> start(TestContext testContext, Application application,
            DeploymentInfo deploymentInfo) {
        try {
            DeploymentManager deploymentManager = Servlets.defaultContainer()
                    .addDeployment(deploymentInfo);

            deploymentManager.deploy();
            HttpHandler httpHandler = deploymentManager.start();

            String host = deploymentInfo.getHostName();
            String path = deploymentInfo.getContextPath();

            RedirectHandler defaultHandler = Handlers.redirect(path);
            PathHandler pathHandler = Handlers.path(defaultHandler);
            pathHandler.addPrefixPath(path, httpHandler);

            Undertow server = Undertow.builder()
                    .addHttpListener(DEFAULT_PORT, host, pathHandler)
                    .build();

            server.start();

            Integer port = getPorts(server).get(0);

            URI baseURI = new URI(DEFAULT_SCHEME, null, host, port, path, null, null);

            ServerInstance serverInstance = ServerInstanceBuilder.builder()
                    .baseURI(baseURI)
                    .server(server)
                    .property("deploymentInfo", deploymentInfo)
                    .property("deploymentManager", deploymentManager)
                    .property("httpHandler", httpHandler)
                    .build("undertow", application);
            return serverInstance;
        } catch (Exception e) {
            throw ExceptionUtil.INSTANCE.propagate("Could not start Undertow Server", e);
        }
    }

    @Override
    public void stop(ServerInstance<Undertow> serverInstance) {
        if (serverInstance != null) {
            LoggingUtil.INSTANCE.debug("Stopping Undertow server");
            serverInstance.getServer().getValue().stop();
        }

        applicationProvider.stop();
    }

    List<Integer> getPorts(Undertow undertow) {
        List<Integer> ports = new ArrayList<>();
        Optional<Field> foundField = findField(Undertow.class, "channels");

        if (foundField.isPresent()) {

            Field channelsField = foundField.get();
            channelsField.setAccessible(true);
            List<AcceptingChannel<? extends StreamConnection>> channels =
                    (List) getFieldValue(channelsField, undertow);

            channels.stream()
                    .map(this::getPortFromChannel).
                    filter(Objects::nonNull)
                    .forEach(ports::add);
        }

        return ports;
    }

    Integer getPortFromChannel(Object channel) {
        Object tcpServer = channel;
        Optional<Field> sslContext = findField(channel.getClass(), "sslContext");

        if (sslContext.isPresent()) {
            tcpServer = getTcpServer(channel);
        }

        return getSocket(tcpServer).getLocalPort();

    }

    Object getTcpServer(Object channel) {
        Optional<Field> foundField = findField(channel.getClass(), "tcpServer");
        if (foundField.isPresent()) {
            return getFieldValue(foundField.get(), channel);
        }

        return null;
    }

    ServerSocket getSocket(Object tcpServer) {
        Optional<Field> foundField = findField(tcpServer.getClass(), "socket");

        if (foundField.isPresent()) {
            return getFieldValue(foundField.get(), tcpServer);
        }

        return null;
    }

    Optional<Field> findField(Class<?> type, String name) {
        try {
            return of(type.getDeclaredField(name));
        } catch (NoSuchFieldException | SecurityException e) {
            LoggingUtil.INSTANCE.debug("Could not find field '{}' in type '{}'", name,
                    type.getSimpleName(), e);
            return empty();
        }
    }

    <T> T getFieldValue(Field field, Object instance) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> {
            try {
                field.setAccessible(true);

                return (T) field.get(instance);
            } catch (IllegalAccessException |
                    IllegalArgumentException |
                    SecurityException e) {
                throw ExceptionUtil.INSTANCE.propagate(e);
            }
        });
    }

}
