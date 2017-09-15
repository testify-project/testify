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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.SpringServletContainerInitializer;
import org.testifyproject.ApplicationInstance;
import org.testifyproject.ApplicationProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.ApplicationInstanceProperties;
import org.testifyproject.core.DefaultApplicationInstance;
import org.testifyproject.core.TestContextHolder;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.tools.Discoverable;

/**
 * A Spring Servlet implementation of the ApplicationProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringSystemApplicationProvider implements ApplicationProvider {

    private static final TestContextHolder TEST_CONTEXT_HOLDER =
            TestContextHolder.INSTANCE;

    @Override
    public ApplicationInstance start(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Optional<Application> foundApplication = testDescriptor.getApplication();
        ApplicationInstance applicationInstance = null;

        if (foundApplication.isPresent()) {
            Application application = foundApplication.get();

            String servletClassName = "org.springframework.web.servlet.FrameworkServlet";
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            SpringSystemInterceptor interceptor = new SpringSystemInterceptor(
                    TEST_CONTEXT_HOLDER);

            ReflectionUtil.INSTANCE.rebase(servletClassName, classLoader, interceptor);

            Class<?> dynamicApp = ReflectionUtil.INSTANCE.subclass(application.value(),
                    classLoader, interceptor);

            Set<Class<?>> handlers = Collections.singleton(dynamicApp);
            SpringServletContainerInitializer initializer =
                    new SpringServletContainerInitializer();

            applicationInstance = DefaultApplicationInstance.of(testContext, application);

            applicationInstance.addProperty(
                    ApplicationInstanceProperties.SERVLET_CONTAINER_INITIALIZER,
                    initializer);
            applicationInstance
                    .addProperty(ApplicationInstanceProperties.SERVLET_HANDLERS, handlers);

            TEST_CONTEXT_HOLDER.set(testContext);
        }

        return applicationInstance;
    }

    @Override
    public void stop() {
        TEST_CONTEXT_HOLDER.remove();
    }
}
