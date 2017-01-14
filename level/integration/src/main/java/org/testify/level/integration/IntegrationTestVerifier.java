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
package org.testify.level.integration;

import static org.testify.guava.common.base.Preconditions.checkState;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.ParameterDescriptor;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestVerifier;
import org.testify.annotation.RequiresResource;
import static java.lang.Class.forName;
import static java.lang.reflect.Modifier.isFinal;
import static java.security.AccessController.doPrivileged;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * An integration test verifier that inspects the test context descriptors to
 * make sure everything is properly configured before test class injection is
 * performed and everything is wired correctly after test class injection is
 * performed.
 *
 * @author saden
 */
public class IntegrationTestVerifier implements TestVerifier {

    private final TestContext testContext;

    public IntegrationTestVerifier(TestContext context) {
        this.testContext = context;
    }

    @Override
    public void dependency() {
        doPrivileged((PrivilegedAction<Object>) () -> {
            testContext.getDependencies().entrySet().parallelStream().forEach(p -> {
                try {
                    forName(p.getKey());
                } catch (ClassNotFoundException e) {
                    checkState(false,
                            "'%s' not found. Please insure '%s' is in the classpath.",
                            p.getKey(), p.getValue());
                }
            });
            return null;
        });
    }

    @Override
    public void configuration() {
        doPrivileged((PrivilegedAction<Object>) () -> {
            TestDescriptor testDescriptor = testContext.getTestDescriptor();
            String testClassName = testDescriptor.getTestClassName();
            Optional<CutDescriptor> cutDescriptor = testContext.getCutDescriptor();

            Collection<FieldDescriptor> fieldDescriptors = testDescriptor.getFieldDescriptors();

            //insure that there is a field annotated with @Cut defined or one or more
            //fields annotated with @Real or @Inject
            if (!cutDescriptor.isPresent() && fieldDescriptors.isEmpty()) {
                checkState(false,
                        "Test class '%s' does not define a field annotated with @Cut "
                        + "nor does it define field(s) annotated with @Real or @Inject. "
                        + "Please insure the test class defines a single field annotated "
                        + "with @Cut or defines at least one field annotated with @Real "
                        + "or @Inject.",
                        testClassName
                );
            }

            //insure required resources providers have default constructors.
            List<RequiresResource> requiresResources = testDescriptor.getRequiresResources();
            requiresResources.parallelStream().map(RequiresResource::value).forEach(p -> {
                try {
                    p.getDeclaredConstructor();
                } catch (NoSuchMethodException e) {
                    checkState(false,
                            "Required Resource '%s' defined in test class '%s' does not have a zero "
                            + "argument default constructor. Please insure that the required resource "
                            + "provider defines an accessible zero argument default constructor.",
                            testClassName, p.getSimpleName()
                    );
                }
            });

            fieldDescriptors.parallelStream().forEach(p -> {
                Class<?> fieldType = p.getType();
                String fieldName = p.getName();
                String fieldTypeName = p.getTypeName();

                checkState(!fieldType.isArray(),
                        "Field '%s' in test class '%s' can not be configured because '%s'"
                        + " is an array. Please consider using a List instead of arrays.",
                        fieldName, testClassName, fieldTypeName);

                if (p.getFake().isPresent()) {
                    checkState(!isFinal(fieldType.getModifiers()),
                            "Can not fake field '%s' in test class '%s' because '%s'"
                            + " is a final class.",
                            fieldName, testClassName, fieldTypeName);
                }

                if (p.getVirtual().isPresent()) {
                    checkState(!isFinal(fieldType.getModifiers()),
                            "Can not create delegated Real of field '%s' in test class '%s' "
                            + "because '%s' is a final class.",
                            fieldName, testClassName, fieldTypeName);
                }
            });

            return null;
        });
    }

    @Override
    public void wiring() {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<CutDescriptor> descriptor = testContext.getCutDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        if (descriptor.isPresent()) {
            CutDescriptor cutDescriptor = descriptor.get();
            List<ParameterDescriptor> paramDescriptors = cutDescriptor.getParameterDescriptors();

            String cutClassName = cutDescriptor.getTypeName();
            List<ParameterDescriptor> undeclared = new ArrayList<>(paramDescriptors);
            paramDescriptors.parallelStream().forEach(p -> {
                for (FieldDescriptor fieldDescriptor : testDescriptor.getFieldDescriptors()) {
                    if (p.isSubtypeOf(fieldDescriptor.getGenericType()) && fieldDescriptor.getValue(testInstance).isPresent()) {
                        undeclared.remove(p);
                    }
                }

            });

            undeclared
                    .stream()
                    .map((parameterDescriptor) -> parameterDescriptor.getTypeName())
                    .forEach((paramTypeName) -> {
                        testContext.warn("Class under test '{}' defined in '{}' has a collaborator "
                                + "of type '{}' but test class '{}' does not define a field of "
                                + "type '{}' annotated with @Fake, @Real, or @Inject. The real "
                                + "instance of the collaborator will be used.",
                                cutClassName, testClassName, paramTypeName, testClassName, paramTypeName);
                    });

        }

    }

}
