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

import java.lang.annotation.Annotation;
import java.util.Set;
import org.testifyproject.MockProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Real;
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class that reifies test class fields that have not been initialized.
 *
 * @author saden
 */
@IntegrationTest
@SystemTest
@Discoverable
public class ServiceTestReifier implements TestReifier {

    @Override
    public void reify(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        testContext.getServiceInstance().ifPresent(serviceInstance -> {
            MockProvider mockProvider = testContext.getMockProvider();
            Set<Class<? extends Annotation>> nameQualifers = serviceInstance.getNameQualifers();
            Set<Class<? extends Annotation>> customQualifiers = serviceInstance.getCustomQualifiers();

            //if there are any fields on the test class that are not collaborators
            //of the cut class and are annotated with DI supported injection
            //annotations then get the services and initialize the test fields.
            testContext.getTestDescriptor().getFieldDescriptors().parallelStream()
                    .filter(p -> !p.getValue(testInstance).isPresent() && p.hasAnyAnnotations(Real.class))
                    .forEach(fieldDescriptor -> {
                        Class fieldType = fieldDescriptor.getType();
                        Annotation[] fieldQualifiers
                                = fieldDescriptor.getMetaAnnotations(nameQualifers, customQualifiers);

                        Object value = serviceInstance.getService(fieldType, fieldQualifiers);

                        if (value != null) {
                            if (fieldDescriptor.getVirtual().isPresent()) {
                                value = mockProvider.createVirtual(fieldDescriptor.getType(), value);
                            }

                            fieldDescriptor.setValue(testInstance, value);
                            fieldDescriptor.init(value);
                        }
                    });
        });

    }

}
