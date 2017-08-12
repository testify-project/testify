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

import java.util.List;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.Lenient;
import org.testifyproject.extension.annotation.Loose;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that collaborator provider methods are configured correctly.
 *
 * @author saden
 */
@Strict
@Lenient
@Loose
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class CollaboratorProviderPreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        testDescriptor.getCollaboratorProviders().parallelStream().forEach(collaboratorProvider -> {
            List<Class> parameterTypes = collaboratorProvider.getParameterTypes();
            int size = parameterTypes.size();
            String name = collaboratorProvider.getName();
            String declaringClassName = collaboratorProvider.getDeclaringClassName();
            Class returnType = collaboratorProvider.getReturnType();

            ExceptionUtil.INSTANCE.raise(size > 0,
                    "Collaborator Provider method '{}' in class '{}' has {} paramters. "
                    + "Please insure the configuration handler has no paramters.",
                    name, declaringClassName, size);

            ExceptionUtil.INSTANCE.raise(returnType.equals(void.class) || returnType.equals(Void.class),
                    "Collaborator Provider method '{}' in class '{}' has void return type."
                    + "Please insure the collaborator provider returns a non-void type.",
                    name, declaringClassName);
        });
    }

}
