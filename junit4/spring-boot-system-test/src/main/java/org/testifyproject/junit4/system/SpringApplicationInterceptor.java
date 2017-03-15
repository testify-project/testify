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

import java.util.concurrent.Callable;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestReifier;
import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.APP;
import static org.testifyproject.core.TestContextProperties.APP_ARGUMENTS;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ServiceLocatorUtil;

/**
 * A class that intercepts methods of classes that extend or implement
 * {@link org.springframework.boot.SpringApplication} and
 * {@link org.springframework.boot.context.embedded.EmbeddedWebApplicationContext}.
 * This class is responsible for configuring the Spring Boot application as well
 * as extracting information useful for test reification.
 *
 * @author saden
 */
public class SpringApplicationInterceptor {

    private final TestContextHolder testContextHolder;

    SpringApplicationInterceptor(TestContextHolder testContextHolder) {
        this.testContextHolder = testContextHolder;
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

        testContextHolder.execute(testContext -> {
            testContext.addProperty(APP, object);

            if (args.length == 2) {
                testContext.addProperty(APP_ARGUMENTS, args[1]);
            }
        });

        return applicationContext;
    }

    public void refresh(@SuperCall Callable<Void> zuper, ApplicationContext applicationContext)
            throws Exception {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        //TODO: should the service provider be set up front and available from the test context?
        ServiceProvider serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);

        ServiceInstance serviceInstance = testContextHolder.execute(testContext -> {
            return serviceProvider.configure(testContext, configurableApplicationContext);
        });

        zuper.call();

        testContextHolder.execute(testContext -> {
            serviceProvider.postConfigure(testContext, serviceInstance);

            TestReifier testReifier = testContext.getTestReifier();
            testReifier.configure(testContext, configurableApplicationContext);

            testContext.addProperty(SERVICE_INSTANCE, serviceInstance);
        });

    }

}
