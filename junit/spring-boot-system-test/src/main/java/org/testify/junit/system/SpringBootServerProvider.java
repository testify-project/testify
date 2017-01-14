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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
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
import org.testify.core.impl.DefaultServerInstance;
import static org.testify.core.impl.TestContextProperties.APP;
import static org.testify.core.impl.TestContextProperties.APP_NAME;
import static org.testify.core.impl.TestContextProperties.APP_SERVLET_CONTAINER;
import static org.testify.core.impl.TestContextProperties.APP_SERVLET_CONTEXT;
import org.testify.tools.Discoverable;

/**
 * A SpringBoot implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringBootServerProvider implements ServerProvider<SpringApplicationBuilder> {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();
    private static final Map<String, DynamicType.Loaded<?>> REBASED_CLASSES = new ConcurrentHashMap<>();
    private static final InheritableThreadLocal<TestContext> LOCAL_TEST_CONTEXT = new InheritableThreadLocal<>();

    @Override
    public SpringApplicationBuilder configure(TestContext testContext) {
        LOCAL_TEST_CONTEXT.set(testContext);

        SpringBootInterceptor interceptor = new SpringBootInterceptor(LOCAL_TEST_CONTEXT);

        ClassFileLocator locator = ClassFileLocator.ForClassLoader.ofClassPath();
        TypePool typePool = TypePool.Default.ofClassPath();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String applicationClassName = "org.springframework.boot.SpringApplication";

        REBASED_CLASSES.computeIfAbsent(applicationClassName, p -> {
            TypeDescription typeDescription = typePool.describe(p).resolve();

            return BYTE_BUDDY
                    .rebase(typeDescription, locator)
                    .method(not(isDeclaredBy(Object.class)))
                    .intercept(
                            to(interceptor)
                                    .filter(not(isDeclaredBy(Object.class)))
                                    .defineAmbiguityResolver(
                                            MethodNameEqualityResolver.INSTANCE,
                                            BindingPriority.Resolver.INSTANCE)
                    )
                    .make()
                    .load(classLoader, ClassLoadingStrategy.Default.INJECTION);
        });

        String applicationContextClassName = "org.springframework.boot.context.embedded.EmbeddedWebApplicationContext";

        REBASED_CLASSES.computeIfAbsent(applicationContextClassName, p -> {
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

        Set<Object> sources = new LinkedHashSet<>();
        sources.add(app.value());

        testContext.getTestDescriptor()
                .getModules()
                .parallelStream()
                .map(p -> p.value())
                .forEach(sources::add);

        SpringApplicationBuilder builder = new SpringApplicationBuilder(sources.toArray())
                .bannerMode(Banner.Mode.OFF);

        builder = testContext.getTestReifier().configure(builder);

        return builder;
    }

    @Override
    public ServerInstance start(SpringApplicationBuilder configuration) {
        try {
            SpringApplication application = configuration.build();
            TestContext testContext = LOCAL_TEST_CONTEXT.get();

            testContext.addProperty(APP, application);
            testContext.addProperty(APP_NAME, testContext.getName());

            application.run();

            Optional<ServletContext> servletContext = testContext.getProperty(APP_SERVLET_CONTEXT);
            Optional<EmbeddedServletContainer> servletContainer = testContext.getProperty(APP_SERVLET_CONTAINER);
            EmbeddedServletContainer container = servletContainer.get();
            ServletContext context = servletContext.get();

            URI baseURI = new URI("http",
                    null,
                    "0.0.0.0",
                    container.getPort(),
                    context.getContextPath(),
                    null,
                    null);

            return DefaultServerInstance.of(baseURI, container);
        } catch (Throwable e) {
            throw new IllegalStateException("Could not start Spring Boot Application", e);
        }
    }

    @Override
    public void stop() {
        TestContext testContext = LOCAL_TEST_CONTEXT.get();
        Optional<EmbeddedServletContainer> servletContainer = testContext.getProperty(APP_SERVLET_CONTAINER);

        if (servletContainer.isPresent()) {
            EmbeddedServletContainer container = servletContainer.get();
            container.stop();
        }

        LOCAL_TEST_CONTEXT.remove();
    }

}
