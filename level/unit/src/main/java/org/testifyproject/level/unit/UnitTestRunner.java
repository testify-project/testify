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

import java.util.Optional;
import org.testifyproject.CutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorsReifier;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.CutReifier;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.WiringVerifier;
import org.testifyproject.extension.annotation.UnitTest;
import org.testifyproject.tools.Discoverable;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@UnitTest
@Discoverable
public class UnitTestRunner implements TestRunner {

    private final ServiceLocatorUtil serviceLocatorUtil;

    public UnitTestRunner() {
        this(ServiceLocatorUtil.INSTANCE);
    }

    UnitTestRunner(ServiceLocatorUtil serviceLocatorUtil) {
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        serviceLocatorUtil.findAllWithFilter(ConfigurationVerifier.class, UnitTest.class)
                .forEach(p -> p.verify(testContext));

        serviceLocatorUtil.findAllWithFilter(CutReifier.class, UnitTest.class)
                .forEach(p -> p.reify(testContext));

        if (testDescriptor.getCollaboratorProvider().isPresent()) {
            serviceLocatorUtil.findAllWithFilter(CollaboratorsReifier.class, UnitTest.class)
                    .forEach(p -> p.reify(testContext));
        }

        serviceLocatorUtil.findAllWithFilter(FieldReifier.class, UnitTest.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil.findAllWithFilter(TestReifier.class, UnitTest.class)
                .forEach(p -> p.reify(testContext));

        //invoke init method on test fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.init(testInstance));

        //invoke init method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.init(testInstance));

        serviceLocatorUtil.findAllWithFilter(WiringVerifier.class, UnitTest.class)
                .forEach(p -> p.verify(testContext));
    }

    @Override
    public void stop(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Optional<CutDescriptor> cutDescriptor = testContext.getCutDescriptor();

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on cut field annotated with Fixture
        cutDescriptor
                .ifPresent(p -> p.destroy(testInstance));
    }

}
