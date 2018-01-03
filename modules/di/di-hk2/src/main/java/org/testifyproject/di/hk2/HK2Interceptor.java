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
package org.testifyproject.di.hk2;

import static org.glassfish.hk2.utilities.BuilderHelper.createContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameAndContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameFilter;
import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.removeFilter;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.testifyproject.Instance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.Scan;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Argument;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.ProxyInstanceController;

/**
 * HK2 Service Locator operation interceptor. This class intercepts certain HK2 initialization
 * calls to configure the test case.
 *
 * @author saden
 */
public class HK2Interceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object)
            throws Exception {
        return zuper.call();
    }

    public ServiceLocator create(@SuperCall Callable<ServiceLocator> zuper,
            @Argument(0) String name,
            @Argument(1) ServiceLocator parent,
            @Argument(2) ServiceLocatorGenerator generator,
            @Argument(3) ServiceLocatorFactory.CreatePolicy policy)
            throws Exception {
        ServiceLocator serviceLocator = zuper.call();
        String locatorName = serviceLocator.getName();

        TestContextHolder.INSTANCE.command(testContext -> {
            if (testContext.getName().equals(locatorName)) {
                testContext.computeIfAbsent(SERVICE_INSTANCE, key -> {
                    TestDescriptor testDescriptor = testContext.getTestDescriptor();
                    ClassLoader classLoader = testDescriptor.getTestClassLoader();

                    HK2ServiceInstance serviceInstance =
                            new HK2ServiceInstance(testContext, serviceLocator);

                    HK2InjectionResolver hK2InjectionResolver =
                            new HK2InjectionResolver(testContext, serviceLocator);
                    ServiceLocatorUtilities.addOneConstant(serviceLocator, hK2InjectionResolver);

                    ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
                    ServiceLocatorUtilities.enableImmediateScopeSuspended(serviceLocator);
                    ServiceLocatorUtilities.enableInheritableThreadScope(serviceLocator);
                    ServiceLocatorUtilities.enableLookupExceptions(serviceLocator);

                    enableExtras("enableDefaultInterceptorServiceImplementation", serviceLocator);
                    enableExtras("enableOperations", serviceLocator);
                    enableExtras("enableTopicDistribution", serviceLocator);

                    addModules(serviceLocator, testDescriptor);
                    addScans(serviceLocator, testDescriptor, classLoader);
                    addInstances(serviceLocator, testContext);

                    ServiceProvider<ServiceLocator> serviceProvider =
                            ServiceLocatorUtil.INSTANCE
                                    .getOne(ServiceProvider.class, HK2ServiceProvider.class);

                    return serviceProvider.configure(testContext, serviceLocator);
                });
            }

        });

        return serviceLocator;
    }

    void addScans(ServiceLocator serviceLocator,
            TestDescriptor testDescriptor,
            ClassLoader classLoader)
            throws TestifyException {
        try {
            DynamicConfigurationService dcs =
                    serviceLocator.getService(DynamicConfigurationService.class);
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            Populator populator = dcs.getPopulator();

            for (Scan scan : testDescriptor.getScans()) {
                HK2DescriptorPopulator descriptorPopulator =
                        new HK2DescriptorPopulator(classLoader, scan.value());
                populator.populate(descriptorPopulator);
            }

            dc.commit();
        } catch (IOException | MultiException e) {
            throw ExceptionUtil.INSTANCE.propagate("Could not populate service instance",
                    e);
        }
    }

    void addModules(ServiceLocator serviceLocator, TestDescriptor testDescriptor)
            throws MultiException {
        DynamicConfigurationService dcs =
                serviceLocator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();

        testDescriptor.getModules().stream()
                .map(p -> ReflectionUtil.INSTANCE.newInstance(p.value()))
                .map(Binder.class::cast)
                .forEachOrdered(binder -> {
                    binder.bind(config);
                });

        config.commit();
    }

    private void addInstances(ServiceLocator serviceLocator, TestContext testContext) {
        ConcurrentLinkedDeque<Instance<?>> instances = new ConcurrentLinkedDeque<>();

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(InstanceProvider.class)
                .stream()
                .map(p -> p.get(testContext))
                .flatMap(p -> p.stream())
                .forEach(p -> instances.addLast(p));

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(ProxyInstanceController.class)
                .stream()
                .map(p -> p.create(testContext))
                .flatMap(p -> p.stream())
                .forEach(p -> instances.addLast(p));

        removeInstances(serviceLocator, instances);
        Binder instanceModule = HK2AbstractBinder.of(instances);

        DynamicConfigurationService dcs =
                serviceLocator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();

        instanceModule.bind(config);

        config.commit();
    }

    void removeInstances(ServiceLocator serviceLocator,
            ConcurrentLinkedDeque<Instance<?>> instances) {
        instances.forEach(instance -> {
            String name = instance.getName();
            Class contract = instance.getContract();

            Class instanceType = instance.getClass();

            IndexedFilter filter = BuilderHelper.createContractFilter(instanceType
                    .getTypeName());
            removeFilter(serviceLocator, filter, true);

            if (name != null && contract != null) {
                removeFilter(serviceLocator, createNameAndContractFilter(contract.getName(),
                        name), true);
                removeFilter(serviceLocator, createNameFilter(name), true);
                removeFilter(serviceLocator, createContractFilter(contract.getName()), true);
            } else if (name != null) {
                removeFilter(serviceLocator, createNameFilter(name), true);
            } else if (contract != null) {
                removeFilter(serviceLocator, createContractFilter(contract.getName()), true);
            }
        });
    }

    void enableExtras(String methodName, ServiceLocator serviceLocator) {
        String className = "org.glassfish.hk2.extras.ExtrasUtilities";

        try {
            Class<?> extrasUtilitiesClass = Class.forName(className);
            Method method = extrasUtilitiesClass.getMethod(methodName,
                    ServiceLocator.class);
            method.invoke(null, serviceLocator);
        } catch (ClassNotFoundException |
                NoSuchMethodException |
                SecurityException |
                IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e) {
            LoggingUtil.INSTANCE.debug("Method '{}' not found in class {}", methodName,
                    className, e);
        }
    }
}
