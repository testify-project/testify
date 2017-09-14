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

import static org.glassfish.hk2.api.ServiceLocatorState.RUNNING;
import static org.glassfish.hk2.utilities.BuilderHelper.createContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameAndContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameFilter;
import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.addOneConstant;
import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.removeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Qualifier;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.NamedImpl;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.guava.common.reflect.TypeToken;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An HK2 DI implementation of {@link ServiceInstance} SPI contract. This class provides
 * the ability to work with HK2 {@link ServiceLocator} to create, locate, and manage
 * services.
 *
 * @author saden
 */
@ToString(of = "locator")
@EqualsAndHashCode(of = "locator")
public class HK2ServiceInstance implements ServiceInstance {

    private static final Set<Class<? extends Annotation>> NAME_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER;

    static {
        NAME_ANNOTATIONS = ImmutableSet.of(Named.class);
        CUSTOM_QUALIFIER = ImmutableSet.of(Qualifier.class);
    }

    private final TestContext testContext;
    private final ServiceLocator locator;

    public HK2ServiceInstance(TestContext testContext, ServiceLocator locator) {
        this.testContext = testContext;
        this.locator = locator;
    }

    @Override
    public Boolean isRunning() {
        return locator.getState() == RUNNING;
    }

    @Override
    public void inject(Object instance) {
        if (isRunning()) {
            locator.inject(instance);
        }
    }

    @Override
    public void destroy() {
        if (isRunning()) {
            locator.shutdown();
        }
    }

    @Override
    public ServiceLocator getContext() {
        return locator;
    }

    @Override
    public <T> T getService(Type type, String name) {
        return locator.getService(type, new NamedImpl(name));
    }

    @Override
    public <T> T getService(Type type, Annotation... qualifiers) {
        Object instance;

        if (qualifiers == null || qualifiers.length == 0) {
            instance = locator.getService(type);

            if (instance == null) {
                instance = locator.getService(TypeToken.of(type).getRawType());
            }
        } else {
            instance = locator.getService(type, qualifiers);

            if (instance == null) {
                instance = locator.getService(TypeToken.of(type).getRawType(), qualifiers);
            }
        }

        return (T) instance;
    }

    @Override
    public void addConstant(Object instance, String name, Class contract) {
        Class instanceType = instance.getClass();

        if (name != null && contract != null) {
            addOneConstant(locator, instance, name, instanceType, contract);
            addOneConstant(locator, instance, null, instanceType, contract);
        } else if (name != null) {
            addOneConstant(locator, instance, name, instanceType);
            addOneConstant(locator, instance, null, instanceType);
        } else if (contract != null) {
            addOneConstant(locator, instance, null, instanceType, contract);
        } else {
            Class<?>[] interfaces = instanceType.getInterfaces();
            Class<?>[] contracts = Arrays.copyOf(interfaces, interfaces.length + 1);
            contracts[interfaces.length] = instanceType;

            addOneConstant(locator, instance, null, contracts);
        }

        addOneConstant(locator, instance);
    }

    @Override
    public void replace(Object instance, String name, Class contract) {
        Class instanceType = instance.getClass();

        IndexedFilter filter = BuilderHelper.createContractFilter(instanceType
                .getTypeName());
        removeFilter(locator, filter, true);

        if (name != null && contract != null) {
            removeFilter(locator, createNameAndContractFilter(contract.getName(), name),
                    true);
            removeFilter(locator, createNameFilter(name), true);
            removeFilter(locator, createContractFilter(contract.getName()), true);
        } else if (name != null) {
            removeFilter(locator, createNameFilter(name), true);
        } else if (contract != null) {
            removeFilter(locator, createContractFilter(contract.getName()), true);
        }

        addConstant(instance, name, contract);
    }

    @Override
    public void addModules(Module... modules) {
        DynamicConfigurationService dcs = locator.getService(
                DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();

        for (Module module : modules) {
            Binder binder = (Binder) ReflectionUtil.INSTANCE.newInstance(module.value());
            binder.bind(config);
        }

        config.commit();
    }

    @Override
    public void addScans(Scan... scans) {
        try {
            DynamicConfigurationService dcs = locator.getService(
                    DynamicConfigurationService.class);
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            Populator populator = dcs.getPopulator();
            ClassLoader classLoader = testContext.getTestDescriptor().getTestClassLoader();

            for (Scan scan : scans) {
                HK2DescriptorPopulator descriptorPopulator = new HK2DescriptorPopulator(
                        classLoader, scan.value());
                populator.populate(descriptorPopulator);
            }

            dc.commit();
        } catch (IOException | MultiException e) {
            throw ExceptionUtil.INSTANCE.propagate("Could not populate service instance",
                    e);
        }
    }

    @Override
    public Set<Class<? extends Annotation>> getNameQualifers() {
        return NAME_ANNOTATIONS;
    }

    @Override
    public Set<Class<? extends Annotation>> getCustomQualifiers() {
        return CUSTOM_QUALIFIER;
    }

}
