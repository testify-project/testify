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
package org.testifyproject.di.guice;

import java.util.List;
import java.util.Optional;

import javax.inject.Named;
import javax.inject.Provider;

import org.testifyproject.Instance;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * Instance provider implementation that provides fake fields as instances that can be added to
 * Guice injector.
 *
 * @author saden
 */
@IntegrationCategory
@SystemCategory
@Discoverable
public class GuiceFakeInstanceProvider implements InstanceProvider {

    @Override
    public List<Instance> get(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        MockProvider mockProvider = testContext.getMockProvider();
        Object testInstance = testContext.getTestInstance();

        ImmutableList.Builder<Instance> builder = ImmutableList.builder();

        testDescriptor.getFieldDescriptors().stream().forEach(fieldDescriptor -> {
            Optional<Object> value = fieldDescriptor.getValue(testInstance);

            if (fieldDescriptor.isMock() && value.isPresent()) {
                Object instance = value.get();
                String name = null;
                TypeToken<?> token = TypeToken.of(fieldDescriptor.getGenericType());
                Class contract = token.getRawType();

                //if we are dealing with provider type then extract its parameter type and
                //use it as the contract type
                if (token.isSubtypeOf(Provider.class)) {
                    contract = token
                            .resolveType(Provider.class.getTypeParameters()[0])
                            .getRawType();
                    instance = mockProvider.createFake(contract);
                } else if (token.isSubtypeOf(com.google.inject.Provider.class)) {
                    contract = token
                            .resolveType(com.google.inject.Provider.class.getTypeParameters()[0])
                            .getRawType();
                    instance = mockProvider.createFake(contract);
                }

                Optional<Named> javaxName =
                        fieldDescriptor.getAnnotation(Named.class);
                Optional<com.google.inject.name.Named> guiceName =
                        fieldDescriptor.getAnnotation(
                                com.google.inject.name.Named.class);

                if (javaxName.isPresent()) {
                    name = javaxName.get().value();
                } else if (guiceName.isPresent()) {
                    name = guiceName.get().value();
                }

                builder.add(DefaultInstance.of(instance, name, contract));
            }

        });

        return builder.build();
    }
}
