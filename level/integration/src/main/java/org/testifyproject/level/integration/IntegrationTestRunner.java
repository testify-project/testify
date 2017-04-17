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
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.TestRunner;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorsReifier;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.WiringVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@IntegrationTest
@Discoverable
public class IntegrationTestRunner implements TestRunner {

    TestContext testContext;
    ServiceInstance serviceInstance;
    TestResourcesProvider testResourcesProvider;

    private final ServiceLocatorUtil serviceLocatorUtil;

    public IntegrationTestRunner() {
        this(ServiceLocatorUtil.INSTANCE);
    }

    IntegrationTestRunner(ServiceLocatorUtil serviceLocatorUtil) {
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;
        Object testInstance = testContext.getTestInstance();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        Optional<CutDescriptor> foundCutDescriptor = testContext.getCutDescriptor();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        serviceLocatorUtil.findAllWithFilter(FieldReifier.class, IntegrationTest.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil.findAllWithFilter(ConfigurationVerifier.class, IntegrationTest.class)
                .forEach(p -> p.verify(testContext));

        ServiceProvider serviceProvider = serviceLocatorUtil.getOne(ServiceProvider.class);

        Object serviceContext = serviceProvider.create(testContext);

        serviceInstance = serviceProvider.configure(testContext, serviceContext);
        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);
        serviceInstance.addConstant(testContext, null, TestContext.class);

        serviceProvider.postConfigure(testContext, serviceInstance);
        testConfigurer.configure(testContext, serviceContext);

        testResourcesProvider = serviceLocatorUtil.getOne(TestResourcesProvider.class);
        testResourcesProvider.start(testContext, serviceInstance);

        //XXX: Some DI framework (i.e. Spring) require that the service instance
        //context be initialized. We need to do the initialization after the
        //required resources have started so that resources can dynamically
        //added to the service instance and eligiable for injection into the
        //test class and test fixtures.
        serviceInstance.init();

        foundCutDescriptor.ifPresent(cutDescriptor -> {
            Set<Class<? extends Annotation>> nameQualifers = serviceInstance.getNameQualifers();
            Set<Class<? extends Annotation>> customQualifiers = serviceInstance.getCustomQualifiers();
            Class cutType = cutDescriptor.getType();

            Annotation[] cutQualifiers
                    = cutDescriptor.getMetaAnnotations(nameQualifers, customQualifiers);

            Object cutInstance = serviceInstance.getService(cutType, cutQualifiers);
            cutDescriptor.setValue(testInstance, cutInstance);
        });

        if (testDescriptor.getCollaboratorProvider().isPresent()) {
            serviceLocatorUtil.findAllWithFilter(CollaboratorsReifier.class, IntegrationTest.class)
                    .forEach(p -> p.reify(testContext));
        }

        serviceLocatorUtil.findAllWithFilter(TestReifier.class, IntegrationTest.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil.findAllWithFilter(WiringVerifier.class, IntegrationTest.class)
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
            testResourcesProvider.stop(testContext);
        }

        if (serviceInstance != null) {
            serviceInstance.destroy();
        }
    }

}
