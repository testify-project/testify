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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import static org.glassfish.hk2.api.ServiceLocatorState.RUNNING;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.BuilderHelper;
import static org.glassfish.hk2.utilities.BuilderHelper.createContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameAndContractFilter;
import static org.glassfish.hk2.utilities.BuilderHelper.createNameFilter;
import org.glassfish.hk2.utilities.NamedImpl;
import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.addOneConstant;
import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.removeFilter;
import org.testifyproject.ServiceInstance;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Scan;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * An HK2 DI implementation of {@link ServiceInstance} SPI contract. This class
 * provides the ability to work with HK2 {@link ServiceLocator} to create,
 * locate, and manage services.
 *
 * @author saden
 */
public class HK2ServiceInstance implements ServiceInstance {

    public static final Set<Class<? extends Annotation>> INJECT_ANNOTATIONS;
    public static final Set<Class<? extends Annotation>> NAME_ANNOTATIONS;
    public static final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER;

    static {
        INJECT_ANNOTATIONS = ImmutableSet.of(Inject.class, Real.class);
        NAME_ANNOTATIONS = ImmutableSet.of(Named.class);
        CUSTOM_QUALIFIER = ImmutableSet.of(Qualifier.class);
    }

    private final ServiceLocator locator;

    public HK2ServiceInstance(ServiceLocator locator) {
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
        if (locator.getState() == RUNNING) {
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

        IndexedFilter filter = BuilderHelper.createContractFilter(instanceType.getTypeName());
        removeFilter(locator, filter, true);

        if (name != null && contract != null) {
            removeFilter(locator, createNameAndContractFilter(contract.getName(), name), true);
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
        try {
            DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
            DynamicConfiguration config = dcs.createDynamicConfiguration();

            for (Module module : modules) {
                Binder binder = (Binder) module.value().newInstance();
                binder.bind(config);
            }

            config.commit();
        } catch (MultiException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Could not add the module to the service instance", e);
        }
    }

    @Override
    public void addScans(Scan... scans) {
        try {
            DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            Populator populator = dcs.getPopulator();

            for (Scan scan : scans) {
                HK2DescriptorPopulator descriptorPopulator = new HK2DescriptorPopulator(scan.value());
                populator.populate(descriptorPopulator);
            }

            dc.commit();
        } catch (IOException | MultiException e) {
            throw new IllegalStateException("Could not populate service instance", e);
        }
    }

    @Override
    public Set<Class<? extends Annotation>> getInjectionAnnotations() {
        return INJECT_ANNOTATIONS;
    }

    @Override
    public Set<Class<? extends Annotation>> getNameQualifers() {
        return NAME_ANNOTATIONS;
    }

    @Override
    public Set<Class<? extends Annotation>> getCustomQualifiers() {
        return CUSTOM_QUALIFIER;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.locator);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HK2ServiceInstance other = (HK2ServiceInstance) obj;

        return Objects.equals(this.locator, other.locator);
    }

    @Override
    public String toString() {
        return "HK2ServiceInstance{" + "locator=" + locator + '}';
    }

}
