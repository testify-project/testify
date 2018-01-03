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

import java.util.function.Supplier;

import org.testifyproject.extension.ProxyInstance;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A default implementation of {@link ProxyInstance}.
 *
 * @author saden
 * @param <T> the proxy type
 */
@ToString
@EqualsAndHashCode
public class DefaultProxyInstance<T> implements ProxyInstance<T> {

    private final Class<T> type;
    private final String name;
    private final Supplier<T> delegate;

    DefaultProxyInstance(Class<T> type, String name, Supplier<T> delegate) {
        this.type = type;
        this.name = name;
        this.delegate = delegate;
    }

    /**
     * Create a new proxy instance with the give type and delegate supplier.
     *
     * @param <T> the type of the proxy
     * @param type the proxy type
     * @param delegate the supplier of the object the proxy will delegate calls to
     * @return a new proxy instance
     */
    public static <T> ProxyInstance of(Class<T> type, Supplier<T> delegate) {
        return new DefaultProxyInstance(type, null, delegate);
    }

    /**
     * Create a new proxy instance with the give type, name and delegate supplier.
     *
     * @param <T> the type of the proxy
     * @param type the proxy type
     * @param name the proxy name
     * @param delegate the supplier of the object the proxy will delegate calls to
     * @return a new proxy instance
     */
    public static <T> ProxyInstance of(Class<T> type, String name, Supplier<T> delegate) {
        return new DefaultProxyInstance(type, name, delegate);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Supplier<T> getDelegate() {
        return delegate;
    }

}
