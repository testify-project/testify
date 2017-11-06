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

import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.testifyproject.Instance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.bytebuddy.implementation.bind.annotation.SuperCall;
import org.testifyproject.bytebuddy.implementation.bind.annotation.This;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.ProxyInstanceController;

/**
 * TODO.
 *
 * @author saden
 */
public class ApplicationContextInterceptor {

    @RuntimeType
    @BindingPriority(Integer.MAX_VALUE)
    public Object anyMethod(
            @SuperCall Callable<?> zuper,
            @This(optional = true) Object object,
            @AllArguments Object[] args)
            throws Exception {
        return zuper.call();
    }

    public void refresh(
            @SuperCall Callable<Void> zuper,
            @This ConfigurableApplicationContext applicationContext,
            @AllArguments Object[] args) throws Exception {
        TestContextHolder.INSTANCE.execute(testContext -> {
            if (applicationContext instanceof AnnotationConfigApplicationContext) {
                AnnotationConfigApplicationContext configContext =
                        (AnnotationConfigApplicationContext) applicationContext;
                configContext.setId(testContext.getName());
                configContext.setDisplayName(testContext.getName());
                configContext.setAllowCircularReferences(false);
                configContext.setAllowBeanDefinitionOverriding(true);
            }

            SpringBeanFactoryPostProcessor postProcessor =
                    new SpringBeanFactoryPostProcessor(testContext);

            applicationContext.addBeanFactoryPostProcessor(postProcessor);

            TestConfigurer testConfigurer = testContext.getTestConfigurer();
            ConfigurableApplicationContext configuredApplicationContext =
                    testConfigurer.configure(testContext, applicationContext);

            TestDescriptor testDescriptor = testContext.getTestDescriptor();
            DefaultListableBeanFactory beanFactory =
                    (DefaultListableBeanFactory) applicationContext.getBeanFactory();
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;

            addModules(testDescriptor, registry);
            addScans(testDescriptor, registry);

            Queue<Instance<?>> instances = getInstances(testContext);

            removeInstances(instances, beanFactory);
            addInstances(instances, beanFactory);

            testContext.computeIfAbsent(SERVICE_INSTANCE, key -> {
                ServiceProvider<ConfigurableApplicationContext> serviceProvider =
                        ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class,
                                SpringServiceProvider.class);

                return serviceProvider.configure(testContext, configuredApplicationContext);
            });
        });

        zuper.call();
    }

    Queue<Instance<?>> getInstances(TestContext testContext) {
        //add instances
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
        return instances;
    }

    void addModules(TestDescriptor testDescriptor, BeanDefinitionRegistry registry) {
        //add modules
        AnnotatedBeanDefinitionReader annotatedBeanDefinitionReader =
                new AnnotatedBeanDefinitionReader(registry);

        testDescriptor.getModules().stream()
                .map(Module::value)
                .forEachOrdered(annotatedBeanDefinitionReader::registerBean);
    }

    void addScans(TestDescriptor testDescriptor, BeanDefinitionRegistry registry) {
        //add scans
        ClassPathBeanDefinitionScanner scanner =
                new ClassPathBeanDefinitionScanner(registry);
        BeanDefinitionDefaults beanDefinitionDefaults =
                scanner.getBeanDefinitionDefaults();
        beanDefinitionDefaults.setLazyInit(true);

        testDescriptor.getScans().stream()
                .map(Scan::value)
                .forEachOrdered(scanner::scan);
    }

    void removeInstances(Queue<Instance<?>> instances, DefaultListableBeanFactory beanFactory) {
        //remove existing instances
        instances.forEach(instance -> {
            String name = instance.getName();
            Class contract = instance.getContract();

            if (name != null && contract != null) {
                if (beanFactory.containsBean(name)) {
                    beanFactory.removeBeanDefinition(name);
                }

                //XXX: find and remove all the beans that implment the given contract
                String[] contractBeanNames = beanNamesForTypeIncludingAncestors(
                        beanFactory,
                        contract, true, false);

                for (String beanName : contractBeanNames) {
                    beanFactory.removeBeanDefinition(beanName);
                }
            } else if (name != null) {
                if (beanFactory.containsBean(name)) {
                    beanFactory.removeBeanDefinition(name);
                }
            } else if (contract != null) {
                //XXX: find and remove all the beans that implment the given contract
                String[] contractBeanNames = beanNamesForTypeIncludingAncestors(
                        beanFactory,
                        contract, true, false);

                for (String beanName : contractBeanNames) {
                    beanFactory.removeBeanDefinition(beanName);
                }
            } else {
                //XXX: find and remove all the beans of the given instance type
                String[] typeBeanNames = beanNamesForTypeIncludingAncestors(beanFactory,
                        instance.getClass(), true, false);
                for (String beanName : typeBeanNames) {
                    beanFactory.removeBeanDefinition(beanName);
                }
            }

        });
    }

    void addInstances(Queue<Instance<?>> instances, DefaultListableBeanFactory beanFactory) {
        //add new instances
        instances.forEach(instance -> {
            Object value = instance.getValue();
            String name = instance.getName();
            Class contract = value.getClass();

            if (name == null) {
                beanFactory.registerSingleton(contract.getSimpleName(), value);
            } else {
                beanFactory.registerSingleton(name, value);
            }
        });
    }
}
