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
package org.testifyproject.trait;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.stream.Stream;
import org.testifyproject.TestifyException;
import org.testifyproject.guava.common.reflect.TypeToken;

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
        boolean result = TypeToken.of(getGenericType()).isSubtypeOf(type);

        return result ? result : TypeToken.of(getType()).isSubtypeOf(type);
    }

    /**
     * Determine if the type is a supertype of the given type.
     *
     * @param type the super type
     * @return true if type is a supertype of the given type, false otherwise
     */
    default Boolean isSupertypeOf(Type type) {
        boolean result = TypeToken.of(getGenericType()).isSupertypeOf(type);

        return result ? result : TypeToken.of(getType()).isSupertypeOf(type);
    }

    /**
     * Invoke the given method with the given argument on the given instance.
     *
     * @param <T> the return type
     * @param instance the instance the underlying method is invoked from
     * @param methodName the method name
     * @param methodArgs method arguments
     *
     * @return optional containing return value, empty optional otherwise
     */
    default <T> Optional<T> invoke(Object instance, String methodName, Object... methodArgs) {
        return AccessController.doPrivileged((PrivilegedAction<Optional<T>>) () -> {
            try {
                Class[] methodArgTypes = Stream.of(methodArgs)
                        .map(Object::getClass)
                        .toArray(Class[]::new);

                Method method = findMethod(instance.getClass(), methodName, methodArgTypes);
                method.setAccessible(true);

                T result = (T) method.invoke(instance, methodArgs);

                return Optional.ofNullable(result);
            } catch (SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException e) {
                throw TestifyException.of(e);
            }
        });
    }

    /**
     * Find a method with the given name and argument types.
     *
     * @param type type that will be inspected for the method
     * @param methodName the method name
     * @param methodArgTypes the method argument types
     * @return found method or throws {@link IllegalStateException}
     */
    default Method findMethod(Class<?> type, String methodName, Class<?>... methodArgTypes) {
        return AccessController.doPrivileged((PrivilegedAction<Method>) () -> {
            try {
                return type.getDeclaredMethod(methodName, methodArgTypes);
            } catch (NoSuchMethodException | SecurityException e) {
                Class<?> superType = type.getSuperclass();

                if (superType == null) {
                    throw TestifyException.of(e);
                } else {
                    return findMethod(superType, methodName, methodArgTypes);
                }
            }
        });
    }

    /**
     * Find a method with the given name. Note that this method will find the
     * first method that matches the given name without consideration for method
     * parameters.
     *
     * @param methodName the method name
     * @return found method or throws {@link IllegalStateException}
     */
    default Optional<Method> findMethod(String methodName) {
        return AccessController.doPrivileged((PrivilegedAction<Optional<Method>>) () -> {
            for (Method declaredMethod : getType().getDeclaredMethods()) {
                if (declaredMethod.getName().equals(methodName)) {
                    return Optional.of(declaredMethod);
                }
            }

            return Optional.empty();
        });
    }
}
