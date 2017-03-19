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
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testifyproject.MethodDescriptor;

/**
 * A descriptor class used to access or perform operations on test class
 * methods.
 *
 * @author saden
 */
public class DefaultMethodDescriptor implements MethodDescriptor {

    private final Method method;
    private final Object instance;

    public static MethodDescriptor of(Method method) {
        return new DefaultMethodDescriptor(method, null);
    }

    public static MethodDescriptor of(Method method, Object instance) {
        return new DefaultMethodDescriptor(method, instance);
    }

    DefaultMethodDescriptor(Method method, Object instance) {
        this.method = method;
        this.instance = instance;
    }

    @Override
    public Optional<Object> getInstance() {
        return ofNullable(instance);
    }

    @Override
    public Method getMember() {
        return method;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.method);
        hash = 89 * hash + Objects.hashCode(this.instance);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultMethodDescriptor other = (DefaultMethodDescriptor) obj;
        if (!Objects.equals(this.method, other.method)) {
            return false;
        }
        return Objects.equals(this.instance, other.instance);
    }

    @Override
    public String toString() {
        return "DefaultMethodDescriptor{" + "method=" + method + ", instance=" + instance + '}';
    }

}
