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
import java.util.Set;
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
import org.testifyproject.annotation.Module;
import org.testifyproject.core.DefaultServerInstance;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.APP;
import static org.testifyproject.core.TestContextProperties.APP_NAME;
import static org.testifyproject.core.TestContextProperties.APP_SERVLET_CONTAINER;
import static org.testifyproject.core.TestContextProperties.APP_SERVLET_CONTEXT;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.tools.Discoverable;

/**
 * A SpringBoot implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringBootServerProvider implements ServerProvider<SpringApplicationBuilder, EmbeddedServletContainer> {

    private static final String DEFAULT_URI_FORMAT = "http://0.0.0.0:%d%s";
    private static final TestContextHolder TEST_CONTEXT_HOLDER = TestContextHolder.INSTANCE;

    @Override
    public SpringApplicationBuilder configure(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestReifier testReifier = testContext.getTestReifier();

        LoggingUtil.INSTANCE.debug("setting test context");
        TEST_CONTEXT_HOLDER.set(testContext);

        SpringApplicationInterceptor springApplicationInterceptor = new SpringApplicationInterceptor(TEST_CONTEXT_HOLDER);

        ClassLoader classLoader = testDescriptor.getTestClassLoader();

        String applicationClassName = "org.springframework.boot.SpringApplication";

        ReflectionUtil.INSTANCE.rebase(applicationClassName, classLoader, springApplicationInterceptor);

        ApplicationContextInterceptor applicationContextInterceptor = new ApplicationContextInterceptor(TEST_CONTEXT_HOLDER);

        String applicationContextClassName = "org.springframework.boot.context.embedded.EmbeddedWebApplicationContext";

        ReflectionUtil.INSTANCE.rebase(applicationContextClassName, classLoader, applicationContextInterceptor);

        Optional<Application> foundApplication = testDescriptor.getApplication();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        foundApplication.ifPresent(application -> {
            ImmutableSet.Builder<Object> sourcesBuilder = ImmutableSet.builder().add(application.value());

            testDescriptor.getModules()
                    .stream()
                    .sequential()
                    .map(Module::value)
                    .forEach(sourcesBuilder::add);

            Set<Object> sources = sourcesBuilder.build();

            DefaultResourceLoader resourceLoader = new DefaultResourceLoader(classLoader);

            builder.sources(sources.toArray())
                    .resourceLoader(resourceLoader)
                    .bannerMode(Banner.Mode.OFF);
        });

        return testReifier.configure(testContext, builder);
    }

    @Override
    public ServerInstance<EmbeddedServletContainer> start(SpringApplicationBuilder configuration) {
        LoggingUtil.INSTANCE.debug("starting spring boot application");
        SpringApplication application = configuration.build();
        Optional<TestContext> foundTextContext = TEST_CONTEXT_HOLDER.get();

        if (foundTextContext.isPresent()) {
            TestContext testContext = foundTextContext.get();

            testContext.addProperty(APP, application);
            testContext.addProperty(APP_NAME, testContext.getName());

            LoggingUtil.INSTANCE.debug("running spring boot application");
            application.run();

            Optional<ServletContext> servletContext = testContext.findProperty(APP_SERVLET_CONTEXT);
            Optional<EmbeddedServletContainer> servletContainer = testContext.findProperty(APP_SERVLET_CONTAINER);

            if (servletContext.isPresent() && servletContainer.isPresent()) {
                EmbeddedServletContainer container = servletContainer.get();
                ServletContext context = servletContext.get();

                String uri = format(DEFAULT_URI_FORMAT, container.getPort(), context.getContextPath());
                URI baseURI = URI.create(uri);

                LoggingUtil.INSTANCE.debug("creating spring boot application server instance");

                return DefaultServerInstance.of(baseURI, container);
            }
        }

        throw ExceptionUtil.INSTANCE.propagate("TestContext not set");
    }

    @Override
    public void stop() {
        LoggingUtil.INSTANCE.debug("stopping spring application");
        TEST_CONTEXT_HOLDER.execute(testContext -> {
            testContext.findProperty(APP_SERVLET_CONTAINER)
                    .map(EmbeddedServletContainer.class::cast)
                    .ifPresent(servletContainer -> {
                        LoggingUtil.INSTANCE.debug("stopping spring boot application servlet container");
                        servletContainer.stop();

                    });

            TEST_CONTEXT_HOLDER.remove();
        });
    }

}
