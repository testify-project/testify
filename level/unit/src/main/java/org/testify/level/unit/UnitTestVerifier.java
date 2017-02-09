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

import static java.lang.reflect.Modifier.isFinal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.ParameterDescriptor;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestVerifier;
import org.testify.annotation.Real;
import static org.testify.guava.common.base.Preconditions.checkState;

/**
 * A unit test verifier that inspects unit test context descriptors to make sure
 * everything is properly configured before test class injection is performed
 * and everything is wired correctly after test class injection is performed.
 *
 * @author saden
 */
public class UnitTestVerifier implements TestVerifier {

    private final TestContext testContext;

    public UnitTestVerifier(TestContext testContext) {
        this.testContext = testContext;
    }

    @Override
    public void dependency() {

        testContext.getDependencies().entrySet().parallelStream().forEach(p -> {
            try {
                Class.forName(p.getKey());
            } catch (ClassNotFoundException e) {
                checkState(false,
                        "'%s' not found. Please insure '%s' dependency is in the classpath.",
                        p.getKey(), p.getValue());
            }
        });
    }

    @Override
    public void configuration() {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testContext.getClassName();
        Collection<FieldDescriptor> fieldDescriptors = testDescriptor.getFieldDescriptors();
        Optional<CutDescriptor> cutDescriptor = testContext.getCutDescriptor();

        checkState(cutDescriptor.isPresent(),
                "Test class '%s' does not define a field annotated with @Cut class.",
                testClassName);

        fieldDescriptors.parallelStream().forEach(p -> {

            Class<?> fieldType = p.getType();
            String fieldName = p.getName();
            String fieldTypeName = p.getTypeName();

            checkState(!isFinal(fieldType.getModifiers()),
                    "Field '%s' in test class '%s' can not be faked because '%s'"
                    + " is a final class.",
                    fieldName, testClassName, fieldTypeName);

            checkState(!p.hasAnyAnnotations(Real.class, Inject.class),
                    "Field '%s' in test class '%s' is annotated with @Real or @Inject. "
                    + "@Real and @Inject annotations are not supported for unit tests. "
                    + "Please use @Fake instead.",
                    fieldName, testClassName
            );

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
            Collection<ParameterDescriptor> paramDescriptors = cutDescriptor.getParameterDescriptors();

            String cutClassName = cutDescriptor.getTypeName();

            if (!testDescriptor.getCollaboratorProvider().isPresent()) {
                List<ParameterDescriptor> undeclared = new ArrayList<>(paramDescriptors);
                paramDescriptors.stream().forEach(p -> {
                    testDescriptor.getFieldDescriptors().
                            stream()
                            .filter(fd -> p.isSubtypeOf(fd.getGenericType()) && fd.getValue(testInstance).isPresent())
                            .forEach(fd -> {
                                undeclared.remove(p);
                            });

                });

                undeclared
                        .stream()
                        .map((p) -> p.getTypeName())
                        .forEach((paramTypeName) -> {
                            testContext.warn("Class under test '{}' defined in '{}' has a collaborator "
                                    + "type '{}' but test class '{}' does not define a field of "
                                    + "type '{}' annotated with @Fake. Null values will be used.",
                                    cutClassName, testClassName, paramTypeName, testClassName, paramTypeName);
                        });
            }
        }
    }

}
