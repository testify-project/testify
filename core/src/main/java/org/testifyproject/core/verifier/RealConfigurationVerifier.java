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
package org.testifyproject.core.verifier;

import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that the test class contains a field annotated with
 * {@link org.testifyproject.annotation.Cut} annotation or a field annotated
 * with {@link org.testifyproject.annotation.Real}.
 *
 * @author saden
 */
@IntegrationTest
@SystemTest
@Discoverable
public class RealConfigurationVerifier implements ConfigurationVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        Optional<CutDescriptor> foundCutDescriptor = testContext.getCutDescriptor();
        List<FieldDescriptor> fieldDescriptors = testDescriptor.getFieldDescriptors()
                .parallelStream()
                .filter(p -> p.getReal().isPresent())
                .collect(toList());

        ExceptionUtil.INSTANCE.raise(!foundCutDescriptor.isPresent() && fieldDescriptors.isEmpty(),
                "Test class '{}' does not define a field annotated with @Cut "
                + "nor does it define field(s) annotated with @Real. Please "
                + "insure the test class defines a single field annotated "
                + "with @Cut or at least one field annotated with @Real.",
                testClassName);
    }

}
