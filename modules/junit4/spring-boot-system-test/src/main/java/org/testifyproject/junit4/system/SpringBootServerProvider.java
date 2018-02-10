/*
 * Copyright 2016-2018 Testify Project.
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
import static org.testifyproject.core.TestContextProperties.SERVER;
import static org.testifyproject.server.core.ServletProperties.SERVLET_CONTEXT;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.ServerInstanceBuilder;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;

/**
 * A SpringBoot implementation of the ServerProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringBootServerProvider implements
        ServerProvider<SpringApplicationBuilder, EmbeddedServletContainer> {

    private static final String DEFAULT_URI_FORMAT = "http://0.0.0.0:%d%s";

    @Override
    public SpringApplicationBuilder configure(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        ClassLoader classLoader = testDescriptor.getTestClassLoader();

        Optional<Application> foundApplication = testDescriptor.getApplication();
        SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder();

        foundApplication.ifPresent(application -> {
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.application.name", testContext.getName());
            properties.put("server.port", 0);
            properties.put("spring.jmx.default-domain", testContext.getName());

            applicationBuilder.sources(application.value())
                    .resourceLoader(new DefaultResourceLoader(classLoader))
                    .bannerMode(Banner.Mode.OFF)
                    .properties(properties);
        });

        return applicationBuilder;
    }

    @Override
    public ServerInstance<EmbeddedServletContainer> start(TestContext testContext,
            Application application, SpringApplicationBuilder configuration) {
        LoggingUtil.INSTANCE.debug("Starting Spring Boot application '{}'", testContext
                .getName());
        SpringApplication springApplication = configuration.build();

        springApplication.run();

        Optional<ServletContext> servletContext =
                testContext.findProperty(SERVLET_CONTEXT);
        Optional<EmbeddedServletContainer> servletContainer =
                testContext.findProperty(SERVER);

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
                    .property(SERVER, server)
                    .build("springboot", application);
        }

        throw ExceptionUtil.INSTANCE.propagate(
                "Could not start springboot application due to missing servlet container");
    }

    @Override
    public void stop(ServerInstance<EmbeddedServletContainer> serverInstance) {
        if (serverInstance != null) {
            serverInstance.command((container, baseURI) -> {
                LoggingUtil.INSTANCE.debug("Stopping Spring Boot server");
                container.stop();
            });
        }
    }

    @Override
    public Class<EmbeddedServletContainer> getServerType() {
        return EmbeddedServletContainer.class;
    }

}
