/*
 * Copyright 2016-2018 Testify Project.
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

import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestConfigurer;
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

/**
 * Spring Application Context operation interceptor. This class intercepts certain Spring
 * Application Context initialization calls to configure the test case.
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
        TestContextHolder.INSTANCE.command(testContext -> {
            if (applicationContext instanceof AnnotationConfigApplicationContext) {
                AnnotationConfigApplicationContext configContext =
                        (AnnotationConfigApplicationContext) applicationContext;
                configContext.setId(testContext.getName());
                configContext.setDisplayName(testContext.getName());
                configContext.setAllowCircularReferences(false);
                configContext.setAllowBeanDefinitionOverriding(true);
            }

            TestConfigurer testConfigurer = testContext.getTestConfigurer();
            ConfigurableApplicationContext configuredApplicationContext =
                    testConfigurer.configure(testContext, applicationContext);

            TestDescriptor testDescriptor = testContext.getTestDescriptor();
            DefaultListableBeanFactory beanFactory =
                    (DefaultListableBeanFactory) applicationContext.getBeanFactory();
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;

            addModules(testDescriptor, registry);
            addScans(testDescriptor, registry);

            SpringBeanFactoryPostProcessor postProcessor =
                    new SpringBeanFactoryPostProcessor(testContext);

            applicationContext.addBeanFactoryPostProcessor(postProcessor);

            testContext.computeIfAbsent(SERVICE_INSTANCE, key -> {
                ServiceProvider<ConfigurableApplicationContext> serviceProvider =
                        ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class,
                                SpringServiceProvider.class);

                return serviceProvider.configure(testContext, configuredApplicationContext);
            });
        });

        zuper.call();
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

}
