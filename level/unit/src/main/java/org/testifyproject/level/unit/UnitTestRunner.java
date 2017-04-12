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

import org.testifyproject.TestContext;
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

    private TestContext testContext;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(ConfigurationVerifier.class, UnitTest.class)
                .forEach(p -> p.verify(testContext));

        Object testInstance = testContext.getTestInstance();

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(CutReifier.class, UnitTest.class)
                .forEach(p -> p.reify(testContext));

        if (testContext.getTestDescriptor().getCollaboratorProvider().isPresent()) {
            ServiceLocatorUtil.INSTANCE.findAllWithFilter(CollaboratorsReifier.class, UnitTest.class)
                    .forEach(p -> p.reify(testContext));
        }

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(FieldReifier.class, UnitTest.class)
                .forEach(p -> p.reify(testContext));

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(TestReifier.class, UnitTest.class)
                .forEach(p -> p.reify(testContext));

        //invoke init method on test fields annotated with Fixture
        testContext.getTestDescriptor().getFieldDescriptors()
                .forEach(p -> p.init(testInstance));

        //invoke init method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.init(testInstance));

        ServiceLocatorUtil.INSTANCE.findAllWithFilter(WiringVerifier.class, UnitTest.class)
                .forEach(p -> p.verify(testContext));
    }

    @Override
    public void stop() {
        Object testInstance = testContext.getTestInstance();

        //invoke destroy method on fields annotated with Fixture
        testContext.getTestDescriptor().getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.destroy(testInstance));
    }

}
