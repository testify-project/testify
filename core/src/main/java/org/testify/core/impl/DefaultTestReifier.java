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
import java.util.Optional;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.InvokableDescriptor;
import org.testify.MethodDescriptor;
import org.testify.MockProvider;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.core.util.ServiceLocatorUtil;
import static org.testify.guava.common.base.Preconditions.checkState;

/**
 * An implementation of {@link TestReifier} contract.
 *
 * @author saden
 */
public class DefaultTestReifier implements TestReifier {

    private final Object testInstance;
    private final TestDescriptor testDescriptor;
    private final CutDescriptor cutDescriptor;

    public DefaultTestReifier(Object testInstance, TestDescriptor testDescriptor, CutDescriptor cutDescriptor) {
        this.testInstance = testInstance;
        this.testDescriptor = testDescriptor;
        this.cutDescriptor = cutDescriptor;
    }

    @Override
    public <T> T configure(T configuration) {
        Class<? extends Object> configurationType = configuration.getClass();
        Optional<InvokableDescriptor> match = testDescriptor.findConfigHandler(configurationType);

        if (match.isPresent()) {
            InvokableDescriptor invokableDescriptor = match.get();
            MethodDescriptor methodDescriptor = invokableDescriptor.getMethodDescriptor();
            Class<?> returnType = methodDescriptor.getReturnType();
            Optional<Object> result;

            if (invokableDescriptor.getInstance().isPresent()) {
                result = invokableDescriptor.invoke(configuration);
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
    public void reify(Object cutInstance) {
        Collection<FieldDescriptor> testFields = testDescriptor.getFieldDescriptors();

        testFields.stream().forEach((testField) -> {
            String testFieldName = testField.getDefinedName();
            Type testFieldType = testField.getGenericType();

            Optional<FieldDescriptor> match = cutDescriptor.findFieldDescriptor(testFieldType, testFieldName);

            if (!match.isPresent() && testField.isReplaceable()) {
                match = cutDescriptor.findFieldDescriptor(testFieldType);
            }

            Optional<Object> testFieldValue = testField.getValue(testInstance);

            if (match.isPresent() && !testFieldValue.isPresent()) {
                MockProvider mockProvider = ServiceLocatorUtil.INSTANCE.getOne(MockProvider.class);

                FieldDescriptor cutField = match.get();
                Optional cutValue = cutField.getValue(cutInstance);
                Object value = null;

                if (cutValue.isPresent()) {
                    value = cutValue.get();
                    if (testField.getFake().isPresent()) {
                        value = mockProvider.isMock(value)
                                ? value
                                : mockProvider.createFake(cutField.getType());
                    } else if (testField.getVirtual().isPresent()) {
                        value = mockProvider.isMock(value)
                                ? value
                                : mockProvider.createVirtual(cutField.getType(), value);
                    }
                } else if (testField.getFake().isPresent()) {
                    value = mockProvider.isMock(value)
                            ? value
                            : mockProvider.createFake(cutField.getType());
                }

                cutField.setValue(cutInstance, value);
                testField.setValue(testInstance, value);
            }
        });
    }

}
