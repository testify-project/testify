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
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.extension.SutReifier;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.CollaboratorReifier;

/**
 * A class used to run a integration test.
 *
 * @author saden
 */
@UnitCategory
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

        serviceLocatorUtil.findAllWithFilter(PreVerifier.class, testDescriptor.getGuidelines(), UnitCategory.class)
                .forEach(p -> p.verify(testContext));

        serviceLocatorUtil.findAllWithFilter(SutReifier.class, UnitCategory.class)
                .forEach(p -> p.reify(testContext));

        if (testDescriptor.getCollaboratorProvider().isPresent()) {
            serviceLocatorUtil.findAllWithFilter(InitialReifier.class, UnitCategory.class)
                    .forEach(p -> p.reify(testContext));
        }

        serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class, UnitCategory.class)
                .forEach(p -> p.reify(testContext));

        serviceLocatorUtil.findAllWithFilter(FinalReifier.class, UnitCategory.class)
                .forEach(p -> p.reify(testContext));

        //invoke init method on test fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.init(testInstance));

        //invoke init method on sut field annotated with Fixture
        testContext.getSutDescriptor()
                .ifPresent(p -> p.init(testInstance));

        serviceLocatorUtil.findAllWithFilter(PreiVerifier.class, testDescriptor.getGuidelines(), UnitCategory.class)
                .forEach(p -> p.verify(testContext));
    }

    @Override
    public void stop(TestContext testContext) {
        Object testInstance = testContext.getTestInstance();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Optional<SutDescriptor> sutDescriptor = testContext.getSutDescriptor();

        serviceLocatorUtil.findAllWithFilter(PostVerifier.class, testDescriptor.getGuidelines(), UnitCategory.class)
                .forEach(p -> p.verify(testContext));

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        sutDescriptor.ifPresent(p -> p.destroy(testInstance));
    }

}
