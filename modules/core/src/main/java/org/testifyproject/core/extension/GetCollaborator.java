/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.core.extension;

import java.lang.reflect.Parameter;
import java.util.Optional;

import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.Operation;

/**
 * Get a collaborator for a method parameter.
 *
 * @author saden
 */
public class GetCollaborator implements Operation<Object> {

    private final TestDescriptor testDescriptor;
    private final Parameter parameter;
    private final Object testInstance;

    public GetCollaborator(TestDescriptor testDescriptor, Parameter parameter,
            Object testInstance) {
        this.testDescriptor = testDescriptor;
        this.parameter = parameter;
        this.testInstance = testInstance;
    }

    @Override
    public Object execute() {
        Optional<MethodDescriptor> foundMethodDescriptor =
                new FindCollaboratorProvider(testDescriptor, parameter)
                        .execute();

        ExceptionUtil.INSTANCE.raise(!foundMethodDescriptor.isPresent(),
                "Could not find a provider for argument '{} {}'.",
                parameter.getType().getSimpleName(),
                parameter.getName());

        return foundMethodDescriptor.map(parameterMethodDescriptor -> {
            FindCollaborators query = new FindCollaborators(
                    testDescriptor,
                    parameterMethodDescriptor,
                    testInstance);
            return query.execute().orElse(null);
        }).orElse(null);
    }

}
