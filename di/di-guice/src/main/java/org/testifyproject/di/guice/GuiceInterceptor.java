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
package org.testifyproject.di.guice;

import static com.google.inject.util.Modules.override;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.testifyproject.Instance;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestDescriptor;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Argument;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.ProxyInstanceController;
import org.testifyproject.extension.annotation.IntegrationCategory;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.internal.InternalInjectorCreator;

/**
 * Guice operation interceptor. This class intercepts certain Guice initialization calls to
 * configure the test case.
 *
 * @author saden
 */
public class GuiceInterceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object)
            throws Exception {
        return zuper.call();
    }

    public Injector createInjector(@SuperCall Callable<Injector> zuper,
            @Argument(0) Stage stage,
            @Argument(1) Iterable<? extends Module> modules)
            throws Exception {
        Injector result = TestContextHolder.INSTANCE.query(testContext -> {
            TestDescriptor testDescriptor = testContext.getTestDescriptor();
            Queue<Module> prodModules = new ConcurrentLinkedQueue<>();
            Queue<Module> testModules = new ConcurrentLinkedQueue<>();

            modules.forEach(prodModules::add);

            testDescriptor.getModules().forEach(module -> {
                Module instance = (Module) ReflectionUtil.INSTANCE.newInstance(module.value());

                if (module.test()) {
                    testModules.add(instance);
                } else {
                    prodModules.add(instance);
                }
            });

            ConcurrentLinkedDeque<Instance> instances = new ConcurrentLinkedDeque<>();

            ServiceLocatorUtil.INSTANCE.findAllWithFilter(InstanceProvider.class,
                    IntegrationCategory.class)
                    .stream()
                    .map(p -> p.get(testContext))
                    .flatMap(p -> p.stream())
                    .forEach(p -> instances.addLast(p));

            ServiceLocatorUtil.INSTANCE.findAllWithFilter(ProxyInstanceController.class)
                    .stream()
                    .map(p -> p.create(testContext))
                    .flatMap(p -> p.stream())
                    .forEach(p -> instances.addLast(p));

            Module instanceModule = GuiceAbstractModule.of(instances);

            //override prod modules with test modules to insure services defined in test modules
            //take precedence over prod modules
            Module testModuleSet = override(prodModules).with(testModules);

            //override test module set with instance module to insure services defined by
            //instance providers take precedence over prod ad test modules
            Module finalModuleSet = override(testModuleSet).with(instanceModule);

            //create a guice injector
            Injector injector = new InternalInjectorCreator()
                    .stage(Stage.DEVELOPMENT)
                    .addModules(Arrays.asList(finalModuleSet))
                    .build();

            //add service instance to the test context
            ServiceInstance serviceInstance = new GuiceServiceInstance(injector);
            testContext.addProperty(TestContextProperties.SERVICE_INSTANCE, serviceInstance);

            return injector;
        });

        if (result == null) {
            result = zuper.call();
        }

        return result;
    }

}
