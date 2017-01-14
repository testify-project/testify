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
package org.testify.di.guice;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import static com.google.inject.util.Modules.combine;
import static com.google.inject.util.Modules.override;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import org.testify.Instance;
import org.testify.ServiceInstance;
import org.testify.annotation.Fixture;
import org.testify.annotation.Real;
import org.testify.core.impl.DefaultInstance;
import org.testify.guava.common.collect.ImmutableSet;

/**
 * A Google Guice DI implementation of the {@link ServiceInstance} spi contract.
 * This class provides the ability to work with Google Guice {@link Injector} to
 * create, locate, and manage services.
 *
 * @author saden
 */
public class GuiceServiceInstance implements ServiceInstance {

    public static final Set<Class<? extends Annotation>> INJECT_ANNOTATIONS;
    public static final Set<Class<? extends Annotation>> NAME_ANNOTATIONS;
    public static final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER;

    static {
        INJECT_ANNOTATIONS = ImmutableSet.of(Inject.class, com.google.inject.Inject.class, Real.class);
        NAME_ANNOTATIONS = ImmutableSet.of(Named.class, com.google.inject.name.Named.class);
        CUSTOM_QUALIFIER = ImmutableSet.of(Qualifier.class, BindingAnnotation.class);
    }

    private final Queue<Instance> constants = new ConcurrentLinkedQueue<>();
    private final Queue<Instance> replacements = new ConcurrentLinkedQueue<>();
    private final Queue<Module> modules = new ConcurrentLinkedQueue<>();
    private final Queue<Module> testModules = new ConcurrentLinkedQueue<>();
    private transient boolean running = false;
    private Injector injector;

    public GuiceServiceInstance(Injector injector) {
        this.injector = injector;
    }

    @Override
    public boolean isRunning() {
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
    public <T> T getService(Type type, Annotation... qualifiers) {
        updateInjector();
        if (qualifiers == null || qualifiers.length == 0) {
            return (T) injector.getInstance(Key.get(type));
        } else {
            return (T) injector.getInstance(Key.get(type, qualifiers[0]));
        }
    }

    private synchronized void updateInjector() {
        if (running) {
            return;
        }

        AbstractModule constantModule = new AbstractModule() {
            @Override
            protected void configure() {
                while (constants.peek() != null) {
                    Instance constant = constants.poll();
                    Object instance = constant.getInstance();
                    Class instanceType = instance.getClass();
                    Optional<String> name = constant.getName();
                    Optional<Class> contract = constant.getContract();

                    if (name.isPresent() && contract.isPresent()) {
                        bind(Key.get(contract.get(), Names.named(name.get()))).toInstance(instance);
                        bind(Key.get(instanceType, Names.named(name.get()))).toInstance(instance);
                    } else if (name.isPresent()) {
                        bind(instanceType).annotatedWith(Names.named(name.get())).toInstance(instance);
                    } else if (contract.isPresent()) {
                        bind(contract.get()).toInstance(instance);
                    } else {
                        bind(instanceType).toInstance(instance);
                    }
                }
            }
        };

        AbstractModule replacementModule = new AbstractModule() {
            @Override
            protected void configure() {
                while (replacements.peek() != null) {
                    Instance constant = replacements.poll();
                    Object instance = constant.getInstance();
                    Class instanceType = instance.getClass();
                    Optional<String> name = constant.getName();
                    Optional<Class> contract = constant.getContract();

                    if (name.isPresent() && contract.isPresent()) {
                        bind(Key.get(contract.get(), Names.named(name.get()))).toInstance(instance);
                        bind(Key.get(instanceType, Names.named(name.get()))).toInstance(instance);
                    } else if (name.isPresent()) {
                        bind(instanceType).annotatedWith(Names.named(name.get())).toInstance(instance);
                    } else if (contract.isPresent()) {
                        bind(contract.get()).toInstance(instance);
                    } else {
                        bind(instanceType).toInstance(instance);
                        for (Class type : instanceType.getInterfaces()) {
                            bind(type).toInstance(instance);
                        }
                    }
                }
            }
        };

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

    @Override
    public void addConstant(Object instance, String name, Class contract) {
        constants.add(DefaultInstance.of(instance, name, contract));
    }

    @Override
    public void replace(Object instance, String name, Class contract) {
        replacements.add(DefaultInstance.of(instance, name, contract));
    }

    @Override
    public void addModules(org.testify.annotation.Module... modules) {
        try {
            for (org.testify.annotation.Module module : modules) {
                Class<?> value = module.value();
                Module instance = (Module) value.newInstance();
                if (value.isAnnotationPresent(Fixture.class)) {
                    testModules.add(instance);
                } else {
                    this.modules.add(instance);
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException("Could not create an instance of the module", e);
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

}
