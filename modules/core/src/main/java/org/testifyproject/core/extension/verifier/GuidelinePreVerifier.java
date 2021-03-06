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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.Lenient;
import org.testifyproject.extension.annotation.Loose;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 * Insure that tests classes are annotated with only one guideline annotation.
 *
 * @author saden
 */
@Strict
@Lenient
@Loose
@UnitCategory
@IntegrationCategory
@SystemCategory
@Discoverable
public class GuidelinePreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        Collection<Class<? extends Annotation>> foundGuidelines = testDescriptor.getGuidelines();

        testContext.addError(foundGuidelines.size() > 1,
                "Test class '{}' has multiple multiple guideline annotations ({}). "
                + "Please insure there is only one guideline annotation present.",
                testClassName, foundGuidelines.stream()
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining(", ")));
    }

}
