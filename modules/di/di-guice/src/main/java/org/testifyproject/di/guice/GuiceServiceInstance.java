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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.inject.Qualifier;

import org.testifyproject.ServiceInstance;
import org.testifyproject.guava.common.collect.ImmutableSet;

import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A Google Guice DI implementation of the {@link ServiceInstance} spi contract. This class
 * provides the ability to work with Google Guice {@link Injector} to create, locate, and manage
 * services.
 *
 * @author saden
 */
@ToString(of = "injector")
@EqualsAndHashCode(of = "injector")
public class GuiceServiceInstance implements ServiceInstance {

    private static final Set<Class<? extends Annotation>> NAME_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER;

    static {
        NAME_ANNOTATIONS = ImmutableSet.of(javax.inject.Named.class, Named.class);
        CUSTOM_QUALIFIER = ImmutableSet.of(Qualifier.class, BindingAnnotation.class);
    }

    private final Injector injector;

    public GuiceServiceInstance(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Boolean isRunning() {
        return true;
    }

    @Override
    public void inject(Object instance) {
        //XXX: should we throw an exception if the injector is not running?
        if (isRunning()) {
            injector.injectMembers(instance);
        }
    }

    @Override
    public <T> T getContext() {
        return (T) injector;
    }

    @Override
    public <T> T getService(Type type, String name) {
        return (T) injector.getInstance(Key.get(type, Names.named(name)));
    }

    @Override
    public <T> T getService(Type type, Annotation... qualifiers) {
        if (qualifiers == null || qualifiers.length == 0) {
            return (T) injector.getInstance(Key.get(type));
        } else {
            return (T) injector.getInstance(Key.get(type, qualifiers[0]));
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
