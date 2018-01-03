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
package org.testifyproject.core.extension.verifier;

import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.RemoteResource;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.Lenient;
import org.testifyproject.extension.annotation.Loose;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * Insure remote resource providers have default constructors.
 *
 * @author saden
 */
@Strict
@Lenient
@Loose
@SystemCategory
@IntegrationCategory
@Discoverable
public class RemoteResourcePreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        testDescriptor.getRemoteResources()
                .parallelStream()
                .map(RemoteResource::value)
                .forEach(p -> {
                    try {
                        p.getConstructor();
                    } catch (NoSuchMethodException e) {
                        testContext.addError(
                                "Remote Resource '{}' defined in test class '{}' does not have "
                                + "a zero argument default constructor. Please insure that the "
                                + "remote resource provider defines a public zero argument "
                                + "default constructor.",
                                testClassName, p.getSimpleName()
                        );
                    }
                });
    }

}
