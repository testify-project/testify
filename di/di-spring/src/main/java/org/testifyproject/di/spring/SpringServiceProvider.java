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

import java.util.List;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;

/**
 * A Spring implementation of the {@link ServiceProvider} contract.
 *
 * @author saden
 */
@Discoverable
public class SpringServiceProvider implements ServiceProvider<ConfigurableApplicationContext> {

    @Override
    public ConfigurableApplicationContext create(TestContext testContext) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        applicationContext.setId(testContext.getName());
        applicationContext.setDisplayName(testContext.getName());
        applicationContext.setAllowCircularReferences(false);
        applicationContext.setAllowBeanDefinitionOverriding(true);

        return applicationContext;
    }

    @Override
    public ServiceInstance configure(TestContext testContext, ConfigurableApplicationContext applicationContext) {
        SpringServiceInstance serviceInstance = new SpringServiceInstance(applicationContext);
        List<ResourceProvider> resourceProviders = ServiceLocatorUtil.INSTANCE.findAll(ResourceProvider.class);

        SpringBeanFactoryPostProcessor postProcessor
                = new SpringBeanFactoryPostProcessor(testContext, serviceInstance, resourceProviders);

        SpringContextClosedListener closeListener
                = new SpringContextClosedListener(testContext, resourceProviders);

        applicationContext.setId(testContext.getName());
        applicationContext.addBeanFactoryPostProcessor(postProcessor);
        applicationContext.addApplicationListener(closeListener);

        return serviceInstance;
    }

    @Override
    public void postConfigure(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        testDescriptor.getModules().stream()
                .forEach(serviceInstance::addModules);

        testDescriptor.getScans().stream()
                .forEach(serviceInstance::addScans);
    }

}
