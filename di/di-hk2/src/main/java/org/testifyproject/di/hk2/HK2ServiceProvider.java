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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.internal.ServiceLocatorImpl;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.tools.Discoverable;

/**
 * An HK2 implementation of the {@link ServiceProvider} contract.
 *
 * @author saden
 */
@Discoverable
public class HK2ServiceProvider implements ServiceProvider<ServiceLocator> {

    @Override
    public ServiceLocator create(TestContext testContext) {
        ServiceLocatorFactory locatorFactory = ServiceLocatorFactory.getInstance();

        ServiceLocator serviceLocator = locatorFactory.create(testContext.getName());
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
        ServiceLocatorUtilities.enableImmediateScopeSuspended(serviceLocator);
        ServiceLocatorUtilities.enableInheritableThreadScope(serviceLocator);
        ServiceLocatorUtilities.enableLookupExceptions(serviceLocator);

        enableExtras("enableDefaultInterceptorServiceImplementation", serviceLocator);
        enableExtras("enableOperations", serviceLocator);
        enableExtras("enableTopicDistribution", serviceLocator);

        return serviceLocator;
    }

    @Override
    public ServiceInstance configure(TestContext testContext, ServiceLocator serviceLocator) {
        HK2ServiceInstance serviceInstance = new HK2ServiceInstance(testContext, serviceLocator);

        ServiceLocatorImpl serviceLocatorImpl = (ServiceLocatorImpl) serviceLocator;
        HK2InjectionResolver hK2InjectionResolver = new HK2InjectionResolver(testContext, serviceLocatorImpl);
        ServiceLocatorUtilities.addOneConstant(serviceLocator, hK2InjectionResolver);

        return serviceInstance;
    }

    @Override
    public void postConfigure(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        testDescriptor.getModules().stream()
                .forEach(serviceInstance::addModules);

        testDescriptor.getScans().stream()
                .forEach(serviceInstance::addScans);
    }

    void enableExtras(String methodName, ServiceLocator serviceLocator) {
        String className = "org.glassfish.hk2.extras.ExtrasUtilities";

        try {
            Class<?> extrasUtilitiesClass = Class.forName(className);
            Method method = extrasUtilitiesClass.getMethod(methodName, ServiceLocator.class);
            method.invoke(null, serviceLocator);
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | SecurityException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            LoggingUtil.INSTANCE.debug("Method '{}' not found in class {}", methodName, className, e);
        }
    }
}
