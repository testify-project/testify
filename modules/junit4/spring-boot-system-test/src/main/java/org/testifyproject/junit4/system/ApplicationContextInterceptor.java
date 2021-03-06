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

import static org.testifyproject.core.TestContextProperties.SERVER;
import static org.testifyproject.server.core.ServletProperties.SERVLET_CONTEXT;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.servlet.ServletContext;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Argument;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.LoggingUtil;

/**
 * A class that intercepts methods of classes that extend or implement
 * {@link org.springframework.boot.SpringApplication} and
 * {@link org.springframework.boot.context.embedded.EmbeddedWebApplicationContext}. This class
 * is responsible for configuring the Spring Boot application as well as extracting information
 * useful for test reification.
 *
 * @author saden
 */
public class ApplicationContextInterceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object,
            @AllArguments Object[] args)
            throws Exception {
        return zuper.call();
    }

    public EmbeddedServletContainerFactory getEmbeddedServletContainerFactory(
            @SuperCall Callable<EmbeddedServletContainerFactory> zuper) throws Exception {
        EmbeddedServletContainerFactory containerFactory = zuper.call();

        TestContextHolder.INSTANCE.command(testContext -> {
            ConfigurableEmbeddedServletContainer servletContainer =
                    (ConfigurableEmbeddedServletContainer) containerFactory;
            servletContainer.setPort(0);

            TestConfigurer testConfigurer = testContext.getTestConfigurer();
            testConfigurer.configure(testContext, servletContainer);
        });

        return containerFactory;
    }

    protected void prepareEmbeddedWebApplicationContext(@SuperCall Callable<Void> zuper,
            @Argument(0) ServletContext servletContext) throws Exception {
        TestContextHolder.INSTANCE.command(testContext -> {
            TestConfigurer testConfigurer = testContext.getTestConfigurer();
            testConfigurer.configure(testContext, servletContext);
        });

        zuper.call();
    }

    public EmbeddedServletContainer startEmbeddedServletContainer(
            @SuperCall Callable<EmbeddedServletContainer> zuper) throws Exception {
        EmbeddedServletContainer servletContainer = zuper.call();

        TestContextHolder.INSTANCE.command((Consumer<TestContext>) testContext ->
                testContext.addProperty(SERVER, servletContainer)
        );

        return servletContainer;
    }

    public void prepareEmbeddedWebApplicationContext(@SuperCall Callable<Void> zuper,
            @AllArguments Object[] args) throws Exception {
        TestContextHolder.INSTANCE.command(testContext -> {
            LoggingUtil.INSTANCE.setTextContext(testContext);
            testContext.addProperty(SERVLET_CONTEXT, args[0]);
        });

        zuper.call();

    }

}
