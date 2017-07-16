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
package org.testifyproject.core.reifier;

import java.util.Collection;
import java.util.Optional;
import org.testifyproject.SutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 * A class that reifies the sut and test based on the presence of
 * {@link org.testifyproject.annotation.CollaboratorProvider} class on the test
 * class.
 *
 * @author saden
 */
@UnitCategory
@IntegrationCategory
@Discoverable
public class CollaboratorsInitialReifier implements InitialReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();

        testContext.getSutDescriptor()
                .ifPresent(sutDescriptor
                        -> sutDescriptor.getValue(testInstance)
                        .ifPresent(sutValue
                                -> testContext.getTestDescriptor().getCollaboratorProvider()
                                .ifPresent(methodDescriptor
                                        -> getCollaborators(methodDescriptor, testInstance)
                                        .ifPresent(collaborators
                                                -> processCollaborators(
                                                testContext,
                                                sutDescriptor,
                                                sutValue,
                                                convertToArray(collaborators))
                                        )
                                )));
    }

    public void processCollaborators(TestContext testContext,
            SutDescriptor sutDescriptor,
            Object sutValue,
            Object[] collaborators) {

        for (Object collaborator : collaborators) {
            if (collaborator == null) {
                continue;
            }

            Class collaboratorType = collaborator.getClass();

            if (testContext.getMockProvider().isMock(collaborator)) {
                Class collaboratorSuperclass = collaborator.getClass().getSuperclass();
                Class[] collaboratorInterfaces = collaborator.getClass().getInterfaces();
                collaboratorType = !Object.class.equals(collaboratorSuperclass)
                        ? collaboratorSuperclass
                        : collaboratorInterfaces.length != 0
                                ? collaboratorInterfaces[0]
                                : collaboratorType;
            }

            Optional<FieldDescriptor> foundFieldDescriptor
                    = sutDescriptor.findFieldDescriptor(collaboratorType);

            if (foundFieldDescriptor.isPresent()) {
                FieldDescriptor fieldDescriptor = foundFieldDescriptor.get();
                fieldDescriptor.setValue(sutValue, collaborator);
            }
        }
    }

    public Object[] convertToArray(Object value) {
        Object[] collaborators;

        if (value.getClass().isArray()) {
            collaborators = (Object[]) value;
        } else if (value instanceof Collection) {
            collaborators = ((Collection) value).stream().toArray();
        } else {
            throw ExceptionUtil.INSTANCE.propagate(
                    "Collaborator provided ({}) must be of type Object[] or Collection.",
                    value.getClass().getSimpleName());
        }

        return collaborators;
    }

    public Optional<Object> getCollaborators(MethodDescriptor methodDescriptor, Object testInstance) {
        return methodDescriptor.getInstance()
                .map(instance -> methodDescriptor.invoke(instance))
                .orElseGet(() -> methodDescriptor.invoke(testInstance));
    }

}
