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
package org.testifyproject.level.unit;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.TestRunner;
import org.testifyproject.annotation.Cut;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.WiringVerifier;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@UnitTest
@Discoverable
public class UnitTestRunner implements TestRunner {

    private TestContext testContext;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(ConfigurationVerifier.class, UnitTest.class)
                .forEach(p -> p.verify(testContext));

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        TestReifier testReifier = testContext.getTestReifier();
        Optional<CutDescriptor> foundCutDescriptor = testContext.getCutDescriptor();

        if (foundCutDescriptor.isPresent()) {
            CutDescriptor cutDescriptor = foundCutDescriptor.get();

            Class<?> cutType = cutDescriptor.getType();

            //lets try to create an instance of the cut class
            Object cutInstance = ReflectionUtil.INSTANCE.newInstance(cutType);
            Object testInstance = testContext.getTestInstance();

            Optional<MethodDescriptor> collaboratorMethod = testDescriptor.getCollaboratorProvider();
            MockProvider mockProvider = testContext.getMockProvider();

            if (collaboratorMethod.isPresent()) {
                MethodDescriptor methodDescriptor = collaboratorMethod.get();
                Optional<Object> collaboratorMethodResult;
                Optional<Object> methodInstance = methodDescriptor.getInstance();

                if (methodInstance.isPresent()) {
                    collaboratorMethodResult = methodDescriptor.invoke(methodInstance.get());
                } else {
                    collaboratorMethodResult = methodDescriptor.invoke(testInstance);
                }

                if (collaboratorMethodResult.isPresent()) {
                    Object resultValue = collaboratorMethodResult.get();
                    Object[] collaborators;

                    if (resultValue.getClass().isArray()) {
                        collaborators = (Object[]) resultValue;
                    } else if (resultValue instanceof Collection) {
                        collaborators = ((Collection) resultValue).stream().toArray();
                    } else {
                        collaborators = new Object[]{};
                    }

                    testReifier.reify(testContext, cutInstance, collaborators);
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

                    if (match.isPresent() && testFieldDescriptor.isInjectable()) {
                        cutFieldDescriptor = match.get();
                        Optional cutValue = cutFieldDescriptor.getValue(cutInstance);
                        Optional testValue = testFieldDescriptor.getValue(testInstance);
                        Class<?> mockType = testFieldDescriptor.getType();
                        Object value = null;

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
                                    Object instance = ReflectionUtil.INSTANCE.newInstance(mockType);
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

            Cut cut = cutDescriptor.getCut();
            Object testCutInstance = cutInstance;

            //If the cut annotation's value flag is set to true then create a
            //virtual instance of the cut class
            if (cut.value()) {
                testCutInstance = mockProvider.createVirtual(cutType, cutInstance);
            }

            cutDescriptor.setValue(testInstance, testCutInstance);

            //invoke init method on test fields annotated with Fixture
            testDescriptor.getFieldDescriptors()
                    .forEach(p -> p.init(testInstance));

            //invoke init method on cut field annotated with Fixture
            foundCutDescriptor
                    .ifPresent(p -> p.init(testInstance));

            ServiceLocatorUtil.INSTANCE.findAllWithFilter(WiringVerifier.class, UnitTest.class)
                    .forEach(p -> p.verify(testContext));
        }
    }

    @Override
    public void stop() {
        Object testInstance = testContext.getTestInstance();

        //invoke destroy method on fields annotated with Fixture
        testContext.getTestDescriptor().getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.destroy(testInstance));
    }

}
