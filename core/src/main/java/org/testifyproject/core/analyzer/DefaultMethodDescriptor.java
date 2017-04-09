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
package org.testifyproject.core.analyzer;

import java.lang.reflect.Method;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.MethodDescriptor;

/**
 * A descriptor class used to access or perform operations on test class methods.
 *
 * @author saden
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class DefaultMethodDescriptor implements MethodDescriptor {

    private final Method method;
    private final Object instance;

    DefaultMethodDescriptor(Method method, Object instance) {
        this.method = method;
        this.instance = instance;
    }

    /**
     * Create a new method descriptor instance from the given method.
     *
     * @param method the underlying method
     * @return a method descriptor instance
     */
    public static MethodDescriptor of(Method method) {
        return new DefaultMethodDescriptor(method, null);
    }

    /**
     * Create a new method descriptor instance from the given method and instance.
     *
     * @param method the underlying method
     * @param instance the instance associated with the method
     * @return a method descriptor instance
     */
    public static MethodDescriptor of(Method method, Object instance) {
        return new DefaultMethodDescriptor(method, instance);
    }

    @Override
    public Optional<Object> getInstance() {
        return ofNullable(instance);
    }

    @Override
    public Method getMember() {
        return method;
    }

}
