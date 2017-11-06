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
package org.testifyproject.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of {@link ServiceProvider} backed by objects in the {@link TestContext}.
 *
 * @author saden
 */
@UnitCategory
@Discoverable
public class DefaultServiceProvider implements ServiceProvider<Map<ServiceKey, Object>> {

    @Override
    public Map<ServiceKey, Object> create(TestContext testContext) {
        Map<ServiceKey, Object> serviceContext = new ConcurrentHashMap<>();

        return serviceContext;
    }

    @Override
    public ServiceInstance configure(TestContext testContext,
            Map<ServiceKey, Object> serviceContext) {

        //add instances
        ServiceLocatorUtil.INSTANCE
                .findAllWithFilter(InstanceProvider.class)
                .stream()
                .flatMap(p -> p.get(testContext).stream())
                .forEach(instance -> {
                    serviceContext.put(
                            ServiceKey.of(instance.getContract(), instance.getName()),
                            instance.getValue()
                    );
                });

        return new DefaultServiceInstance(serviceContext);
    }

}
