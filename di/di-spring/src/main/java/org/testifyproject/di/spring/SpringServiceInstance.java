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
package org.testifyproject.di.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.inject.Named;
import javax.inject.Provider;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.BeansException;
import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.testifyproject.ServiceInstance;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.guava.common.collect.ImmutableSet;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * A Spring DI implementation of {@link ServiceInstance} SPI contract. This class provides the
 * ability to work with Spring {@link ConfigurableApplicationContext} to create, locate, and manage
 * services.
 *
 * @author saden
 */
@ToString(of = "context")
@EqualsAndHashCode(of = "context")
public class SpringServiceInstance implements ServiceInstance {

    private static final Set<Class<? extends Annotation>> NAME_QUALIFIER;
    private static final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER;

    static {
        NAME_QUALIFIER = ImmutableSet.of(Named.class, Qualifier.class);
        CUSTOM_QUALIFIER = ImmutableSet.of(javax.inject.Qualifier.class, Qualifier.class);
    }

    private final ConfigurableApplicationContext context;

    public SpringServiceInstance(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Override
    public Boolean isRunning() {
        return context.isActive() && context.isRunning();
    }

    @Override
    public void inject(Object instance) {
        if (isRunning()) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
            beanFactory.autowireBean(instance);
        }
    }

    @Override
    public void destroy() {
        if (isRunning()) {
            context.close();
        }
    }

    @Override
    public void init() {
        if (!context.isActive()) {
            context.refresh();
        }
    }

    @Override
    public ConfigurableApplicationContext getContext() {
        return context;
    }

    @Override
    public <T> T getService(Type type, String name) {
        TypeToken<?> token = TypeToken.of(type);
        Class rawType = token.getRawType();

        //if the desired type is the application context itself then return it
        if (token.isSupertypeOf(context.getClass())) {
            return (T) context;
        }

        return (T) context.getBean(name, rawType);
    }

    @Override
    public <T> T getService(Type type, Annotation... qualifiers) {
        TypeToken<?> token = TypeToken.of(type);
        Class rawType = token.getRawType();

        //if the desired type is the application context itself then return it
        if (token.isSupertypeOf(context.getClass())) {
            return (T) context;
        }

        Object instance = null;

        if (qualifiers == null || qualifiers.length == 0) {
            if (token.isSubtypeOf(Provider.class)) {
                rawType = token.resolveType(Provider.class.getTypeParameters()[0]).getRawType();
                instance = SpringDefaultProvider.of(this, rawType);
            } else if (token.isSubtypeOf(Optional.class)) {
                rawType = token.resolveType(Optional.class.getTypeParameters()[0]).getRawType();
                instance = Optional.ofNullable(context.getBean(rawType));
            } else if (token.isSubtypeOf(Map.class)) {
                rawType = token.resolveType(Map.class.getTypeParameters()[1]).getRawType();
                instance = context.getBeansOfType(rawType);
            } else if (token.isSubtypeOf(Set.class)) {
                rawType = token.resolveType(Set.class.getTypeParameters()[0]).getRawType();
                instance = context.getBeansOfType(rawType)
                        .values()
                        .stream()
                        .collect(toSet());
            } else if (token.isSubtypeOf(List.class)) {
                rawType = token.resolveType(List.class.getTypeParameters()[0]).getRawType();
                instance = context.getBeansOfType(rawType)
                        .values()
                        .stream()
                        .collect(toList());
            } else if (rawType.isInterface()) {
                try {
                    instance = context.getBean(rawType);
                } catch (BeansException e) {
                    LoggingUtil.INSTANCE.debug("Could not find bean", e);
                    //we could find the bean by its raw type. maybe this bean is
                    //a dynamically created bean so lets try another method to
                    //find the bean by looking at all possible beans of that type
                    //and returning the first one.
                    Optional result = context.getBeansOfType(rawType)
                            .values()
                            .stream()
                            .findFirst();

                    if (result.isPresent()) {
                        instance = result.get();
                    }
                }

            } else {
                instance = context.getBean(rawType);
            }
        } else {
            Annotation annotation = qualifiers[0];
            Class<? extends Annotation> annotationType = annotation.annotationType();
            String[] beanNames = context.getBeanNamesForAnnotation(annotationType);

            for (String beanName : beanNames) {
                Class<?> beanType = context.getType(beanName);
                if (token.isSupertypeOf(beanType)) {
                    instance = context.getBean(beanName, beanType);
                    break;
                }
            }
        }

        return (T) instance;
    }

    @Override
    public void addConstant(Object instance, String name, Class contract) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        Class instanceType = instance.getClass();

        if (name != null) {
            beanFactory.registerSingleton(name, instance);
        } else {
            beanFactory.registerSingleton(instanceType.getSimpleName(), instance);
        }
    }

    @Override
    public void replace(Object instance, String name, Class contract) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        Class instanceType = instance.getClass();

        if (contract != null) {
            //XXX: find and remove all the beans that implment the given contract
            String[] contractBeanNames = beanNamesForTypeIncludingAncestors(beanFactory, contract, true, false);
            for (String beanName : contractBeanNames) {
                beanFactory.removeBeanDefinition(beanName);
            }
        }

        //XXX: find and remove all the beans of the given instance type
        String[] typeBeanNames = beanNamesForTypeIncludingAncestors(beanFactory, instanceType, true, false);
        for (String beanName : typeBeanNames) {
            beanFactory.removeBeanDefinition(beanName);
        }

        if (name != null && beanFactory.containsBean(name)) {
            beanFactory.removeBeanDefinition(name);
        }

        addConstant(instance, name, contract);
    }

    @Override
    public void addModules(Module... modules) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
        AnnotatedBeanDefinitionReader annotatedBeanDefinitionReader = new AnnotatedBeanDefinitionReader(registry);

        for (Module module : modules) {
            annotatedBeanDefinitionReader.registerBean(module.value());
        }
    }

    @Override
    public void addScans(Scan... scans) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context;
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        BeanDefinitionDefaults beanDefinitionDefaults = scanner.getBeanDefinitionDefaults();
        beanDefinitionDefaults.setLazyInit(true);

        for (Scan scan : scans) {
            scanner.scan(scan.value());
        }
    }

    @Override
    public Set<Class<? extends Annotation>> getNameQualifers() {
        return NAME_QUALIFIER;
    }

    @Override
    public Set<Class<? extends Annotation>> getCustomQualifiers() {
        return CUSTOM_QUALIFIER;
    }

}
