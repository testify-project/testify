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
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 * A contract that specifies method traits.
 *
 * @author saden
 */
public interface MethodTrait extends MemberTrait<Method>, AnnotationTrait<Method> {

    /**
     * Get the method as the annotated element.
     *
     * @return the underlying method instance.
     */
    @Override
    default Method getAnnotatedElement() {
        return getMember();
    }

    /**
     * Get the method parameter types.
     *
     * @return a list of parameter types, empty list otherwise
     */
    default List<Class> getParameterTypes() {
        return of(getAnnotatedElement().getParameterTypes()).collect(toList());
    }

    /**
     * Method return type.
     *
     * @return the method return type
     */
    default Class<?> getReturnType() {
        return getAnnotatedElement().getReturnType();
    }

    /**
     * Determine if the method has parameters of the given type.
     *
     * @param parameterTypes the parameter types
     * @return true if the method has the given parameter types, false otherwise
     */
    default Boolean hasParameterTypes(Type... parameterTypes) {
        Method method = getAnnotatedElement();
        Class<?>[] methodParameterTypes = method.getParameterTypes();

        boolean matches = true;

        if (parameterTypes.length == methodParameterTypes.length) {
            for (int i = 0; i < methodParameterTypes.length; i++) {
                TypeToken token = TypeToken.of(methodParameterTypes[i]);

                if (!token.isSupertypeOf(parameterTypes[i])) {
                    matches = false;
                    break;
                }
            }
        } else {
            matches = false;
        }

        return matches;
    }

    /**
     * Invoke the method on the given instance with the given arguments.
     *
     * @param <T> the return type
     * @param instance the instance the underlying method is invoked from
     * @param args method arguments
     *
     * @return optional with method return value, empty optional otherwise
     */
    default <T> Optional<T> invoke(Object instance, Object... args) {
        return AccessController.doPrivileged((PrivilegedAction<Optional<T>>) () -> {
            try {
                Method method = getAnnotatedElement();
                method.setAccessible(true);

                T result = (T) method.invoke(instance, args);

                return Optional.ofNullable(result);
            }
            catch (SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
