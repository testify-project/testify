/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.level.integration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.testify.CutDescriptor;
import org.testify.FieldDescriptor;
import org.testify.InvokableDescriptor;
import org.testify.MethodDescriptor;
import org.testify.ReificationProvider;
import org.testify.ServiceInstance;
import org.testify.ServiceProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.TestRunner;
import static org.testify.core.impl.TestContextProperties.CUT_INSTANCE;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.tools.Discoverable;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@Discoverable
public class IntegrationTestRunner implements TestRunner {

    private TestContext testContext;
    private ServiceInstance serviceInstance;
    private ReificationProvider reificationProvider;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;
        IntegrationTestVerifier verifier = new IntegrationTestVerifier(testContext);
        verifier.dependency();
        verifier.configuration();

        ServiceProvider serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);

        Object serviceContext = serviceProvider.create(testContext);

        serviceInstance = serviceProvider.configure(testContext, serviceContext);
        serviceInstance.addConstant(testContext, null, TestContext.class);

        serviceProvider.postConfigure(testContext, serviceInstance);

        TestReifier testReifier = testContext.getTestReifier();
        testReifier.configure(serviceContext);

        reificationProvider = ServiceLocatorUtil.INSTANCE.getOne(ReificationProvider.class);
        reificationProvider.start(testContext, serviceInstance);

        verifier.wiring();

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<CutDescriptor> cutDescriptor = testContext.getCutDescriptor();
        Optional<Object> cutProperty = testContext.getProperty(CUT_INSTANCE);
        Optional<InvokableDescriptor> collaboratorMethod = testDescriptor.getCollaboratorProvider();

        if (cutProperty.isPresent() && cutDescriptor.isPresent() && collaboratorMethod.isPresent()) {
            Object cutInstance = cutProperty.get();

            InvokableDescriptor invokableDescriptor = collaboratorMethod.get();
            Optional<Object> result;

            if (invokableDescriptor.getInstance().isPresent()) {
                result = invokableDescriptor.invoke();
            } else {
                MethodDescriptor methodDescriptor = invokableDescriptor.getMethodDescriptor();
                result = methodDescriptor.invoke(testInstance);
            }

            if (result.isPresent()) {
                Object resultValue = result.get();
                Object[] collaborators;

                if (resultValue.getClass().isArray()) {
                    collaborators = (Object[]) resultValue;
                } else if (resultValue instanceof Collection) {
                    collaborators = ((Collection) resultValue).stream().toArray();
                } else {
                    collaborators = new Object[]{};
                }

                Set<FieldDescriptor> replacedFields = new HashSet<>();

                for (Object collaborator : collaborators) {
                    cutDescriptor.get().getFieldDescriptors().stream()
                            .filter(fieldDescriptor
                                    -> !replacedFields.contains(fieldDescriptor)
                            && fieldDescriptor.isSupertypeOf(collaborator.getClass()))
                            .map(fieldDescriptor -> {
                                fieldDescriptor.setValue(cutInstance, collaborator);

                                return fieldDescriptor;
                            })
                            .forEach(fieldDescriptor -> {
                                //TODO: you can end up with a cut class that has
                                //multiple fields of the same type. we should
                                //log any ambiguity
                                replacedFields.add(fieldDescriptor);
                            });
                }
            }
        }

        //invoke init method on test fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.init(testInstance));

        //invoke init method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.init(testInstance));
    }

    @Override
    public void stop() {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.destroy(testInstance));

        if (reificationProvider != null) {
            reificationProvider.destroy(testContext, serviceInstance);
        }

        if (serviceInstance != null) {
            serviceInstance.destroy();
        }
    }

}
