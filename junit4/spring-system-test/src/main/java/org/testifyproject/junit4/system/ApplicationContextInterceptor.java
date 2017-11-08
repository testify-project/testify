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
package org.testifyproject.junit4.system;

import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
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
import org.testifyproject.di.spring.SpringBeanFactoryPostProcessor;
import org.testifyproject.di.spring.SpringServiceProvider;

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

    public ConfigurableListableBeanFactory obtainFreshBeanFactory(
            @SuperCall Callable<ConfigurableListableBeanFactory> zuper,
            @This Object applicationContext,
            @AllArguments Object[] args) throws Exception {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) zuper.call();

        TestContextHolder.INSTANCE.execute(testContext -> {
            AnnotationConfigWebApplicationContext configuredContext =
                    (AnnotationConfigWebApplicationContext) applicationContext;

            if (applicationContext instanceof AnnotationConfigWebApplicationContext) {
                configuredContext.setId(testContext.getName());
                configuredContext.setDisplayName(testContext.getName());
                configuredContext.setAllowCircularReferences(false);
                configuredContext.setAllowBeanDefinitionOverriding(true);
            }

            SpringBeanFactoryPostProcessor postProcessor =
                    new SpringBeanFactoryPostProcessor(testContext);

            configuredContext.addBeanFactoryPostProcessor(postProcessor);

            TestConfigurer testConfigurer = testContext.getTestConfigurer();
            ConfigurableApplicationContext configuredApplicationContext =
                    testConfigurer.configure(testContext, configuredContext);

            TestDescriptor testDescriptor = testContext.getTestDescriptor();

            ConfigurableEnvironment environment = configuredContext.getEnvironment();
            addModules(testDescriptor, environment, beanFactory);
            addScans(testDescriptor, environment, beanFactory);

            testContext.computeIfAbsent(SERVICE_INSTANCE, key -> {
                ServiceProvider<ConfigurableApplicationContext> serviceProvider =
                        ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class,
                                SpringServiceProvider.class);

                return serviceProvider.configure(testContext, configuredApplicationContext);
            });
        });

        return beanFactory;
    }

    void addModules(TestDescriptor testDescriptor,
            ConfigurableEnvironment environment,
            DefaultListableBeanFactory beanFactory) {
        AnnotatedBeanDefinitionReader reader =
                new AnnotatedBeanDefinitionReader(beanFactory, environment);

        testDescriptor.getModules().stream()
                .map(Module::value)
                .forEachOrdered(reader::registerBean);
    }

    void addScans(TestDescriptor testDescriptor,
            ConfigurableEnvironment environment,
            DefaultListableBeanFactory beanFactory) {
        ClassPathBeanDefinitionScanner scanner =
                new ClassPathBeanDefinitionScanner(beanFactory, true, environment);
        BeanDefinitionDefaults beanDefinitionDefaults =
                scanner.getBeanDefinitionDefaults();
        beanDefinitionDefaults.setLazyInit(true);

        testDescriptor.getScans().stream()
                .map(Scan::value)
                .forEachOrdered(scanner::scan);
    }

}
