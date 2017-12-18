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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Qualifier;

import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.guava.common.reflect.TypeToken;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An HK2 DI implementation of {@link ServiceInstance} SPI contract. This class provides the
 * ability to work with Jersey {@link InjectionManager} to create, locate, and manage services.
 *
 * @author saden
 */
@ToString(of = "injectionManager")
@EqualsAndHashCode(of = "injectionManager")
public class JerseyServiceInstance implements ServiceInstance {

    private static final Set<Class<? extends Annotation>> NAME_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER;

    static {
        NAME_ANNOTATIONS = ImmutableSet.of(Named.class);
        CUSTOM_QUALIFIER = ImmutableSet.of(Qualifier.class);
    }

    private final TestContext testContext;
    private final InjectionManager injectionManager;

    public JerseyServiceInstance(TestContext testContext, InjectionManager injectionManager) {
        this.testContext = testContext;
        this.injectionManager = injectionManager;
    }

    @Override
    public Boolean isRunning() {
        return true;
    }

    @Override
    public void inject(Object instance) {
        if (isRunning()) {
            injectionManager.inject(instance);
        }
    }

    @Override
    public void destroy() {
        if (isRunning()) {
            injectionManager.shutdown();
        }
    }

    @Override
    public InjectionManager getContext() {
        return injectionManager;
    }

    @Override
    public <T> T getService(Type type, String name) {
        return (T) getService(type, new NamedImpl(name));
    }

    @Override
    public <T> T getService(Type type, Annotation... qualifiers) {
        Object instance;
        Class<?> rawType = TypeToken.of(type).getRawType();

        if (qualifiers == null || qualifiers.length == 0) {
            instance = injectionManager.getInstance(type);

            if (instance == null) {
                instance = injectionManager.getInstance(rawType);
            }
        } else {
            instance = injectionManager.getInstance(rawType, qualifiers);
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
