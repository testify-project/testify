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
package org.testifyproject.core.extension.instrument;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.testifyproject.bytebuddy.implementation.bind.annotation.AllArguments;
import org.testifyproject.bytebuddy.implementation.bind.annotation.Origin;
import org.testifyproject.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.testifyproject.core.util.ExceptionUtil;

/**
 * A generic interceptor implementation that delegates calls in a lazy manner to a delegate
 * supplied by specified delegate supplier.
 *
 * @author saden
 */
public class DefaultDelegateInterceptor {

    private final Supplier<?> delegateSupplier;

    DefaultDelegateInterceptor(Supplier<?> delegateSupplier) {
        this.delegateSupplier = delegateSupplier;
    }

    /**
     * Create a new instance of DefaultDelegateInterceptor.
     *
     * @param delegateSupplier the supplier of the delegate instance
     * @return a new instance of DefaultDelegateInterceptor
     */
    public static DefaultDelegateInterceptor of(Supplier<?> delegateSupplier) {
        return new DefaultDelegateInterceptor(delegateSupplier);
    }

    @RuntimeType
    public Object intercept(@Origin Method method, @AllArguments Object[] allArguments)
            throws Exception {
        try {
            Object delegate = delegateSupplier.get();
            return method.invoke(delegate, allArguments);
        } catch (IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e) {
            throw ExceptionUtil.INSTANCE.propagate(e);
        }
    }

}
