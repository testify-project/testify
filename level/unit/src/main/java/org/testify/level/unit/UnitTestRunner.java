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
package org.testify.level.unit;

import org.testify.ObjenesisHelper;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.InvokableDescriptor;
import org.testify.MethodDescriptor;
import org.testify.MockProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestRunner;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.tools.Discoverable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@Discoverable
public class UnitTestRunner implements TestRunner {

    @Override
    public void start(TestContext testContext) {
        UnitTestVerifier verifier = new UnitTestVerifier(testContext);
        verifier.dependency();
        verifier.configuration();

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        CutDescriptor cutDescriptor = testContext.getCutDescriptor().get();

        Object newInstance;
        Class<?> cutType = cutDescriptor.getType();

        try {
            testContext.debug("Creating a new instance of class under test {}", cutDescriptor.getTypeName());
            //lets try to createFake an instance the traditional way
            newInstance = cutType.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            //fallback to using Objenesis
            testContext.debug("Could not create a new instance of class under test {}, using Obenesis to create it.",
                    cutDescriptor.getTypeName());
            newInstance = ObjenesisHelper.getInstantiatorOf(cutType).newInstance();
        }

        Object cutInstance = newInstance;
        Object testInstance = testContext.getTestInstance();

        Optional<InvokableDescriptor> collaboratorMethod = testDescriptor.getCollaboratorProvider();
        MockProvider mockProvider = ServiceLocatorUtil.INSTANCE.getOne(MockProvider.class);

        if (collaboratorMethod.isPresent()) {
            InvokableDescriptor invokableDescriptor = collaboratorMethod.get();
            Optional<Object> result;

            if (invokableDescriptor.getInstance().isPresent()) {
                result = invokableDescriptor.invoke();
            } else {
                MethodDescriptor methodDescriptor = invokableDescriptor.getMethodDescriptor();
                result = methodDescriptor.invoke(testInstance);
            }

            if (result.isPresent()) {
                Object resultValue = result.get();
                Object[] collaborators;

                if (resultValue.getClass().isArray()) {
                    collaborators = (Object[]) resultValue;
                } else if (resultValue instanceof Collection) {
                    collaborators = ((Collection) resultValue).stream().toArray();
                } else {
                    collaborators = new Object[]{};
                }

                for (Object collaborator : collaborators) {
                    cutDescriptor.getFieldDescriptors().stream()
                            .filter(p -> {
                                return !p.getValue(cutInstance).isPresent()
                                        && p.isSupertypeOf(collaborator.getClass());
                            })
                            .forEach(p -> p.setValue(cutInstance, collaborator));
                }
            }
        } else {
            testDescriptor.getFieldDescriptors().stream().forEach(testFieldDescriptor -> {
                FieldDescriptor cutFieldDescriptor;
                String testFieldName = testFieldDescriptor.getDefinedName();
                Type testFieldType = testFieldDescriptor.getGenericType();

                Optional<FieldDescriptor> match = cutDescriptor.findFieldDescriptor(testFieldType, testFieldName);

                if (!match.isPresent()) {
                    match = cutDescriptor.findFieldDescriptor(testFieldType);
                }

                if (match.isPresent() && testFieldDescriptor.isReplaceable()) {
                    cutFieldDescriptor = match.get();
                    Optional cutValue = cutFieldDescriptor.getValue(cutInstance);
                    Optional testValue = testFieldDescriptor.getValue(testInstance);
                    Object value = null;
                    Class<?> mockType = testFieldDescriptor.getType();

                    if (testValue.isPresent()) {
                        Object instance = testValue.get();
                        value = mockProvider.isMock(instance)
                                ? instance
                                : mockProvider.createVirtual(mockType, instance);
                    } else if (!cutValue.isPresent()) {
                        if (testFieldDescriptor.getFake().isPresent()) {
                            value = mockProvider.createFake(mockType);
                        } else if (testFieldDescriptor.getVirtual().isPresent()) {
                            if (mockType.isInterface()) {
                                value = mockProvider.createFake(mockType);
                            } else {
                                Object instance = ObjenesisHelper.getInstantiatorOf(mockType).newInstance();
                                value = mockProvider.createVirtual(mockType, instance);
                            }
                        }
                    } else if (testFieldDescriptor.getVirtual().isPresent()) {
                        value = mockProvider.createVirtual(mockType, cutValue.get());
                    } else if (testFieldDescriptor.getFake().isPresent()) {
                        value = mockProvider.createFake(mockType);
                    }

                    cutFieldDescriptor.setValue(cutInstance, value);
                    testFieldDescriptor.setValue(testInstance, value);
                }
            });
        }

        if (cutDescriptor.getCut().isPresent() && cutDescriptor.getCut().get().value()) {
            Object instance = mockProvider.createVirtual(cutType, cutInstance);
            cutDescriptor.setValue(testInstance, instance);
        } else {
            cutDescriptor.setValue(testInstance, cutInstance);
        }

        verifier.wiring();
    }

}
