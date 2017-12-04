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
package org.testifyproject.level.unit;

import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.testifyproject.ResourceController;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.Hint;
import org.testifyproject.core.DefaultServiceProvider;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.SutReifier;
import org.testifyproject.extension.Verifier;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@UnitCategory
@Discoverable
public class UnitTestRunner implements TestRunner {

    private final ServiceLocatorUtil serviceLocatorUtil;
    ResourceController resourceController;

    public UnitTestRunner() {
        this(ServiceLocatorUtil.INSTANCE);
    }

    UnitTestRunner(ServiceLocatorUtil serviceLocatorUtil) {
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Collection<Class<? extends Annotation>> guidelines = testDescriptor.getGuidelines();

        serviceLocatorUtil
                .findAllWithFilter(PreVerifier.class, guidelines, UnitCategory.class)
                .forEach(p -> p.verify(testContext));
        testContext.verify();

        resourceController = serviceLocatorUtil.getOne(ResourceController.class);
        resourceController.start(testContext);

        ServiceProvider serviceProvider = serviceLocatorUtil.getFromHintOrDefault(
                testContext,
                ServiceProvider.class,
                DefaultServiceProvider.class,
                Hint::serviceProvider);

        Object serviceContext = serviceProvider.create(testContext);
        testConfigurer.configure(testContext, serviceContext);

        ServiceInstance serviceInstance =
                serviceProvider.configure(testContext, serviceContext);
        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);

        serviceProvider.postConfigure(testContext, serviceInstance);

        serviceLocatorUtil.findAllWithFilter(SutReifier.class, UnitCategory.class)
                .forEach(p -> p.reify(testContext));

        if (testDescriptor.getCollaboratorProvider().isPresent()) {
            serviceLocatorUtil.findAllWithFilter(InitialReifier.class, UnitCategory.class)
                    .forEach(p -> p.reify(testContext));
        }

        serviceLocatorUtil
                .findAllWithFilter(CollaboratorReifier.class, UnitCategory.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil.findAllWithFilter(FinalReifier.class, UnitCategory.class)
                .forEach(p -> p.reify(testContext));

        //invoke init method on test fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.init(testInstance));

        //invoke init method on sut field annotated with Fixture
        testContext.getSutDescriptor()
                .ifPresent(p -> p.init(testInstance));

        serviceLocatorUtil
                .findAllWithFilter(Verifier.class, guidelines, UnitCategory.class)
                .forEach(p -> p.verify(testContext));
        testContext.verify();
    }

    @Override
    public void stop(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Collection<Class<? extends Annotation>> guidelines = testDescriptor.getGuidelines();

        serviceLocatorUtil
                .findAllWithFilter(PostVerifier.class, guidelines, UnitCategory.class)
                .forEach(p -> p.verify(testContext));
        testContext.verify();

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        testContext.getSutDescriptor()
                .ifPresent(p -> p.destroy(testInstance));

        testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)
                .ifPresent(ServiceInstance::destroy);

        resourceController.stop(testContext);
    }

}
