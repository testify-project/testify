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


import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;

/**
 * A class that intercepts methods of classes that extend
 * {@link org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer}.
 * This class is responsible for configuring the Spring application as well as extracting
 * information useful for test scaffolding.
 *
 * @author saden
 */
public class SpringApplicationInterceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object,
            @AllArguments Object[] args)
            throws Exception {
        return zuper.call();
    }


    @RuntimeType
    public Class<?>[] getServletConfigClasses(@SuperCall Callable<Class<?>[]> zuper,
            @This Object object) throws Exception {
        Class<?>[] result = zuper.call();

        return TestContextHolder.INSTANCE.query(testContext -> {
            TestDescriptor testDescriptor = testContext.getTestDescriptor();
            Collection<Module> modules = testDescriptor.getModules();

            Stream<Class<?>> testModules = modules.stream().map(Module::value);
            Stream<Class<?>> productionModules = Stream.empty();

            if (result != null) {
                productionModules = Stream.of(result);
            }

            return Stream.concat(testModules, productionModules).toArray(Class[]::new);
        });
    }

}
