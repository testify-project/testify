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

import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.util.concurrent.Callable;

import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.testifyproject.ServiceProvider;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Argument;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.di.jersey.JerseyServiceProvider;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * Jersey Application Handler operation interceptor. This class intercepts certain Jersey
 * ApplicationHandler initialization calls to configure the test case.
 *
 * @author saden
 */
public class ApplicationHandlerInterceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object)
            throws Exception {
        return zuper.call();
    }

    public void initialize(@SuperCall Callable<Void> zuper,
            @Argument(0) Object applicationConfigurator,
            @Argument(1) InjectionManager injectionManager,
            @Argument(2) Binder customBinder)
            throws Exception {
        zuper.call();

        TestContextHolder.INSTANCE.command(testContext -> {
            if (SystemCategory.class.equals(testContext.getTestCategory())) {
                testContext.computeIfAbsent(SERVICE_INSTANCE, key -> {
                    ServiceProvider<InjectionManager> serviceProvider =
                            ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class,
                                    JerseyServiceProvider.class);

                    return serviceProvider.configure(testContext, injectionManager);
                });
            }
        });
    }

}
