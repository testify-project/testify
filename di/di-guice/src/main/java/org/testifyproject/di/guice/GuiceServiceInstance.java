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

import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import static com.google.inject.util.Modules.combine;
import static com.google.inject.util.Modules.override;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Qualifier;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.Instance;
import org.testifyproject.ServiceInstance;
import org.testifyproject.annotation.Fixture;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.guava.common.collect.ImmutableSet;

/**
 * A Google Guice DI implementation of the {@link ServiceInstance} spi contract. This class provides the ability to work
 * with Google Guice {@link Injector} to create, locate, and manage services.
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

    private final Queue<Instance> constants = new ConcurrentLinkedQueue<>();
    private final Queue<Instance> replacements = new ConcurrentLinkedQueue<>();
    private final Queue<Module> modules = new ConcurrentLinkedQueue<>();
    private final Queue<Module> testModules = new ConcurrentLinkedQueue<>();
    private boolean running = false;
    private Injector injector;

    public GuiceServiceInstance(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Boolean isRunning() {
        return running;
    }

    @Override
    public void inject(Object instance) {
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
        updateInjector();

        return (T) injector.getInstance(Key.get(type, Names.named(name)));
    }

    @Override
    public <T> T getService(Type type, Annotation... qualifiers) {
        updateInjector();

        if (qualifiers == null || qualifiers.length == 0) {
            return (T) injector.getInstance(Key.get(type));
        } else {
            return (T) injector.getInstance(Key.get(type, qualifiers[0]));
        }
    }

    @Override
    public void addConstant(Object instance, String name, Class contract) {
        constants.add(DefaultInstance.of(instance, name, contract));
    }

    @Override
    public void replace(Object instance, String name, Class contract) {
        replacements.add(DefaultInstance.of(instance, name, contract));
    }

    @Override
    public void addModules(org.testifyproject.annotation.Module... modules) {
        for (org.testifyproject.annotation.Module module : modules) {
            Class<?> value = module.value();
            Module instance = (Module) ReflectionUtil.INSTANCE.newInstance(value);

            if (value.isAnnotationPresent(Fixture.class)) {
                testModules.add(instance);
            } else {
                this.modules.add(instance);
            }
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

    synchronized void updateInjector() {
        if (!running) {

            Module constantModule = GuiceAbstractModule.of(constants);
            Module replacementModule = GuiceAbstractModule.of(replacements);

            //Combine the modules defined in the test class with testify's constant
            //modules, then replace binding definitions with testify replacements
            //and the modules designated as test modules.
            Module serviceModule = combine(combine(modules), constantModule);
            Module testModule = combine(testModules);
            Module module = override(serviceModule).with(replacementModule, testModule);

            //create a guice injector
            injector = Guice.createInjector(Stage.DEVELOPMENT, module);
            running = true;
        }
    }
}
