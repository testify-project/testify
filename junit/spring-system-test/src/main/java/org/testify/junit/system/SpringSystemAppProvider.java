/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.junit.system;

import org.testify.bytebuddy.ByteBuddy;
import org.testify.bytebuddy.description.type.TypeDescription;
import org.testify.bytebuddy.dynamic.ClassFileLocator;
import org.testify.bytebuddy.dynamic.DynamicType;
import org.testify.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import static org.testify.bytebuddy.implementation.MethodDelegation.to;
import org.testify.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testify.bytebuddy.implementation.bind.annotation.BindingPriority;
import static org.testify.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testify.bytebuddy.matcher.ElementMatchers.not;
import org.testify.bytebuddy.pool.TypePool;
import org.testify.ApplicationInstance;
import org.testify.ApplicationProvider;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.annotation.Application;
import org.testify.core.impl.DefaultApplicationInstance;
import org.testify.tools.Discoverable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.SpringServletContainerInitializer;

/**
 * A Spring Servlet implementation of the ApplicationProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class SpringSystemAppProvider implements ApplicationProvider {

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
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Set<Class<?>> servletContainerInitializerHandlers = new HashSet<>();
        servletContainerInitializerHandlers.add(proxyAppType);

        SpringServletContainerInitializer servletContainerInitializer = new SpringServletContainerInitializer();

        ApplicationInstance applicationInstance = DefaultApplicationInstance.of(application,
                testContext,
                servletContainerInitializer,
                servletContainerInitializerHandlers);

        LOCAL_APPLICATION_INSTANCE.set(applicationInstance);

        return applicationInstance;
    }

    public void stop() {
        LOCAL_APPLICATION_INSTANCE.remove();
    }
}
