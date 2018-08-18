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

import java.util.Collection;
import java.util.Optional;

import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.extension.Query;

/**
 * TODO.
 *
 * @author saden
 */
public class FindCollaborators implements Query<Object> {

    private final TestDescriptor testDescriptor;
    private final MethodDescriptor methodDescriptor;
    private final Object testInstance;

    public FindCollaborators(TestDescriptor testDescriptor,
            MethodDescriptor methodDescriptor,
            Object testInstance) {
        this.testDescriptor = testDescriptor;
        this.methodDescriptor = methodDescriptor;
        this.testInstance = testInstance;
    }

    @Override
    public Optional<Object> execute() {
        Object[] collaborators = new GetCollaborators(
                testDescriptor,
                methodDescriptor,
                testInstance
        ).execute();

        return methodDescriptor.getInstance()
                .map(instance -> {
                    return methodDescriptor.invoke(instance, convertToArray(collaborators));
                })
                .orElseGet(() -> {
                    return methodDescriptor.invoke(testInstance, convertToArray(collaborators));
                });
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

}
