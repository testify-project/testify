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

import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.testifyproject.ResourceController;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.Hint;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.Verifier;
import org.testifyproject.extension.annotation.IntegrationCategory;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@IntegrationCategory
@Discoverable
public class IntegrationTestRunner implements TestRunner {

    private final ServiceLocatorUtil serviceLocatorUtil;
    ResourceController resourceController;

    public IntegrationTestRunner() {
        this(ServiceLocatorUtil.INSTANCE);
    }

    IntegrationTestRunner(ServiceLocatorUtil serviceLocatorUtil) {
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        Optional<SutDescriptor> foundSutDescriptor = testContext.getSutDescriptor();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Collection<Class<? extends Annotation>> guidelines = testDescriptor.getGuidelines();

        resourceController = serviceLocatorUtil.getOne(ResourceController.class);
        resourceController.start(testContext);

        serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class,
                IntegrationCategory.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil
                .findAllWithFilter(PreVerifier.class, guidelines, IntegrationCategory.class)
                .forEach(p -> p.verify(testContext));
        testContext.verify();

        ServiceProvider serviceProvider = serviceLocatorUtil.getFromHintWithFilter(
                testContext,
                ServiceProvider.class,
                Hint::serviceProvider
        );

        Object serviceContext = serviceProvider.create(testContext);
        testConfigurer.configure(testContext, serviceContext);
        ServiceInstance serviceInstance = serviceProvider.configure(testContext, serviceContext);
        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);

        foundSutDescriptor.ifPresent(sutDescriptor -> {
            Set<Class<? extends Annotation>> nameQualifers =
                    serviceInstance.getNameQualifers();
            Set<Class<? extends Annotation>> customQualifiers =
                    serviceInstance.getCustomQualifiers();
            Class sutType = sutDescriptor.getType();

            Annotation[] sutQualifiers = sutDescriptor
                    .getMetaAnnotations(nameQualifers, customQualifiers);

            Object sutInstance = serviceInstance.getService(sutType, sutQualifiers);
            sutDescriptor.setValue(testInstance, sutInstance);
        });

        if (testDescriptor.getCollaboratorProvider().isPresent()) {
            serviceLocatorUtil.findAllWithFilter(InitialReifier.class,
                    IntegrationCategory.class)
                    .forEach(p -> p.reify(testContext));
        }

        serviceLocatorUtil
                .findAllWithFilter(FinalReifier.class, IntegrationCategory.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil
                .findAllWithFilter(Verifier.class, guidelines, IntegrationCategory.class)
                .forEach(p -> p.verify(testContext));
        testContext.verify();
    }

    @Override
    public void stop(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Collection<Class<? extends Annotation>> guidelines = testDescriptor.getGuidelines();

        serviceLocatorUtil
                .findAllWithFilter(PostVerifier.class, guidelines, IntegrationCategory.class)
                .forEach(p -> p.verify(testContext));
        testContext.verify();

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        testContext.getSutDescriptor()
                .ifPresent(p -> p.destroy(testInstance));

        if (resourceController != null) {
            resourceController.stop(testContext);
        }

        testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)
                .ifPresent(ServiceInstance::destroy);
    }

}
