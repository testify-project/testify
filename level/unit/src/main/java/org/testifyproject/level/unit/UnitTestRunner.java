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

import java.util.Optional;
import java.util.function.Predicate;

import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.TestRunner;
import org.testifyproject.core.DefaultServiceProvider;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreInstanceProvider;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.extension.SutReifier;
import org.testifyproject.extension.annotation.Hint;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.tools.Discoverable;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@UnitCategory
@Discoverable
public class UnitTestRunner implements TestRunner {

    private final ServiceLocatorUtil serviceLocatorUtil;
    TestResourcesProvider testResourcesProvider;

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

        serviceLocatorUtil.findAllWithFilter(PreVerifier.class, testDescriptor
                .getGuidelines(), UnitCategory.class)
                .forEach(p -> p.verify(testContext));

        ServiceProvider serviceProvider;

        Optional<Class<? extends ServiceProvider>> foundServiceProvider = testDescriptor
                .getHint()
                .map(Hint::serviceProvider)
                .filter(((Predicate) ServiceProvider.class::equals).negate());

        if (foundServiceProvider.isPresent()) {
            serviceProvider = serviceLocatorUtil.getOne(ServiceProvider.class,
                    foundServiceProvider.get());
        } else {
            serviceProvider = serviceLocatorUtil.getOne(ServiceProvider.class,
                    DefaultServiceProvider.class);
        }

        Object serviceContext = serviceProvider.create(testContext);

        ServiceInstance serviceInstance = serviceProvider.configure(testContext,
                serviceContext);
        testContext.addProperty(SERVICE_INSTANCE, serviceInstance);

        serviceProvider.postConfigure(testContext, serviceInstance);
        testConfigurer.configure(testContext, serviceContext);

        testResourcesProvider = serviceLocatorUtil.getOne(TestResourcesProvider.class);
        testResourcesProvider.start(testContext);

        //XXX: Some DI framework (i.e. Spring) require that the service instance
        //context be initialized. We need to do the initialization after the
        //required resources have started so that resources can dynamically
        //added to the service instance and eligiable for injection into the
        //test class and test fixtures.
        serviceInstance.init();

        //add constant instances
        serviceLocatorUtil
                .findAllWithFilter(PreInstanceProvider.class, UnitCategory.class)
                .stream()
                .flatMap(p -> p.get(testContext).stream())
                .forEach(serviceInstance::replace);

        serviceLocatorUtil.findAllWithFilter(InstanceProvider.class)
                .stream()
                .flatMap(p -> p.get(testContext).stream())
                .forEach(serviceInstance::replace);

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

        serviceLocatorUtil.findAllWithFilter(PreiVerifier.class, testDescriptor
                .getGuidelines(), UnitCategory.class)
                .forEach(p -> p.verify(testContext));
    }

    @Override
    public void stop(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        serviceLocatorUtil.findAllWithFilter(PostVerifier.class, testDescriptor
                .getGuidelines(), UnitCategory.class)
                .forEach(p -> p.verify(testContext));

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        testContext.getSutDescriptor()
                .ifPresent(p -> p.destroy(testInstance));

        testResourcesProvider.stop(testContext);
    }

}
