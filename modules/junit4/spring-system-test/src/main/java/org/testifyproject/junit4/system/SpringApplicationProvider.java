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
package org.testifyproject.junit4.system;

import java.util.Optional;

import org.springframework.web.SpringServletContainerInitializer;
import org.testifyproject.ApplicationInstance;
import org.testifyproject.ApplicationProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.DefaultApplicationInstance;
import org.testifyproject.core.util.InstrumentUtil;
import org.testifyproject.server.core.ServletInstance;
import org.testifyproject.server.core.ServletProperties;

/**
 * A Spring Servlet implementation of the ApplicationProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringApplicationProvider implements ApplicationProvider {

    @Override
    public ApplicationInstance start(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Optional<Application> foundApplication = testDescriptor.getApplication();
        ApplicationInstance applicationInstance = null;

        if (foundApplication.isPresent()) {
            Application application = foundApplication.get();
            SpringApplicationInterceptor interceptor = new SpringApplicationInterceptor();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            Class<?> dynamicApp = InstrumentUtil.INSTANCE.createSubclass(
                    application.value(), classLoader, interceptor
            );
            SpringServletContainerInitializer initializer =
                    new SpringServletContainerInitializer();

            ServletInstance servletInstance = ServletInstance.builder()
                    .handler(dynamicApp)
                    .initializer(initializer)
                    .build();

            applicationInstance = DefaultApplicationInstance.of(testContext, application);
            applicationInstance.addProperty(ServletProperties.SERVLET_INSTANCE, servletInstance);
        }

        return applicationInstance;
    }
}
