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
package org.testifyproject.core.util;

import static org.testifyproject.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.not;

import java.util.function.Supplier;

import org.testifyproject.bytebuddy.ByteBuddy;
import org.testifyproject.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.testifyproject.bytebuddy.implementation.MethodDelegation;
import org.testifyproject.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Morph;
import org.testifyproject.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import org.testifyproject.core.extension.instrument.DefaultDelegateInterceptor;
import org.testifyproject.extension.InstrumentMorpher;

/**
 * A utility class that provides useful instrumentation utility methods.
 *
 * @author saden
 */
public class InstrumentUtil {

    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();
    public static final InstrumentUtil INSTANCE = new InstrumentUtil();

    /**
     * Create a proxy instance for the given type using the given classloader and interceptor.
     *
     * @param <T> the type of the class being proxied
     * @param type the class being proxied
     * @param classLoader the classloader
     * @param interceptor the interceptor instance used to intercept calls
     * @return a new proxy instance
     */
    public <T> T createProxy(Class<T> type, ClassLoader classLoader, Object interceptor) {
        try {
            return createSubclass(type, classLoader, interceptor)
                    .newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw ExceptionUtil.INSTANCE.propagate("Could not create proxy for type {}", type
                    .getSimpleName());
        }
    }

    /**
     * Create a proxy instance for the given type using the given classloader and delegate
     * supplier.
     *
     * @param <T> the type of the class being proxied
     * @param type the class being proxied
     * @param classLoader the classloader
     * @param delegateSupplier the supplier of object calls will be delegated to
     * @return a new proxy instance
     */
    public <T> T createProxy(Class<T> type, ClassLoader classLoader,
            Supplier<?> delegateSupplier) {
        return createProxy(type, classLoader, DefaultDelegateInterceptor.of(delegateSupplier));
    }

    /**
     * Create subclass of the given type using the given classLoader and interceptor.
     *
     * @param <T> the type of the class being subclassed
     * @param type the class being subclassed
     * @param classLoader the classloader used by the new class.
     * @param interceptor the interceptor instance used to intercept calls
     * @return the dynamic type that is loaded after the class is subclassed
     */
    public <T> Class<? extends T> createSubclass(Class<T> type, ClassLoader classLoader,
            Object interceptor) {
        MethodDelegation delegate = MethodDelegation.withEmptyConfiguration()
                .withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS)
                .withBinders(Morph.Binder.install(InstrumentMorpher.class))
                .withResolvers(MethodNameEqualityResolver.INSTANCE)
                .withResolvers(BindingPriority.Resolver.INSTANCE)
                .filter(not(isDeclaredBy(Object.class)))
                .to(interceptor);

        return BYTE_BUDDY.subclass(type)
                .method(isDeclaredBy(type))
                .intercept(delegate)
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }
}
