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
import java.util.Optional;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that collaborator provider methods are configured correctly.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@SystemTest
@Discoverable
public class CollaboratorProviderConfigurationVerifier implements ConfigurationVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Optional<MethodDescriptor> foundCollaboratorProvider = testDescriptor.getCollaboratorProvider();

        if (foundCollaboratorProvider.isPresent()) {
            MethodDescriptor collaboratorProvider = foundCollaboratorProvider.get();
            Class<?> returnType = collaboratorProvider.getReturnType();

            ExceptionUtil.INSTANCE.raise(!(Object[].class.isAssignableFrom(returnType)
                    || Collection.class.isAssignableFrom(returnType)),
                    "Configuration provider method '{}' in '{}' return type '{}' is invalid. "
                    + "Configuration provider methods must return an instance of "
                    + "java.lang.Object[] or java.util.Collection.",
                    collaboratorProvider.getName(), collaboratorProvider.getDeclaringClassName(), returnType.getName());
        }

    }

}
