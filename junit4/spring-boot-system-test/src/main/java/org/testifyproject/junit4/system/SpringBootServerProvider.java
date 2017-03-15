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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.core.io.DefaultResourceLoader;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.annotation.Application;
import org.testifyproject.bytebuddy.ByteBuddy;
import org.testifyproject.bytebuddy.description.type.TypeDescription;
import org.testifyproject.bytebuddy.dynamic.ClassFileLocator;
import org.testifyproject.bytebuddy.dynamic.DynamicType;
import org.testifyproject.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.testifyproject.bytebuddy.implementation.MethodDelegation;
import static org.testifyproject.bytebuddy.implementation.MethodDelegation.to;
import org.testifyproject.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.not;
import org.testifyproject.bytebuddy.pool.TypePool;
import org.testifyproject.core.DefaultServerInstance;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.APP;
import static org.testifyproject.core.TestContextProperties.APP_NAME;
import static org.testifyproject.core.TestContextProperties.APP_SERVLET_CONTAINER;
import static org.testifyproject.core.TestContextProperties.APP_SERVLET_CONTEXT;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.trait.LoggingTrait;

/**
 * A SpringBoot implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringBootServerProvider implements ServerProvider<SpringApplicationBuilder, EmbeddedServletContainer>, LoggingTrait {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();
    private static final Map<String, DynamicType.Loaded<?>> DYNAMIC_CLASSES = new ConcurrentHashMap<>();
    private static final TestContextHolder TEST_CONTEXT_HOLDER = TestContextHolder.INSTANCE;

    @Override
    public SpringApplicationBuilder configure(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestReifier testReifier = testContext.getTestReifier();

        debug("setting test context");
        TEST_CONTEXT_HOLDER.set(testContext);

        SpringApplicationInterceptor springApplicationInterceptor
                = new SpringApplicationInterceptor(TEST_CONTEXT_HOLDER);

        ClassFileLocator fileLocator = ClassFileLocator.ForClassLoader.ofClassPath();
        TypePool typePool = TypePool.Default.ofClassPath();
        ClassLoader classLoader = testDescriptor.getTestClassLoader();

        String applicationClassName = "org.springframework.boot.SpringApplication";

        DYNAMIC_CLASSES.computeIfAbsent(applicationClassName, className -> {
            debug("rebasing spring application class: {}", className);

            TypeDescription typeDescription = typePool.describe(className).resolve();

            return BYTE_BUDDY
                    .rebase(typeDescription, fileLocator)
                    .method(not(isDeclaredBy(Object.class)))
                    .intercept(MethodDelegation.to(springApplicationInterceptor)
                            .filter(not(isDeclaredBy(Object.class)))
                            .defineAmbiguityResolver(
                                    MethodNameEqualityResolver.INSTANCE,
                                    BindingPriority.Resolver.INSTANCE)
                    )
                    .make()
                    .load(classLoader, ClassLoadingStrategy.Default.INJECTION);
        });

        ApplicationContextInterceptor applicationContextInterceptor
                = new ApplicationContextInterceptor(TEST_CONTEXT_HOLDER);

        String applicationContextClassName = "org.springframework.boot.context.embedded.EmbeddedWebApplicationContext";

        DYNAMIC_CLASSES.computeIfAbsent(applicationContextClassName, className -> {
            debug("rebasing spring embedded web application context class: {}", className);
            TypeDescription typeDescription = typePool.describe(className).resolve();

            return BYTE_BUDDY
                    .rebase(typeDescription, fileLocator)
                    .method(not(isDeclaredBy(Object.class)))
                    .intercept(
                            to(applicationContextInterceptor)
                                    .filter(not(isDeclaredBy(Object.class)))
                                    .defineAmbiguityResolver(
                                            MethodNameEqualityResolver.INSTANCE,
                                            BindingPriority.Resolver.INSTANCE)
                    )
                    .make()
                    .load(classLoader, ClassLoadingStrategy.Default.INJECTION);
        });

        Application application = testDescriptor.getApplication().get();

        Set<Object> sources = ImmutableSet.of(application.value());

        testDescriptor.getModules()
                .stream()
                .sequential()
                .map(p -> p.value())
                .forEach(sources::add);

        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(classLoader);

        SpringApplicationBuilder builder = new SpringApplicationBuilder(sources.toArray())
                .resourceLoader(resourceLoader)
                .bannerMode(Banner.Mode.OFF);

        debug("configuring spring boot application builder");
        builder = testReifier.configure(testContext, builder);

        return builder;
    }

    @Override
    public ServerInstance<EmbeddedServletContainer> start(SpringApplicationBuilder configuration) {
        try {
            debug("starting spring boot application");
            SpringApplication application = configuration.build();
            Optional<TestContext> result = TEST_CONTEXT_HOLDER.get();
            TestContext testContext = result.get();

            testContext.addProperty(APP, application);
            testContext.addProperty(APP_NAME, testContext.getName());

            debug("running spring boot application");
            application.run();

            Optional<ServletContext> servletContext = testContext.findProperty(APP_SERVLET_CONTEXT);
            Optional<EmbeddedServletContainer> servletContainer = testContext.findProperty(APP_SERVLET_CONTAINER);
            EmbeddedServletContainer container = servletContainer.get();
            ServletContext context = servletContext.get();

            URI baseURI = new URI("http",
                    null,
                    "0.0.0.0",
                    container.getPort(),
                    context.getContextPath(),
                    null,
                    null);

            debug("creating spring boot application server instance");
            return DefaultServerInstance.of(baseURI, container);
        } catch (Throwable e) {
            throw new IllegalStateException("Could not start Spring Boot Application", e);
        }
    }

    @Override
    public void stop() {
        debug("stopping spring application");
        Optional<TestContext> result = TEST_CONTEXT_HOLDER.get();
        TestContext testContext = result.get();
        Optional<EmbeddedServletContainer> servletContainer = testContext.findProperty(APP_SERVLET_CONTAINER);

        if (servletContainer.isPresent()) {
            debug("stopping spring boot application servlet container");
            EmbeddedServletContainer container = servletContainer.get();
            container.stop();
        }

        TEST_CONTEXT_HOLDER.remove();
    }

}
