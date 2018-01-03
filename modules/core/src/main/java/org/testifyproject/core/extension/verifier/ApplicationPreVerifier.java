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

import java.lang.reflect.Method;
import java.util.Optional;

import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.annotation.Lenient;
import org.testifyproject.extension.annotation.Loose;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * Insure system tests annotate the test class with {@link Application} annotation.
 *
 * @author saden
 */
@Strict
@Lenient
@Loose
@SystemCategory
@Discoverable
public class ApplicationPreVerifier implements PreVerifier {

    @Override
    public void verify(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String testClassName = testDescriptor.getTestClassName();

        Optional<Application> foundApplication = testDescriptor.getApplication();

        testContext.addError(!foundApplication.isPresent(),
                "Test class '{}' must be annotated with @Application",
                testClassName);

        foundApplication.ifPresent(application -> {
            Class<?> value = application.value();
            String applicationName = value.getSimpleName();
            String start = application.start();
            String stop = application.stop();

            testContext.addError(!start.isEmpty() && stop.isEmpty(),
                    "@Application annotation on test class '{}' defines 'start' attribute"
                    + "but not stop attribute. Please define the 'stop' attribute.",
                    testClassName);

            Optional<Method> foundMainMethod =
                    ReflectionUtil.INSTANCE.findMainMethod(value);

            if (foundMainMethod.isPresent()) {
                try {
                    value.getConstructor();
                } catch (NoSuchMethodException e) {
                    testContext.addError(
                            "Application class '{}' defined in test class '{}' does not have "
                            + "a zero argument default constructor. Please insure that the "
                            + "the application class defines a public zero argument "
                            + "default constructor.",
                            applicationName, testClassName
                    );
                }
            }

            if (!start.isEmpty()) {
                if ("main".equals(start)) {
                    testContext.addError(!foundMainMethod.isPresent(),
                            "Application class '{}' must declare a main method with the"
                            + " signature 'public static void {}(String[] args)'",
                            applicationName, start);
                } else {
                    Optional<Method> foundStart =
                            ReflectionUtil.INSTANCE.findSimpleMethod(value, start);

                    testContext.addError(!foundStart.isPresent(),
                            "Application class '{}' must declare a start method with the"
                            + " signature 'public void {}()'",
                            applicationName, start);
                }
            }

            if (!stop.isEmpty()) {
                Optional<Method> foundStop =
                        ReflectionUtil.INSTANCE.findSimpleMethod(value, stop);

                testContext.addError(!foundStop.isPresent(),
                        "Application class '{}' must declare a stop method with the"
                        + " signature 'public void {}()'",
                        applicationName, stop);
            }
        });

    }

}
