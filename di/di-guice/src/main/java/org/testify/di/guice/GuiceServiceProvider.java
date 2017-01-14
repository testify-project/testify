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
package org.testify.di.guice;

import org.testify.ServiceInstance;
import org.testify.ServiceProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.annotation.Module;
import org.testify.tools.Discoverable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;

/**
 * A Google Guice implementation of the {@link ServiceProvider} contract.
 *
 * @author saden
 */
@Discoverable
public class GuiceServiceProvider implements ServiceProvider<Injector> {

    @Override
    public Injector create(TestContext testContext) {
        return Guice.createInjector();
    }

    @Override
    public ServiceInstance configure(TestContext testContext, Injector injector) {
        return new GuiceServiceInstance(injector);
    }

    @Override
    public void postConfigure(TestContext testContext, ServiceInstance serviceInstance) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        List<Module> modules = testDescriptor.getModules();

        if (!modules.isEmpty()) {
            modules.stream().forEach(serviceInstance::addModules);
        }
    }

}
