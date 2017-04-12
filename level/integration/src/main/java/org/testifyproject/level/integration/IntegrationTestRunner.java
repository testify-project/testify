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
package org.testifyproject.level.integration;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import org.testifyproject.CutDescriptor;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorsReifier;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.WiringVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.TestConfigurer;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@IntegrationTest
@Discoverable
public class IntegrationTestRunner implements TestRunner {

    private TestContext testContext;
    private ServiceInstance serviceInstance;
    private TestResourcesProvider testResourcesProvider;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;
        Object testInstance = testContext.getTestInstance();

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(FieldReifier.class, IntegrationTest.class)
                .forEach(p -> p.reify(testContext));

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(ConfigurationVerifier.class, IntegrationTest.class)
                .forEach(p -> p.verify(testContext));

        ServiceProvider serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);

        Object serviceContext = serviceProvider.create(testContext);

        serviceInstance = serviceProvider.configure(testContext, serviceContext);
        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);
        serviceInstance.addConstant(testContext, null, TestContext.class);

        serviceProvider.postConfigure(testContext, serviceInstance);

        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        testConfigurer.configure(testContext, serviceContext);

        testResourcesProvider = ServiceLocatorUtil.INSTANCE.getOne(TestResourcesProvider.class);
        testResourcesProvider.start(testContext, serviceInstance);
        Optional<CutDescriptor> foundCutDescriptor = testContext.getCutDescriptor();

        foundCutDescriptor.ifPresent(cutDescriptor -> {
            Set<Class<? extends Annotation>> nameQualifers = serviceInstance.getNameQualifers();
            Set<Class<? extends Annotation>> customQualifiers = serviceInstance.getCustomQualifiers();
            Class cutType = cutDescriptor.getType();

            Annotation[] qualifierAnnotations
                    = cutDescriptor.getMetaAnnotations(nameQualifers, customQualifiers);

            Object cutInstance = serviceInstance.getService(cutType, qualifierAnnotations);
            cutDescriptor.setValue(testInstance, cutInstance);
        });

        if (testContext.getTestDescriptor().getCollaboratorProvider().isPresent()) {
            ServiceLocatorUtil.INSTANCE.findAllWithFilter(CollaboratorsReifier.class, IntegrationTest.class)
                    .forEach(p -> p.reify(testContext));
        }

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(org.testifyproject.extension.TestReifier.class, IntegrationTest.class)
                .forEach(p -> p.reify(testContext));

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(WiringVerifier.class, IntegrationTest.class)
                .forEach(p -> p.verify(testContext));

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

        if (testResourcesProvider != null) {
            testResourcesProvider.destroy(testContext, serviceInstance);
        }

        if (serviceInstance != null) {
            serviceInstance.destroy();
        }
    }

}
