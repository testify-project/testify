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
package org.testifyproject.junit.system;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.SpringServletContainerInitializer;
import org.testifyproject.ApplicationInstance;
import org.testifyproject.ApplicationProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.bytebuddy.ByteBuddy;
import org.testifyproject.bytebuddy.description.type.TypeDescription;
import org.testifyproject.bytebuddy.dynamic.ClassFileLocator;
import org.testifyproject.bytebuddy.dynamic.DynamicType;
import org.testifyproject.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import static org.testifyproject.bytebuddy.implementation.MethodDelegation.to;
import org.testifyproject.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.not;
import org.testifyproject.bytebuddy.pool.TypePool;
import org.testifyproject.core.DefaultApplicationInstance;
import org.testifyproject.tools.Discoverable;

/**
 * A Spring Servlet implementation of the ApplicationProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringSystemApplicationProvider implements ApplicationProvider {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();
    private static final Map<String, DynamicType.Loaded<?>> REBASED_CLASSES = new ConcurrentHashMap<>();
    private static final InheritableThreadLocal<ApplicationInstance> LOCAL_APPLICATION_INSTANCE = new InheritableThreadLocal<>();

    @Override
    public ApplicationInstance start(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Application application = testDescriptor.getApplication().get();

        SpringSystemInterceptor interceptor = new SpringSystemInterceptor(LOCAL_APPLICATION_INSTANCE);

        ClassFileLocator locator = ClassFileLocator.ForClassLoader.ofClassPath();
        TypePool typePool = TypePool.Default.ofClassPath();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String servletClassName = "org.springframework.web.servlet.FrameworkServlet";

        REBASED_CLASSES.computeIfAbsent(servletClassName, p -> {
            TypeDescription typeDescription = typePool.describe(p).resolve();

            return BYTE_BUDDY
                    .rebase(typeDescription, locator)
                    .method(not(isDeclaredBy(Object.class)))
                    .intercept(
                            to(interceptor)
                                    .filter(not(isDeclaredBy(Object.class)))
                                    .defineAmbiguityResolver(
                                            MethodNameEqualityResolver.INSTANCE,
                                            BindingPriority.Resolver.INSTANCE)
                    )
                    .make()
                    .load(classLoader, ClassLoadingStrategy.Default.INJECTION);
        });

        Class<?> proxyAppType = BYTE_BUDDY.subclass(application.value())
                .method(not(isDeclaredBy(Object.class)))
                .intercept(
                        to(interceptor)
                                .filter(not(isDeclaredBy(Object.class)))
                                .defineAmbiguityResolver(
                                        MethodNameEqualityResolver.INSTANCE,
                                        BindingPriority.Resolver.INSTANCE)
                )
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Set<Class<?>> handlers = Collections.singleton(proxyAppType);

        SpringServletContainerInitializer initializer = new SpringServletContainerInitializer();

        ApplicationInstance<SpringServletContainerInitializer> applicationInstance
                = DefaultApplicationInstance.of(testContext, application, initializer, handlers);

        LOCAL_APPLICATION_INSTANCE.set(applicationInstance);

        return applicationInstance;
    }

    @Override
    public void stop() {
        LOCAL_APPLICATION_INSTANCE.remove();
    }
}
