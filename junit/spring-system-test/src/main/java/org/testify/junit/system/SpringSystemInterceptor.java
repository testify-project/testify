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
import java.util.stream.Stream;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.testify.ApplicationInstance;
import org.testify.ServiceInstance;
import org.testify.ServiceProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testify.bytebuddy.implementation.bind.annotation.Argument;
import org.testify.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testify.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testify.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testify.bytebuddy.implementation.bind.annotation.This;
import static org.testify.core.impl.TestContextProperties.SERVICE_INSTANCE;
import org.testify.core.util.ServiceLocatorUtil;

/**
 * A class that intercepts methods of classes that extend
 * {@link org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer}.
 * This class is responsible for configuring the Spring application as well as
 * extracting information useful for test scaffolding.
 *
 * @author saden
 */
public class SpringSystemInterceptor {

    private final InheritableThreadLocal<ApplicationInstance> localApplicationInstance;

    SpringSystemInterceptor(InheritableThreadLocal<ApplicationInstance> localApplicationInstance) {
        this.localApplicationInstance = localApplicationInstance;
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

    public void configureAndRefreshWebApplicationContext(@SuperCall Callable<ConfigurableWebApplicationContext> zuper,
            @Argument(0) ConfigurableWebApplicationContext applicationContext) throws Exception {
        ApplicationInstance applicationInstance = localApplicationInstance.get();
        TestContext testContext = applicationInstance.getTestContext();
        TestReifier testReifier = testContext.getTestReifier();
        applicationContext = testReifier.configure(applicationContext);

        ServiceProvider<ConfigurableApplicationContext> serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);
        ServiceInstance serviceInstance = serviceProvider.configure(testContext, applicationContext);

        zuper.call();

        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);
    }

    public Class<?>[] getServletConfigClasses(@SuperCall Callable<Class<?>[]> zuper, @This Object object) throws Exception {
        ApplicationInstance applicationInstance = localApplicationInstance.get();
        TestContext testContext = applicationInstance.getTestContext();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        Class<?>[] result = zuper.call();

        Stream<Class<?>> acutalModules = Stream.of(result);
        Stream<Class<?>> testModules = testDescriptor.getModules()
                .stream()
                .map(p -> p.value());

        return Stream.concat(testModules, acutalModules).toArray(Class[]::new);
    }

}
