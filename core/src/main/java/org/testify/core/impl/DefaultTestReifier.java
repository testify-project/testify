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
package org.testify.core.impl;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.testify.FieldDescriptor;
import org.testify.MethodDescriptor;
import org.testify.MockProvider;
import org.testify.ObjenesisHelper;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.annotation.Fake;
import org.testify.annotation.Virtual;
import org.testify.core.util.ServiceLocatorUtil;
import static org.testify.guava.common.base.Preconditions.checkState;
import org.testify.guava.common.reflect.TypeToken;
import org.testify.instantiator.ObjectInstantiator;
import org.testify.tools.Discoverable;

/**
 * An implementation of {@link TestReifier} contract.
 *
 * @author saden
 */
@Discoverable
public class DefaultTestReifier implements TestReifier {

    @Override
    public <T> T configure(TestContext testContext, T configuration) {
        Class<? extends Object> configurationType = configuration.getClass();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<MethodDescriptor> match = testDescriptor.findConfigHandler(configurationType);

        if (match.isPresent()) {
            MethodDescriptor methodDescriptor = match.get();
            Class<?> returnType = methodDescriptor.getReturnType();
            Optional<Object> result;

            if (methodDescriptor.getInstance().isPresent()) {
                result = methodDescriptor.invokeMethod(configuration);
            } else {
                result = methodDescriptor.invoke(testInstance, configuration);
            }

            //if the configuration method returns null its because its return type
            //is void so we return the configuration parameter passed to the method
            //otherwise we insure the result is of the same type as the configuration
            //type and return it instead.
            if (!result.isPresent()) {
                checkState(Void.TYPE.isAssignableFrom(returnType),
                        "ConfigHandler method '%s' in class '%s' returns null. "
                        + "Please insure that the method returns a non-null value "
                        + "or its return type is void.",
                        methodDescriptor.getName(), methodDescriptor.getDeclaringClassName()
                );

                return configuration;
            } else if (result.getClass().isAssignableFrom(configurationType)) {
                return (T) result;
            }
        }

        return configuration;
    }

    @Override
    public void reify(TestContext testContext, Object cutInstance) {
        testContext.getCutDescriptor().ifPresent(cutDescriptor -> {
            MockProvider mockProvider = ServiceLocatorUtil.INSTANCE.getOne(MockProvider.class);
            TestDescriptor testDescriptor = testContext.getTestDescriptor();

            Object testInstance = testContext.getTestInstance();
            Collection<FieldDescriptor> testFieldDescriptors = testDescriptor.getFieldDescriptors();

            testFieldDescriptors
                    .stream()
                    .filter(testFieldDescriptor -> testFieldDescriptor.isReplaceable())
                    .forEach((testFieldDescriptor) -> {
                        String testFieldName = testFieldDescriptor.getDefinedName();
                        Class<?> testFieldType = testFieldDescriptor.getType();
                        Type testFieldGenericType = testFieldDescriptor.getGenericType();
                        Optional<FieldDescriptor> cutFieldMatch = cutDescriptor.findFieldDescriptor(testFieldGenericType, testFieldName);

                        if (!cutFieldMatch.isPresent()) {
                            cutFieldMatch = cutDescriptor.findFieldDescriptor(testFieldGenericType);
                        }

                        Optional<Object> testFieldValue = testFieldDescriptor.getValue(testInstance);

                        if (cutFieldMatch.isPresent()) {
                            FieldDescriptor cutFieldDescriptor = cutFieldMatch.get();
                            Class<?> cutFieldType = cutFieldDescriptor.getType();
                            Optional cutFieldValue = cutFieldDescriptor.getValue(cutInstance);

                            Optional<Fake> fake = testFieldDescriptor.getFake();
                            Optional<Virtual> virtual = testFieldDescriptor.getVirtual();
                            Object value = null;

                            if (testFieldValue.isPresent()) {
                                value = testFieldValue.get();
                                if (virtual.isPresent()) {
                                    value = mockProvider.isMock(value)
                                            ? value
                                            : mockProvider.createVirtual(testFieldType, value);
                                }
                            } else {
                                if (cutFieldValue.isPresent()) {
                                    value = cutFieldValue.get();

                                    if (fake.isPresent()) {
                                        value = mockProvider.isMock(value)
                                                ? value
                                                : mockProvider.createFake(cutFieldType);
                                    } else if (virtual.isPresent()) {
                                        value = mockProvider.isMock(value)
                                                ? value
                                                : mockProvider.createVirtual(cutFieldType, value);
                                    }
                                } else {
                                    if (fake.isPresent()) {
                                        value = mockProvider.isMock(value)
                                                ? value
                                                : mockProvider.createFake(cutFieldType);
                                    } else if (virtual.isPresent()) {
                                        if (testFieldType.isInterface()) {
                                            value = mockProvider.createFake(testFieldType);
                                        } else {
                                            ObjectInstantiator<?> instantiator = ObjenesisHelper.getInstantiatorOf(testFieldType);
                                            value = mockProvider.createVirtual(testFieldType, instantiator.newInstance());
                                        }
                                    }
                                }

                            }

                            cutFieldDescriptor.setValue(cutInstance, value);
                            testFieldDescriptor.setValue(testInstance, value);
                        }
                    });
        });
    }

    @Override
    public void reify(TestContext testContext, Object cutInstance, Object... collaborators) {
        testContext.getCutDescriptor().ifPresent(cutDescriptor -> {
            MockProvider mockProvider = ServiceLocatorUtil.INSTANCE.getOne(MockProvider.class);
            TestDescriptor testDescriptor = testContext.getTestDescriptor();
            Object testInstance = testContext.getTestInstance();
            Set<FieldDescriptor> ignoredDescriptors = new HashSet<>();

            for (Object collaborator : collaborators) {
                Class<?> collaboratorType = collaborator.getClass();
                TypeToken<?> typeToken = TypeToken.of(collaboratorType);

                cutDescriptor.getFieldDescriptors().forEach((cutFieldDescriptor) -> {
                    Class<?> cutFieldType = cutFieldDescriptor.getType();
                    String cutFieldName = cutFieldDescriptor.getDefinedName();

                    if (!ignoredDescriptors.contains(cutFieldDescriptor)
                            && typeToken.isSubtypeOf(cutFieldType)) {
                        Optional<FieldDescriptor> testFieldMatch
                                = testDescriptor.findFieldDescriptor(cutFieldType, cutFieldName);

                        if (!testFieldMatch.isPresent()) {
                            testFieldMatch = testDescriptor.findFieldDescriptor(cutFieldType);
                        }

                        Object value = collaborator;

                        if (testFieldMatch.isPresent()) {
                            FieldDescriptor testFieldDescriptor = testFieldMatch.get();
                            Optional<Virtual> virtual = testFieldDescriptor.getVirtual();

                            if (!ignoredDescriptors.contains(testFieldDescriptor)
                                    && virtual.isPresent()) {
                                value = mockProvider.isMock(value)
                                        ? value
                                        : mockProvider.createVirtual(cutFieldType, value);
                            }

                            testFieldDescriptor.setValue(testInstance, value);
                            ignoredDescriptors.add(testFieldDescriptor);
                        }

                        cutFieldDescriptor.setValue(cutInstance, value);
                        ignoredDescriptors.add(cutFieldDescriptor);
                    }
                });
            }
        });
    }
}
