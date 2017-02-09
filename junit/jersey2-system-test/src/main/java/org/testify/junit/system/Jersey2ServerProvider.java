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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.testify.ServerInstance;
import org.testify.ServerProvider;
import org.testify.TestContext;
import org.testify.annotation.Application;
import org.testify.bytebuddy.ByteBuddy;
import org.testify.bytebuddy.description.type.TypeDescription;
import org.testify.bytebuddy.dynamic.ClassFileLocator;
import org.testify.bytebuddy.dynamic.DynamicType;
import org.testify.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import static org.testify.bytebuddy.implementation.MethodDelegation.to;
import org.testify.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testify.bytebuddy.implementation.bind.annotation.BindingPriority;
import static org.testify.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testify.bytebuddy.matcher.ElementMatchers.not;
import org.testify.bytebuddy.pool.TypePool;
import org.testify.core.DefaultServerInstance;
import static org.testify.core.TestContextProperties.APP;
import static org.testify.core.TestContextProperties.APP_NAME;
import static org.testify.core.TestContextProperties.APP_SERVLET_CONTAINER;
import org.testify.tools.Discoverable;

/**
 * A SpringBoot implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class Jersey2ServerProvider implements ServerProvider<ResourceConfig> {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();
    private static final Map<String, DynamicType.Loaded<?>> REBASED_CLASSES = new ConcurrentHashMap<>();
    private static final InheritableThreadLocal<TestContext> LOCAL_TEST_CONTEXT = new InheritableThreadLocal<>();

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public ResourceConfig configure(TestContext testContext) {
        try {
            LOCAL_TEST_CONTEXT.set(testContext);

            Jerset2Interceptor interceptor = new Jerset2Interceptor(LOCAL_TEST_CONTEXT);

            ClassFileLocator locator = ClassFileLocator.ForClassLoader.ofClassPath();
            TypePool typePool = TypePool.Default.ofClassPath();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            String httpServerClassName = "org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory";

            REBASED_CLASSES.computeIfAbsent(httpServerClassName, p -> {
                TypeDescription typeDescription = typePool.describe(p).resolve();

                return BYTE_BUDDY
                        .rebase(typeDescription, locator)
                        .method(not(isDeclaredBy(Object.class)))
                        .intercept(to(interceptor)
                                .filter(not(isDeclaredBy(Object.class)))
                                .defineAmbiguityResolver(
                                        MethodNameEqualityResolver.INSTANCE,
                                        BindingPriority.Resolver.INSTANCE)
                        )
                        .make()
                        .load(classLoader, ClassLoadingStrategy.Default.INJECTION);
            });

            Application app = testContext.getTestDescriptor().getApplication().get();

            ResourceConfig resourceConfig = (ResourceConfig) app.value().newInstance();
            Jersey2ApplicationListener listener = new Jersey2ApplicationListener(LOCAL_TEST_CONTEXT);
            resourceConfig.register(listener);

            resourceConfig = testContext.getTestReifier().configure(testContext, resourceConfig);

            return resourceConfig;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    @Override
    public ServerInstance start(ResourceConfig configuration) {
        try {
            TestContext testContext = LOCAL_TEST_CONTEXT.get();

            testContext.addProperty(APP, configuration);
            testContext.addProperty(APP_NAME, testContext.getName());

            URI uri = new URI("http",
                    null,
                    "0.0.0.0",
                    0,
                    "/",
                    null,
                    null);

            // create and start a new instance of grizzly http server
            // exposing the Jersey application at BASE_URI
            HttpServer container = GrizzlyHttpServerFactory.createHttpServer(uri, configuration);
            testContext.addProperty(APP_SERVLET_CONTAINER, container);
            container.start();
            NetworkListener network = container.getListeners().stream().findFirst().get();

            String host = network.getHost();
            int port = network.getPort();

            URI baseUri = new URI("http",
                    null,
                    host,
                    port,
                    "/",
                    null,
                    null);

            return DefaultServerInstance.of(baseUri, container);
        } catch (Throwable e) {
            throw new IllegalStateException("Could not start Jersey 2 Application", e);
        }
    }

    @Override
    public void stop() {
        try {
            TestContext testContext = LOCAL_TEST_CONTEXT.get();
            Optional<HttpServer> servletContainer = testContext.findProperty(APP_SERVLET_CONTAINER);
            HttpServer httpServer = servletContainer.get();
            httpServer.shutdown().get();
            LOCAL_TEST_CONTEXT.remove();
        } catch (ExecutionException | InterruptedException e) {

            throw new IllegalStateException(e);
        }
    }

}
