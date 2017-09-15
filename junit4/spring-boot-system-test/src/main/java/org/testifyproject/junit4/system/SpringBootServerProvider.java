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
import static org.testifyproject.core.TestContextProperties.APP_SERVLET_CONTAINER;
import static org.testifyproject.core.TestContextProperties.APP_SERVLET_CONTEXT;

import java.net.URI;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.core.io.DefaultResourceLoader;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Module;
import org.testifyproject.core.ServerInstanceBuilder;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;

/**
 * A SpringBoot implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringBootServerProvider implements
        ServerProvider<SpringApplicationBuilder, EmbeddedServletContainer> {

    private static final String DEFAULT_URI_FORMAT = "http://0.0.0.0:%d%s";
    private static final TestContextHolder TEST_CONTEXT_HOLDER =
            TestContextHolder.INSTANCE;

    @Override
    public SpringApplicationBuilder configure(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();

        TEST_CONTEXT_HOLDER.set(testContext);

        SpringApplicationInterceptor springApplicationInterceptor =
                new SpringApplicationInterceptor(TEST_CONTEXT_HOLDER);

        ClassLoader classLoader = testDescriptor.getTestClassLoader();

        String applicationClassName = "org.springframework.boot.SpringApplication";

        ReflectionUtil.INSTANCE.rebase(applicationClassName, classLoader,
                springApplicationInterceptor);

        ApplicationContextInterceptor applicationContextInterceptor =
                new ApplicationContextInterceptor(TEST_CONTEXT_HOLDER);

        String applicationContextClassName =
                "org.springframework.boot.context.embedded.EmbeddedWebApplicationContext";

        ReflectionUtil.INSTANCE.rebase(applicationContextClassName, classLoader,
                applicationContextInterceptor);

        Optional<Application> foundApplication = testDescriptor.getApplication();
        SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder();

        foundApplication.ifPresent(application -> {
            Class[] modules = testDescriptor.getModules()
                    .stream()
                    .sequential()
                    .map(Module::value)
                    .toArray(Class[]::new);

            applicationBuilder.sources(application.value())
                    .sources(modules)
                    .resourceLoader(new DefaultResourceLoader(classLoader))
                    .bannerMode(Banner.Mode.OFF);
        });

        return testConfigurer.configure(testContext, applicationBuilder);
    }

    @Override
    public ServerInstance<EmbeddedServletContainer> start(TestContext testContext,
            Application application, SpringApplicationBuilder configuration) {
        LoggingUtil.INSTANCE.debug("Starting Spring Boot application '{}'", testContext
                .getName());
        SpringApplication springApplication = configuration.build();

        springApplication.run();

        Optional<ServletContext> servletContext = testContext.findProperty(
                APP_SERVLET_CONTEXT);
        Optional<EmbeddedServletContainer> servletContainer = testContext.findProperty(
                APP_SERVLET_CONTAINER);

        if (servletContext.isPresent() && servletContainer.isPresent()) {
            EmbeddedServletContainer server = servletContainer.get();
            ServletContext context = servletContext.get();

            String uri = format(DEFAULT_URI_FORMAT, server.getPort(), context
                    .getContextPath());
            URI baseURI = URI.create(uri);

            return ServerInstanceBuilder.builder()
                    .baseURI(baseURI)
                    .server(server)
                    .property(APP, springApplication)
                    .property(APP_NAME, testContext.getName())
                    .property(APP_SERVLET_CONTAINER, server)
                    .build("springboot", application);
        }

        throw ExceptionUtil.INSTANCE.propagate(
                "Could not start springboot application due to missing servlet container");
    }

    @Override
    public void stop(ServerInstance<EmbeddedServletContainer> serverInstance) {
        if (serverInstance != null) {
            LoggingUtil.INSTANCE.debug("Stopping Spring Boot server");
            serverInstance.getServer().getValue().stop();
        }
    }

}
