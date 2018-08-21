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
import org.testifyproject.annotation.Name;
import org.testifyproject.extension.Query;

/**
 * A query implementation used to find a collaborator provider for a method parameter.
 *
 * @author saden
 */
public class FindCollaboratorProvider implements Query<MethodDescriptor> {

    private final TestDescriptor testDescriptor;
    private final Parameter parameter;

    public FindCollaboratorProvider(TestDescriptor testDescriptor, Parameter parameter) {
        this.testDescriptor = testDescriptor;
        this.parameter = parameter;
    }

    @Override
    public Optional<MethodDescriptor> execute() {
        Class<?> parameterType = parameter.getType();
        Name name = parameter.getDeclaredAnnotation(Name.class);
        Optional<MethodDescriptor> foundMethodDescriptor;

        if (name == null) {
            //find a method that returns the same type and has the same name as the parameter
            foundMethodDescriptor = testDescriptor.findCollaboratorProvider(
                    parameterType,
                    parameter.getName()
            );

            //if one is not found then just use parameter type matching
            if (!foundMethodDescriptor.isPresent()) {
                foundMethodDescriptor = testDescriptor.findCollaboratorProvider(parameterType);
            }
        } else {
            //if a name is specified then use it to find the right method
            foundMethodDescriptor = testDescriptor.findCollaboratorProvider(
                    parameterType,
                    name.value()
            );
        }

        return foundMethodDescriptor;
    }

}
