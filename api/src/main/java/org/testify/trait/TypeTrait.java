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
package org.testify.trait;

import org.testify.guava.common.reflect.TypeToken;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;

/**
 * A contract that specifies type traits.
 *
 * @author saden
 */
public interface TypeTrait {

    /**
     * Get the instance type.
     *
     * @return the type
     */
    Class<?> getType();

    /**
     * Get the instance generic type.
     *
     * @return the generic type
     */
    Type getGenericType();

    /**
     * Get human readable type name.
     *
     * @return the type name.
     */
    default String getTypeName() {
        return getType().getSimpleName();
    }

    /**
     * Get the type's class loader.
     *
     * @return the type's class loader.
     */
    default ClassLoader getClassLoader() {
        return getType().getClassLoader();
    }

    /**
     * Determine if the type is a subtype of the given type.
     *
     * @param type the subtype
     * @return true if type is a subtype of the given type, false otherwise
     */
    default Boolean isSubtypeOf(Type type) {
        return TypeToken.of(getGenericType()).isSubtypeOf(type);
    }

    /**
     * Determine if the type is a supertype of the given type.
     *
     * @param type the super type
     * @return true if type is a supertype of the given type, false otherwise
     */
    default Boolean isSupertypeOf(Type type) {
        return TypeToken.of(getGenericType()).isSupertypeOf(type);
    }

    /**
     * Invoke the given method with the given argument on the given instance.
     *
     * @param <T> the return type
     * @param instance the instance the underlying method is invoked from
     * @param methodName the method name
     * @param args method arguments
     *
     * @return optional containing return value, empty optional otherwise
     */
    default <T> Optional<T> invoke(Object instance, String methodName, Object... args) {

        return AccessController.doPrivileged((PrivilegedAction<Optional<T>>) () -> {
            try {
                Method method = getType().getMethod(methodName);
                method.setAccessible(true);

                return Optional.ofNullable((T) method.invoke(instance));
            } catch (NoSuchMethodException
                    | SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        });
    }

}
