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

import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * An HK2 implementation of the {@link ServiceProvider} contract.
 *
 * @author saden
 */
@IntegrationCategory
@SystemCategory
@Discoverable
public class JerseyServiceProvider implements ServiceProvider<InjectionManager> {

    @Override
    public InjectionManager create(TestContext testContext) {
        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        InjectionManager injectionManager = Injections.createInjectionManager();

        return testConfigurer.configure(testContext, injectionManager);
    }

    @Override
    public ServiceInstance configure(TestContext testContext, InjectionManager injectionManager) {
        return new JerseyServiceInstance(testContext, injectionManager);
    }

}
