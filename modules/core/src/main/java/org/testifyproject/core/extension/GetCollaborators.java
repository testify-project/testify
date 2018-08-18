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

import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.extension.Operation;

/**
 * Get collaborators for a method.
 *
 * @author saden
 */
public class GetCollaborators implements Operation<Object[]> {

    private final TestDescriptor testDescriptor;
    private final MethodDescriptor methodDescriptor;
    private final Object testInstance;

    public GetCollaborators(TestDescriptor testDescriptor,
            MethodDescriptor methodDescriptor,
            Object testInstance) {
        this.testDescriptor = testDescriptor;
        this.methodDescriptor = methodDescriptor;
        this.testInstance = testInstance;
    }

    @Override
    public Object[] execute() {
        return methodDescriptor.getParameters().stream()
                .map(parameter -> {
                    return new GetCollaborator(testDescriptor, parameter, testInstance)
                            .execute();
                }).toArray();
    }

}
