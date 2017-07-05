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
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.Lenient;
import org.testifyproject.extension.annotation.Loose;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.tools.Discoverable;

/**
 * Insure that integration test classes are annotated with {@link Module} or
 * {@link Scan}.
 *
 * @author saden
 */
@Strict
@Lenient
@Loose
@IntegrationCategory
@Discoverable
public class ModulePreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        List<Module> foundModules = testDescriptor.getModules();
        List<Scan> foundScans = testDescriptor.getScans();

        ExceptionUtil.INSTANCE.raise(foundModules.isEmpty() && foundScans.isEmpty(),
                "Test class '{}' must be annotated with @Module or @Scan annotation.",
                testClassName);
    }

}
