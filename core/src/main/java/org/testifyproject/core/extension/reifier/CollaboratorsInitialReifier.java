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
package org.testifyproject.core.extension.reifier;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Name;
import org.testifyproject.annotation.Sut;
import org.testifyproject.core.analyzer.DefaultMethodDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies the sut and test based on the presence of
 * {@link org.testifyproject.annotation.CollaboratorProvider} class on the test class.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@Discoverable
public class CollaboratorsInitialReifier implements InitialReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        MockProvider mockProvider = testContext.getMockProvider();

        testContext.getSutDescriptor()
                .ifPresent(sutDescriptor ->
                        sutDescriptor.getValue(testInstance)
                                .ifPresent(sutValue -> {
                                    TestDescriptor testDescriptor = testContext
                                            .getTestDescriptor();
                                    Sut sut = sutDescriptor.getSut();
                                    String factoryMethodName = sut.factoryMethod();

                                    if (!factoryMethodName.isEmpty()) {
                                        processFactoryMethod(factoryMethodName,
                                                testDescriptor,
                                                sutDescriptor,
                                                mockProvider,
                                                sutValue,
                                                testInstance);
                                    } else {
                                        processFields(testDescriptor,
                                                sutDescriptor,
                                                mockProvider,
                                                sutValue,
                                                testInstance);
                                    }
                                }));
    }

    void processFactoryMethod(String factoryMethodName,
            TestDescriptor testDescriptor,
            SutDescriptor sutDescriptor,
            MockProvider mockProvider,
            Object sutValue,
            Object testInstance) {
        sutDescriptor.findMethod(factoryMethodName).ifPresent(method -> {
            //if factory method is defined then use factory method to create an
            //instance of the SUT
            MethodDescriptor factoryMethodDescriptor = DefaultMethodDescriptor.of(method);
            Class<?>[] factoryMethodParamTypes = method.getParameterTypes();

            //if the collaborator provider has a method that returns a collection or an array
            //of collaborators that match the types of the factory method then use it to
            //create an instance of the sut.
            Collection<MethodDescriptor> collaboratorProviders = testDescriptor
                    .getCollaboratorProviders();

            for (MethodDescriptor collaboratorProvider : collaboratorProviders) {
                if (collaboratorProvider.hasReturnType(Object[].class)
                        || collaboratorProvider.hasReturnType(Collection.class)) {
                    Optional<Object> foundCollaborators =
                            getCollaborators(testDescriptor, collaboratorProvider, testInstance);

                    if (foundCollaborators.isPresent()) {
                        Object[] collaborators = convertToArray(foundCollaborators.get());
                        Class[] collaboratorReturnTypes = Stream.of(collaborators)
                                .map(collaborator -> getCollaboratorType(mockProvider,
                                        collaborator))
                                .toArray(Class[]::new);

                        if (Arrays.equals(factoryMethodParamTypes, collaboratorReturnTypes)) {
                            factoryMethodDescriptor.invoke(sutValue, collaborators)
                                    .ifPresent(value -> sutDescriptor
                                            .setValue(testInstance, value));
                            return;
                        }

                    }
                }
            }

            //if the sut has not been refied already then lets try reifying it by looking
            //at the builder paramteters and see if there are methods in the collaborator
            //providers that return the same type and call them to provide collaborators
            //for the sut
            Object[] collaborators = findCollaborators(testDescriptor, factoryMethodDescriptor,
                    testInstance);

            factoryMethodDescriptor.invoke(sutValue, collaborators)
                    .ifPresent(value -> sutDescriptor.setValue(testInstance, value));
        });
    }

    void processFields(TestDescriptor testDescriptor,
            SutDescriptor sutDescriptor,
            MockProvider mockProvider,
            Object sutValue,
            Object testInstance) {
        //if method factory is not specified then find collaborator methods that provide
        //an array or collection and see if they can be used to reify the sut
        Collection<MethodDescriptor> collaboratorProviders = testDescriptor
                .getCollaboratorProviders();

        for (MethodDescriptor collaboratorProvider : collaboratorProviders) {
            if (collaboratorProvider.hasReturnType(Object[].class)
                    || collaboratorProvider.hasReturnType(Collection.class)) {
                Optional<Object> foundCollaborators =
                        getCollaborators(testDescriptor, collaboratorProvider, testInstance);

                if (foundCollaborators.isPresent()) {
                    processCollaborators(
                            mockProvider,
                            sutDescriptor,
                            sutValue,
                            convertToArray(foundCollaborators.get()));
                    break;
                }
            }
        }
    }

    void processCollaborators(MockProvider mockProvider,
            SutDescriptor sutDescriptor,
            Object sutValue,
            Object[] collaborators) {
        for (Object collaborator : collaborators) {
            if (collaborator == null) {
                continue;
            }

            Class collaboratorType = getCollaboratorType(mockProvider, collaborator);
            Optional<FieldDescriptor> foundFieldDescriptor =
                    sutDescriptor.findFieldDescriptor(collaboratorType);

            if (foundFieldDescriptor.isPresent()) {
                FieldDescriptor fieldDescriptor = foundFieldDescriptor.get();
                fieldDescriptor.setValue(sutValue, collaborator);
            }
        }
    }

    Optional<Object> getCollaborators(TestDescriptor testDescriptor,
            MethodDescriptor methodDescriptor, Object testInstance) {
        Object[] collaborators = findCollaborators(testDescriptor, methodDescriptor,
                testInstance);

        return methodDescriptor.getInstance()
                .map(instance -> methodDescriptor.invoke(instance, convertToArray(
                        collaborators)))
                .orElseGet(() -> methodDescriptor.invoke(testInstance, convertToArray(
                        collaborators)));
    }

    Object[] findCollaborators(TestDescriptor testDescriptor, MethodDescriptor methodDescriptor,
            Object testInstance) {
        return methodDescriptor.getParameters().stream()
                .map(parameter -> {
                    Optional<MethodDescriptor> foundMethodDescriptor =
                            findCollaboratorProvider(testDescriptor, parameter);

                    ExceptionUtil.INSTANCE.raise(!foundMethodDescriptor.isPresent(),
                            "Could not find a provider for argument '{} {}'"
                            + " for collaborator method provider '{}' in '{}'",
                            parameter.getType().getSimpleName(),
                            parameter.getName(),
                            methodDescriptor.getName(),
                            methodDescriptor.getDeclaringClassName());

                    return foundMethodDescriptor;
                })
                .filter(foundMethodDescriptor -> foundMethodDescriptor.isPresent())
                .map(foundMethodDescriptor -> foundMethodDescriptor.get())
                .map(parameterMethodDescriptor ->
                        getCollaborators(testDescriptor, parameterMethodDescriptor, testInstance)
                                .orElse(null)
                )
                .toArray();
    }

    Optional<MethodDescriptor> findCollaboratorProvider(TestDescriptor testDescriptor,
            Parameter parameter) {
        Class<?> parameterType = parameter.getType();
        Name name = parameter.getDeclaredAnnotation(Name.class);
        Optional<MethodDescriptor> foundMethodDescriptor;

        if (name == null) {
            //find a method that has returns the same type as the parameter and has the same name
            foundMethodDescriptor = testDescriptor.findCollaboratorProvider(parameterType,
                    parameter
                            .getName());

            //if one is not found then just use parameter type matching
            if (!foundMethodDescriptor.isPresent()) {
                foundMethodDescriptor = testDescriptor.findCollaboratorProvider(parameterType);
            }
        } else {
            //if a name is specified then use it to find the right method
            foundMethodDescriptor = testDescriptor.findCollaboratorProvider(parameterType, name
                    .value());
        }

        return foundMethodDescriptor;
    }

    Object[] convertToArray(Object value) {
        Object[] collaborators;

        if (value.getClass().isArray()) {
            collaborators = (Object[]) value;
        } else if (value instanceof Collection) {
            collaborators = ((Collection) value).stream().toArray();
        } else if (value instanceof Optional) {
            collaborators = new Object[]{((Optional) value).orElse(null)};
        } else {
            collaborators = new Object[]{value};
        }

        return collaborators;
    }

    Class getCollaboratorType(MockProvider mockProvider, Object collaborator) {
        Class collaboratorType = collaborator.getClass();

        if (mockProvider.isMock(collaborator)) {
            Class collaboratorSuperclass = collaborator.getClass().getSuperclass();
            Class[] collaboratorInterfaces = collaborator.getClass().getInterfaces();
            collaboratorType = !Object.class.equals(collaboratorSuperclass)
                    ? collaboratorSuperclass
                    : collaboratorInterfaces.length != 0
                            ? collaboratorInterfaces[0]
                            : collaboratorType;
        }

        return collaboratorType;
    }

}
