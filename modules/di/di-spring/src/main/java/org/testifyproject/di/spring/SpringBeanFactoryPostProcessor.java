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
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.lang.annotation.Annotation;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.ProxyInstanceController;

/**
 * A class that is called after the application context is refreshed to initialize the test as
 * well as start and stop test resources.
 *
 * @author saden
 */
public class SpringBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private final TestContext testContext;

    public SpringBeanFactoryPostProcessor(TestContext testContext) {
        this.testContext = testContext;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory configurableListableBeanFactory) {
        DefaultListableBeanFactory beanFactory =
                (DefaultListableBeanFactory) configurableListableBeanFactory;

        Queue<Instance<?>> instances = getInstances(testContext);

        removeInstances(instances, beanFactory);
        addInstances(instances, beanFactory);

        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            //mark all beans as lazy beans so we don't needlessly
            //instianticate them during testing
            beanDefinition.setLazyInit(true);

            Class<?> beanType = beanFactory.getType(beanName);

            if (beanDefinition.isPrimary()) {
                processPrimary(beanFactory, beanDefinition, beanName, beanType);
            }

            //by default spring eagerly initilizes singleton scoped beans such as
            //controllers so lets insure that controller entry points are prototype
            //scoped and thus make them lazy.
            boolean isController = isController(beanType);

            if (isController) {
                beanDefinition.setScope(SCOPE_PROTOTYPE);
            }
        }

        beanFactory.addBeanPostProcessor(new SpringReifierPostProcessor(testContext));

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

    void processPrimary(DefaultListableBeanFactory beanFactory,
            BeanDefinition beanDefinition, String beanName, Class<?> beanType) {
        String[] beanNamesForType = beanFactory.getBeanNamesForType(beanType);

        if (beanNamesForType.length == 2) {
            for (String beanNameForType : beanNamesForType) {
                if (beanNameForType.equals(beanName)) {
                    beanFactory.removeBeanDefinition(beanNameForType);
                } else {
                    beanFactory.removeBeanDefinition(beanNameForType);

                    GenericBeanDefinition replacementBeanDefinition =
                            new GenericBeanDefinition(beanDefinition);
                    replacementBeanDefinition.setPrimary(false);
                    replacementBeanDefinition.setLazyInit(true);
                    beanFactory.registerBeanDefinition(beanNameForType,
                            replacementBeanDefinition);
                }
            }
        }
    }

    private boolean isController(Class<?> beanType) {
        Controller controller = beanType.getAnnotation(Controller.class);

        if (controller == null) {
            Annotation[] annotations = beanType.getAnnotations();

            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();

                controller = annotationType.getAnnotation(Controller.class);

                if (controller != null) {
                    break;
                }

            }
        }

        return controller != null;
    }

}
