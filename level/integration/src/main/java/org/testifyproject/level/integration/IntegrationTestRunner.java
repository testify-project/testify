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
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.testifyproject.ResourceController;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.extension.annotation.Hint;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.tools.Discoverable;

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

        resourceController = serviceLocatorUtil.getOne(ResourceController.class);
        resourceController.start(testContext);

        serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class,
                IntegrationCategory.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil.findAllWithFilter(PreVerifier.class, testDescriptor
                .getGuidelines(), IntegrationCategory.class)
                .forEach(p -> p.verify(testContext));

        ServiceProvider serviceProvider = getServiceProvider(testDescriptor);

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

        serviceLocatorUtil.findAllWithFilter(PreiVerifier.class, testDescriptor
                .getGuidelines(), IntegrationCategory.class)
                .forEach(p -> p.verify(testContext));
    }

    ServiceProvider getServiceProvider(TestDescriptor testDescriptor) {
        ServiceProvider serviceProvider;
        Optional<Class<? extends ServiceProvider>> foundServiceProvider = testDescriptor
                .getHint()
                .map(Hint::serviceProvider)
                .filter(((Predicate) ServiceProvider.class::equals).negate());
        if (foundServiceProvider.isPresent()) {
            serviceProvider = serviceLocatorUtil.getOne(ServiceProvider.class,
                    foundServiceProvider.get());
        } else {
            serviceProvider = serviceLocatorUtil.getOneWithFilter(ServiceProvider.class,
                    IntegrationCategory.class);
        }
        return serviceProvider;
    }

    @Override
    public void stop(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        serviceLocatorUtil.findAllWithFilter(PostVerifier.class, testDescriptor
                .getGuidelines(), IntegrationCategory.class)
                .forEach(p -> p.verify(testContext));

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
