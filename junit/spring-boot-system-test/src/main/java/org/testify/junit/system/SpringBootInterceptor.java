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

import java.util.concurrent.Callable;
import javax.servlet.ServletContext;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.testify.ServiceInstance;
import org.testify.ServiceProvider;
import org.testify.TestContext;
import org.testify.TestReifier;
import org.testify.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testify.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testify.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testify.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testify.bytebuddy.implementation.bind.annotation.This;
import static org.testify.core.impl.TestContextProperties.APP;
import static org.testify.core.impl.TestContextProperties.APP_ARGUMENTS;
import static org.testify.core.impl.TestContextProperties.APP_SERVLET_CONTAINER;
import static org.testify.core.impl.TestContextProperties.APP_SERVLET_CONTEXT;
import static org.testify.core.impl.TestContextProperties.SERVICE_INSTANCE;
import org.testify.core.util.ServiceLocatorUtil;

/**
 * A class that intercepts methods of classes that extend or implement
 * {@link org.springframework.boot.SpringApplication} and
 * {@link org.springframework.boot.context.embedded.EmbeddedWebApplicationContext}.
 * This class is responsible for configuring the Spring Boot application as well
 * as extracting information useful for test reification.
 *
 * @author saden
 */
public class SpringBootInterceptor {

    private final InheritableThreadLocal<TestContext> localTestContext;

    SpringBootInterceptor(InheritableThreadLocal<TestContext> localTestContext) {
        this.localTestContext = localTestContext;
    }

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object,
            @AllArguments Object[] args)
            throws Exception {
        return zuper.call();
    }

    public ConfigurableApplicationContext run(
            @SuperCall Callable<ConfigurableApplicationContext> zuper,
            @This Object object,
            @AllArguments Object[] args) throws Exception {
        AnnotationConfigEmbeddedWebApplicationContext applicationContext
                = (AnnotationConfigEmbeddedWebApplicationContext) zuper.call();

        TestContext testContext = localTestContext.get();

        testContext.addProperty(APP, object);

        if (args.length == 2) {
            testContext.addProperty(APP_ARGUMENTS, args[1]);
        }

        return applicationContext;
    }

    public void refresh(@SuperCall Callable<Void> zuper, ApplicationContext applicationContext)
            throws Exception {
        TestContext testContext = localTestContext.get();
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

        ServiceProvider serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);
        ServiceInstance serviceInstance = serviceProvider.configure(testContext, configurableApplicationContext);

        zuper.call();

        serviceProvider.postConfigure(testContext, serviceInstance);

        TestReifier testReifier = testContext.getTestReifier();
        testReifier.configure(testContext, configurableApplicationContext);

        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);
    }

    public EmbeddedServletContainerFactory getEmbeddedServletContainerFactory(
            @SuperCall Callable<EmbeddedServletContainerFactory> zuper,
            @This Object object) throws Exception {
        EmbeddedServletContainerFactory containerFactory = zuper.call();
        ConfigurableEmbeddedServletContainer servletContainer = (ConfigurableEmbeddedServletContainer) containerFactory;
        servletContainer.setPort(0);
        TestContext testContext = localTestContext.get();

        testContext.getTestReifier().configure(testContext, servletContainer);

        return containerFactory;
    }

    protected void prepareEmbeddedWebApplicationContext(@SuperCall Callable<Void> zuper, ServletContext servletContext) throws Exception {
        TestContext testContext = localTestContext.get();
        zuper.call();
    }

    public EmbeddedServletContainer startEmbeddedServletContainer(
            @SuperCall Callable<EmbeddedServletContainer> zuper,
            @This Object object) throws Exception {
        TestContext testContext = localTestContext.get();

        EmbeddedServletContainer servletContainer = zuper.call();

        testContext.addProperty(APP_SERVLET_CONTAINER, servletContainer);

        return servletContainer;
    }

    public void prepareEmbeddedWebApplicationContext(@SuperCall Callable<Void> zuper,
            @This Object object,
            @AllArguments Object[] args) throws Exception {
        zuper.call();

        TestContext testContext = localTestContext.get();
        testContext.addProperty(APP_SERVLET_CONTEXT, args[0]);
    }

}
