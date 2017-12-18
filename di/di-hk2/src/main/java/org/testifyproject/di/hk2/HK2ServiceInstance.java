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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Qualifier;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.NamedImpl;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.guava.common.reflect.TypeToken;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An HK2 DI implementation of {@link ServiceInstance} SPI contract. This class provides the
 * ability to work with HK2 {@link ServiceLocator} to create, locate, and manage services.
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
    public Set<Class<? extends Annotation>> getNameQualifers() {
        return NAME_ANNOTATIONS;
    }

    @Override
    public Set<Class<? extends Annotation>> getCustomQualifiers() {
        return CUSTOM_QUALIFIER;
    }

}
