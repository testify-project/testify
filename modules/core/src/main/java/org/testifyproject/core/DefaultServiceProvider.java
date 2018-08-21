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
package org.testifyproject.core;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.Name;
import org.testifyproject.core.extension.FindCollaborators;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.annotation.UnitCategory;

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

        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        testDescriptor.getCollaboratorProviders().stream().forEach(methodDescriptor -> {
            new FindCollaborators(testDescriptor, methodDescriptor, testInstance)
                    .execute()
                    .filter(Objects::nonNull)
                    .ifPresent(value -> {
                        String name = methodDescriptor.getAnnotation(Name.class)
                                .map(Name::value)
                                .filter(p -> !p.isEmpty())
                                .orElseGet(methodDescriptor::getDeclaredName);

                        Class<?> contract = value.getClass();
                        ServiceKey contractAndNameKey = ServiceKey.of(contract, name);
                        ServiceKey contractKey = ServiceKey.of(contract, name);

                        serviceContext.computeIfAbsent(contractAndNameKey, t -> value);
                        serviceContext.computeIfAbsent(contractKey, t -> value);

                    });
        });

        return new DefaultServiceInstance(serviceContext);
    }

}
