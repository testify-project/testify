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

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Fixture;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.PreInstanceProvider;

/**
 * A class that is called after the application context is refreshed to
 * initialize the test as well as start and stop test resources.
 *
 * @author saden
 */
public class SpringBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private final TestContext testContext;
    private final ServiceInstance serviceInstance;

    public SpringBeanFactoryPostProcessor(TestContext testContext, ServiceInstance serviceInstance) {
        this.testContext = testContext;
        this.serviceInstance = serviceInstance;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        Set<String> replacedBeanNames = new HashSet<>();

        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            if (!replacedBeanNames.contains(beanName)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                Class<?> beanType = beanFactory.getType(beanName);

                if (beanDefinition.isPrimary()) {
                    processPrimary(beanFactory, beanDefinition, beanName, beanType);
                }

                //mark all beans as lazy beans so we don't needlessly
                //instianticate them during testing
                beanDefinition.setLazyInit(true);

                //lets look for beans annotated with configuration
                Configuration configuration = beanType.getAnnotation(Configuration.class);

                if (configuration == null) {
                    processConfiguration(beanFactory, beanDefinition, beanType, beanName, replacedBeanNames);
                }

                //by default spring eagerly initilizes singleton scoped beans
                //so lets insure that controller entry points are prototype
                //scoped and thus make them lazy.
                Controller controller = beanType.getAnnotation(Controller.class);

                if (controller != null) {
                    beanDefinition.setScope(SCOPE_PROTOTYPE);
                }
            }
        }

        beanFactory.addBeanPostProcessor(new SpringReifierPostProcessor(testContext));

        if (testContext.getResourceStartStrategy() == StartStrategy.LAZY) {
            //add constant instances
            ServiceLocatorUtil.INSTANCE.findAllWithFilter(PreInstanceProvider.class)
                    .stream()
                    .flatMap(p -> p.get(testContext).stream())
                    .forEach(serviceInstance::replace);

            ServiceLocatorUtil.INSTANCE.findAllWithFilter(InstanceProvider.class)
                    .stream()
                    .flatMap(p -> p.get(testContext).stream())
                    .forEach(serviceInstance::replace);
        }
    }

    void processPrimary(DefaultListableBeanFactory beanFactory, BeanDefinition beanDefinition, String beanName, Class<?> beanType) {
        String[] beanNamesForType = beanFactory.getBeanNamesForType(beanType);

        if (beanNamesForType.length > 1) {
            for (String beanNameForType : beanNamesForType) {
                if (beanNameForType.equals(beanName)) {
                    beanFactory.removeBeanDefinition(beanNameForType);
                } else {
                    beanFactory.removeBeanDefinition(beanNameForType);

                    GenericBeanDefinition replacementBeanDefinition = new GenericBeanDefinition(beanDefinition);
                    replacementBeanDefinition.setPrimary(false);
                    replacementBeanDefinition.setLazyInit(true);
                    beanFactory.registerBeanDefinition(beanNameForType, replacementBeanDefinition);
                }
            }
        }
    }

    void processConfiguration(DefaultListableBeanFactory beanFactory,
            BeanDefinition beanDefinition,
            Class<?> beanType,
            String beanName,
            Set<String> replacedBeanNames) {
        String factoryBeanName = beanDefinition.getFactoryBeanName();
        Fixture fixture = beanType.getAnnotation(Fixture.class);

        //if fixture is null but the factory bean is defined then try
        //to get the fixture annotation from the factory bean type
        if (fixture == null && factoryBeanName != null) {
            Class<?> factoryBeanType = beanFactory.getType(factoryBeanName);

            fixture = factoryBeanType.isAnnotationPresent(Configuration.class)
                    ? factoryBeanType.getAnnotation(Fixture.class)
                    : null;
        }

        //if fixture is defined then lets find all the beans with
        //the same type and replace them with the bean definition
        //in the bean annotated with fixture and thus replacying
        //all production beans with test fixture beans
        if (fixture != null) {
            processFixture(beanFactory, beanDefinition, beanType, beanName, fixture, replacedBeanNames);
        }
    }

    void processFixture(DefaultListableBeanFactory beanFactory,
            BeanDefinition beanDefinition,
            Class<?> beanType,
            String beanName,
            Fixture fixture,
            Set<String> replacedBeanNames) {
        String[] beanNamesForType = beanFactory.getBeanNamesForType(beanType);

        for (String beanNameForType : beanNamesForType) {
            if (beanNameForType.equals(beanName)) {
                beanFactory.removeBeanDefinition(beanNameForType);
            } else {
                beanFactory.removeBeanDefinition(beanNameForType);
                GenericBeanDefinition replacementBeanDefinition = new GenericBeanDefinition(beanDefinition);

                if (!fixture.init().isEmpty()) {
                    replacementBeanDefinition.setInitMethodName(fixture.init());
                }

                if (!fixture.destroy().isEmpty()) {
                    replacementBeanDefinition.setDestroyMethodName(fixture.destroy());
                }

                replacementBeanDefinition.setPrimary(false);
                replacementBeanDefinition.setLazyInit(true);
                beanFactory.registerBeanDefinition(beanNameForType, replacementBeanDefinition);
                replacedBeanNames.add(beanNameForType);
            }
        }
    }

}
