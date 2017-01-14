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
package org.testify.di.spring;

import org.testify.ServiceInstance;
import org.testify.ServiceProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.annotation.Module;
import org.testify.annotation.Scan;
import org.testify.tools.Discoverable;
import java.util.List;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
    public ServiceInstance configure(TestContext testContext, ConfigurableApplicationContext serviceContext) {
        serviceContext.setId(testContext.getName());

        SpringServiceInstance serviceInstance = new SpringServiceInstance(serviceContext);

        SpringBeanFactoryPostProcessor requiresPostProcessor = new SpringBeanFactoryPostProcessor(testContext, serviceInstance);
        serviceContext.addBeanFactoryPostProcessor(requiresPostProcessor);
        serviceContext.addApplicationListener(requiresPostProcessor);

        return serviceInstance;
    }

    @Override
    public void postConfigure(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        List<Module> modules = testDescriptor.getModules();

        if (!modules.isEmpty()) {
            modules.stream().forEach(serviceInstance::addModules);
        }

        List<Scan> scans = testDescriptor.getScans();

        if (!scans.isEmpty()) {
            scans.stream().forEach(serviceInstance::addScans);
        }
    }

}
