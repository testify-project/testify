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
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.extension.CollaboratorsReifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies the cut and test based on the presence of
 * {@link org.testifyproject.annotation.CollaboratorProvider} class on the test
 * class.
 *
 * @author saden
 */
@UnitTest
@IntegrationTest
@Discoverable
public class DefaultCollaboratorsRefier implements CollaboratorsReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();

        testContext.getCutDescriptor()
                .ifPresent(cutDescriptor
                        -> cutDescriptor.getValue(testInstance)
                        .ifPresent(cutInstance
                                -> testContext.getTestDescriptor().getCollaboratorProvider()
                                .ifPresent(methodDescriptor
                                        -> getCollaborators(methodDescriptor, testInstance)
                                        .ifPresent(collaborators
                                                -> processCollaborator(
                                                testContext,
                                                cutDescriptor,
                                                cutInstance,
                                                convertToArray(collaborators))
                                        )
                                )));
    }

    void processCollaborator(TestContext testContext,
            CutDescriptor cutDescriptor,
            Object cutInstance,
            Object[] collaborators) {

        for (Object collaborator : collaborators) {
            if (collaborator == null) {
                continue;
            }

            Class collaboratorType;

            if (testContext.getMockProvider().isMock(collaborator)) {
                Class collaboratorSuperclass = collaborator.getClass().getSuperclass();
                Class[] collaboratorInterfaces = collaborator.getClass().getInterfaces();
                collaboratorType = !collaboratorSuperclass.equals(Object.class)
                        ? collaboratorSuperclass
                        : collaboratorInterfaces[0];
            } else {
                collaboratorType = collaborator.getClass();
            }

            Optional<FieldDescriptor> foundFieldDescriptor
                    = cutDescriptor.findFieldDescriptor(collaboratorType);

            if (foundFieldDescriptor.isPresent()) {
                FieldDescriptor fieldDescriptor = foundFieldDescriptor.get();
                fieldDescriptor.setValue(cutInstance, collaborator);
            }
        }
    }

    Object[] convertToArray(Object resultValue) {
        Object[] collaborators;
        if (resultValue.getClass().isArray()) {
            collaborators = (Object[]) resultValue;
        } else if (resultValue instanceof Collection) {
            collaborators = ((Collection) resultValue).stream().toArray();
        } else {
            collaborators = new Object[]{};
        }
        return collaborators;
    }

    Optional<Object> getCollaborators(MethodDescriptor methodDescriptor, Object testInstance) {
        Optional<Object> instance = methodDescriptor.getInstance();
        Optional<Object> result;
        if (instance.isPresent()) {
            result = methodDescriptor.invoke(instance.get());
        } else {
            result = methodDescriptor.invoke(testInstance);
        }
        return result;
    }

}
