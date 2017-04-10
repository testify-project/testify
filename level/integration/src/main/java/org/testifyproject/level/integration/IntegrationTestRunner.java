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

import org.testifyproject.ReificationProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.TestRunner;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.ConfigurationVerifier;
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

    private TestContext testContext;
    private ServiceInstance serviceInstance;
    private ReificationProvider reificationProvider;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(ConfigurationVerifier.class, IntegrationTest.class)
                .forEach(p -> p.verify(testContext));

        ServiceProvider serviceProvider = ServiceLocatorUtil.INSTANCE.getOne(ServiceProvider.class);

        Object serviceContext = serviceProvider.create(testContext);

        serviceInstance = serviceProvider.configure(testContext, serviceContext);
        serviceInstance.addConstant(testContext, null, TestContext.class);

        serviceProvider.postConfigure(testContext, serviceInstance);

        TestReifier testReifier = testContext.getTestReifier();
        testReifier.configure(testContext, serviceContext);

        reificationProvider = ServiceLocatorUtil.INSTANCE.getOne(ReificationProvider.class);
        reificationProvider.start(testContext, serviceInstance);

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(WiringVerifier.class, IntegrationTest.class)
                .forEach(p -> p.verify(testContext));

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();

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
