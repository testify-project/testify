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
package org.testify.di.hk2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import static org.glassfish.hk2.api.DescriptorFileFinder.RESOURCE_BASE;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.internal.ServiceLocatorImpl;
import org.testify.ServiceInstance;
import org.testify.ServiceProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.annotation.Module;
import org.testify.annotation.Scan;
import org.testify.tools.Discoverable;

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

        enableExtras(testContext, "enableDefaultInterceptorServiceImplementation", serviceLocator);
        enableExtras(testContext, "enableOperations", serviceLocator);
        enableExtras(testContext, "enableTopicDistribution", serviceLocator);

        try {
            DynamicConfigurationService dcs = serviceLocator.getService(DynamicConfigurationService.class);
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            Populator populator = dcs.getPopulator();

            String defaultResource = RESOURCE_BASE + "default";
            ClassLoader classLoader = testContext.getTestDescriptor().getTestClassLoader();
            HK2DescriptorPopulator testPopulator = new HK2DescriptorPopulator(classLoader, defaultResource);
            populator.populate(testPopulator);
            dc.commit();
        } catch (MultiException | IOException e) {
            throw new IllegalStateException("Could not create HK2 Service Locator", e);
        }

        return serviceLocator;
    }

    @Override
    public ServiceInstance configure(TestContext testContext, ServiceLocator serviceLocator) {
        HK2ServiceInstance serviceInstance = new HK2ServiceInstance(serviceLocator);

        ServiceLocatorImpl serviceLocatorImpl = (ServiceLocatorImpl) serviceLocator;
        HK2InjectionResolver hK2InjectionResolver = new HK2InjectionResolver(testContext, serviceLocatorImpl);
        ServiceLocatorUtilities.addOneConstant(serviceLocator, hK2InjectionResolver);

        return serviceInstance;
    }

    private void enableExtras(TestContext testContext, String methodName, ServiceLocator serviceLocator) {
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
            testContext.debug("Method '{}' not found in class {}", methodName, className, e);
        }
    }

    @Override
    public void postConfigure(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        List<Module> modules = testDescriptor.getModules();

        if (!modules.isEmpty()) {
            modules.stream().forEach(serviceInstance::addModules);
        }

        List<Scan> scans = testDescriptor.getScans();

        if (!scans.isEmpty()) {
            scans.stream().forEach(serviceInstance::addScans);
        }
    }

}
