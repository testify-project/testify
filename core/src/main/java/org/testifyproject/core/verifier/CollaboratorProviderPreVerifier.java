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
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 * Insure that collaborator provider methods are configured correctly.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class CollaboratorProviderPreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        testDescriptor.getCollaboratorProvider().ifPresent(collaboratorProvider -> {
            Class<?> returnType = collaboratorProvider.getReturnType();
            String methodName = collaboratorProvider.getName();
            String declaringClassName = collaboratorProvider.getDeclaringClassName();
            String returnTypeName = returnType.getSimpleName();

            ExceptionUtil.INSTANCE.raise(!(Object[].class.isAssignableFrom(returnType)
                    || Collection.class.isAssignableFrom(returnType)),
                    "Collaborator provider method '{}' in '{}' has an invalid return type ('{}'). "
                    + "Collaborator provider methods must return an instance of "
                    + "java.lang.Object[] or java.util.Collection.",
                    methodName, declaringClassName, returnTypeName);
        });
    }

}
