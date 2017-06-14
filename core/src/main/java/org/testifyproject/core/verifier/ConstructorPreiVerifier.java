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
package org.testifyproject.core.verifier;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that the system under test constructor parameters are defined as
 * collaborators on the test class.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@Discoverable
public class ConstructorPreiVerifier implements PreiVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        String testClassName = testDescriptor.getTestClassName();

        Optional<MethodDescriptor> foundCollaboratorProvider = testDescriptor.getCollaboratorProvider();

        if (!foundCollaboratorProvider.isPresent()) {
            testContext.getSutDescriptor().ifPresent(sutDescriptor -> {
                String sutClassName = sutDescriptor.getTypeName();
                Collection<ParameterDescriptor> paramDescriptors = sutDescriptor.getParameterDescriptors();

                List<ParameterDescriptor> undeclared = paramDescriptors.stream().collect(toList());

                paramDescriptors.stream().forEach(parameterDescriptor
                        -> testDescriptor.getFieldDescriptors().stream()
                                .filter(fieldDescriptor -> parameterDescriptor.isSubtypeOf(fieldDescriptor.getGenericType()))
                                .filter(fieldDescriptor -> fieldDescriptor.getValue(testInstance).isPresent())
                                .forEach(fieldDescriptor -> undeclared.remove(parameterDescriptor))
                );

                undeclared.stream()
                        .map(ParameterDescriptor::getTypeName)
                        .forEach(paramTypeName
                                -> LoggingUtil.INSTANCE.warn(
                                "System under test '{}' defined in '{}' has a collaborator "
                                + "of type '{}' but test class '{}' does not define a field of "
                                + "type '{}' annotated with @Fake, @Real, or @Virtual.",
                                sutClassName, testClassName, paramTypeName, testClassName, paramTypeName));
            });
        }
    }

}
