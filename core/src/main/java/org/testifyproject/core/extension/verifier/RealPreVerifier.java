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

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.testifyproject.FieldDescriptor;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.Lenient;
import org.testifyproject.extension.annotation.Loose;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.extension.annotation.UnitCategory;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that the test class contains a field annotated with
 * {@link org.testifyproject.annotation.Sut} annotation or a field annotated with
 * {@link org.testifyproject.annotation.Real}.
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
public class RealPreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        Optional<SutDescriptor> foundSutDescriptor = testContext.getSutDescriptor();
        List<FieldDescriptor> fieldDescriptors = testDescriptor.getFieldDescriptors()
                .parallelStream()
                .filter(p -> p.getReal().isPresent())
                .collect(toList());

        ExceptionUtil.INSTANCE.raise(!foundSutDescriptor.isPresent() && fieldDescriptors
                .isEmpty(),
                "Test class '{}' does not define a field annotated with @Sut "
                + "nor does it define field(s) annotated with @Real. Please "
                + "insure the test class defines a single field annotated "
                + "with @Sut or at least one field annotated with @Real.",
                testClassName);
    }

}
