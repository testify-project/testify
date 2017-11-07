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

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * A Spring implementation of the {@link ServiceProvider} contract.
 *
 * @author saden
 */
@IntegrationCategory
@SystemCategory
@Discoverable
public class SpringServiceProvider implements
        ServiceProvider<ConfigurableApplicationContext> {

    @Override
    public ConfigurableApplicationContext create(TestContext testContext) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext();

        applicationContext.refresh();

        return applicationContext;
    }

    @Override
    public ServiceInstance configure(TestContext testContext,
            ConfigurableApplicationContext applicationContext) {
        return new SpringServiceInstance(applicationContext);
    }

}
