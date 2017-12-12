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
package org.testifyproject.di.jersey;

import static org.glassfish.hk2.utilities.BuilderHelper.createContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameAndContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameFilter;
import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.removeFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.jersey.inject.hk2.ImmediateHk2InjectionManager;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.testifyproject.Instance;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestifyException;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.Scan;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.ProxyInstanceController;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * An HK2 implementation of the {@link ServiceProvider} contract.
 *
 * @author saden
 */
@IntegrationCategory
@SystemCategory
@Discoverable
public class JerseyServiceProvider implements ServiceProvider<InjectionManager> {

    @Override
    public InjectionManager create(TestContext testContext) {
        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        InjectionManager injectionManager = Injections.createInjectionManager();

        return testConfigurer.configure(testContext, injectionManager);
    }

    @Override
    public ServiceInstance configure(TestContext testContext, InjectionManager injectionManager) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        ClassLoader classLoader = testDescriptor.getTestClassLoader();

        JerseyInjectionResolver hK2InjectionResolver =
                new JerseyInjectionResolver(testContext, injectionManager);

        InstanceBinding<JerseyInjectionResolver> resolverBinding = Bindings
                .service(hK2InjectionResolver)
                .to(InjectionResolver.class)
                .in(Singleton.class);

        injectionManager.register(resolverBinding);

        addModules(injectionManager, testDescriptor);
        addScans(injectionManager, testDescriptor, classLoader);
        addInstances(injectionManager, testContext);

        return new JerseyServiceInstance(testContext, injectionManager);
    }

    void addScans(InjectionManager injectionManager,
            TestDescriptor testDescriptor,
            ClassLoader classLoader)
            throws TestifyException {
        try {
            DynamicConfigurationService dcs =
                    injectionManager.getInstance(DynamicConfigurationService.class);
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            Populator populator = dcs.getPopulator();

            for (Scan scan : testDescriptor.getScans()) {
                JerseyDescriptorPopulator descriptorPopulator =
                        new JerseyDescriptorPopulator(classLoader, scan.value());
                populator.populate(descriptorPopulator);
            }

            dc.commit();
        } catch (IOException | MultiException e) {
            throw ExceptionUtil.INSTANCE.propagate("Could not populate service instance",
                    e);
        }
    }

    void addModules(InjectionManager injectionManager, TestDescriptor testDescriptor)
            throws MultiException {
        testDescriptor.getModules()
                .stream()
                .map(p -> ReflectionUtil.INSTANCE.newInstance(p.value()))
                .forEachOrdered(binder -> {
                    if (binder instanceof Binder) {
                        injectionManager.register((Binder) binder);
                    } else {
                        injectionManager.register(binder);
                    }
                });

    }

    private void addInstances(InjectionManager injectionManager, TestContext testContext) {
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

        removeInstances(injectionManager, instances);
        Binder binder = JerseyAbstractBinder.of(instances);

        injectionManager.register(binder);

    }

    void removeInstances(InjectionManager injectionManager,
            ConcurrentLinkedDeque<Instance<?>> instances) {
        ServiceLocator serviceLocator = ((ImmediateHk2InjectionManager) injectionManager)
                .getServiceLocator();

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

}
