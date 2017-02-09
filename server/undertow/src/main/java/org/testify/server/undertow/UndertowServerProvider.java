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
package org.testify.server.undertow;

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
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import org.testify.ApplicationInstance;
import org.testify.ApplicationProvider;
import org.testify.ServerInstance;
import org.testify.ServerProvider;
import org.testify.TestContext;
import org.testify.core.impl.DefaultServerInstance;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.tools.Discoverable;
import org.xnio.StreamConnection;
import org.xnio.channels.AcceptingChannel;

/**
 * An Undertow implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class UndertowServerProvider implements ServerProvider<DeploymentInfo> {

    private Undertow undertow;
    private URI baseURI;
    private ApplicationProvider applicationProvider;

    @Override
    public DeploymentInfo configure(TestContext testContext) {
        applicationProvider = ServiceLocatorUtil.INSTANCE.getOne(ApplicationProvider.class);
        ApplicationInstance<ServletContainerInitializer> applicationInstance = applicationProvider.start(testContext);

        try {
            String name = testContext.getName();

            Set<Class<?>> handles = applicationInstance.getHandlers();
            ServletContainerInitializer initializerInstance = applicationInstance.getInitializer();

            ImmediateInstanceFactory<ServletContainerInitializer> factory = new ImmediateInstanceFactory<>(initializerInstance);
            URI uri = URI.create("http://0.0.0.0:0/");
            Class<? extends ServletContainerInitializer> initializerClass = initializerInstance.getClass();
            ServletContainerInitializerInfo initInfo = new ServletContainerInitializerInfo(initializerClass, factory, handles);

            ServletInfo servletInfo = Servlets.servlet(name, DefaultServlet.class);

            ClassLoader classLoader = testContext.getTestClass().getClassLoader();

            DeploymentInfo deploymentInfo = Servlets.deployment()
                    .addServletContainerInitalizer(initInfo)
                    .setClassLoader(classLoader)
                    .setHostName(uri.getHost())
                    .setContextPath(uri.getPath())
                    .setDeploymentName(name)
                    .addServlet(servletInfo);

            return deploymentInfo;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ServerInstance start(DeploymentInfo deploymentInfo) {
        try {
            DeploymentManager manager = Servlets.defaultContainer()
                    .addDeployment(deploymentInfo);

            manager.deploy();
            HttpHandler httpHandler = manager.start();

            RedirectHandler defaultHandler = Handlers.redirect(deploymentInfo.getContextPath());
            PathHandler pathHandler = Handlers.path(defaultHandler);
            pathHandler.addPrefixPath(deploymentInfo.getContextPath(), httpHandler);

            undertow = Undertow.builder()
                    .addHttpListener(0, deploymentInfo.getHostName(), pathHandler)
                    .build();

            undertow.start();

            baseURI = new URI("http",
                    null,
                    deploymentInfo.getHostName(),
                    getPorts().get(0),
                    deploymentInfo.getContextPath(),
                    null,
                    null);

            return DefaultServerInstance.of(baseURI, undertow);
        } catch (Throwable e) {
            throw new IllegalStateException("Could not start Undertow Server", e);
        }
    }

    @Override
    public void stop() {
        undertow.stop();
        applicationProvider.stop();
    }

    public List<Integer> getPorts() {
        List<Integer> ports = new ArrayList<>();
        Field channelsField = findField(Undertow.class, "channels").get();
        channelsField.setAccessible(true);
        List<AcceptingChannel<? extends StreamConnection>> channels = (List) getFieldValue(channelsField, undertow);
        channels.stream()
                .map(p -> getPortFromChannel(p)).
                filter(Objects::nonNull)
                .forEach(p -> ports.add(p));

        return ports;

    }

    Integer getPortFromChannel(Object channel) {
        Object tcpServer = channel;
        Optional<Field> sslContext = findField(channel.getClass(), "sslContext");
        if (sslContext.isPresent()) {
            tcpServer = getTcpServer(channel);
        }

        ServerSocket socket = getSocket(tcpServer);

        return socket.getLocalPort();

    }

    Object getTcpServer(Object channel) {
        Field field = findField(channel.getClass(), "tcpServer").get();
        return getFieldValue(field, channel);
    }

    ServerSocket getSocket(Object tcpServer) {
        Optional<Field> socketField = findField(tcpServer.getClass(), "socket");
        if (!socketField.isPresent()) {
            return null;
        }

        return getFieldValue(socketField.get(), tcpServer);
    }

    public Optional<Field> findField(Class<?> type, String name) {
        try {
            return of(type.getDeclaredField(name));
        } catch (Exception e) {
            return empty();
        }
    }

    public <T> T getFieldValue(Field field, Object instance) {
        try {
            field.setAccessible(true);

            return (T) field.get(instance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
