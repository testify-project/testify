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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.stream.Stream;

import org.testifyproject.ObjenesisHelper;

/**
 * A utility class that provides useful reflection methods.
 *
 * @author saden
 */
public class ReflectionUtil {

    public static final ReflectionUtil INSTANCE = new ReflectionUtil();

    /**
     * Create a new instance of the given type with the given constructor arguments. Not that
     * this method is also capable of creating annotation types by creating a proxy type.
     *
     * @param <T> the type that will be created
     * @param type the instance type
     * @param constArgs the constructor arguments
     * @return a new instance of the given type
     */
    public <T> T newInstance(Class<?> type, Object... constArgs) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                createInstance(type, constArgs));
    }

    <T> T createInstance(Class<?> type, Object... constArgs) {
        try {
            T instance;

            if (Annotation.class.isAssignableFrom(type)) {
                ClassLoader classLoader = type.getClassLoader();
                Class[] interfaces = new Class[]{type};

                InvocationHandler handler = (proxy, method, args) -> method.getDefaultValue();

                instance = (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
            } else {
                if (constArgs.length == 0) {
                    instance = (T) type.newInstance();
                } else {
                    Class[] constArgTypes = Stream.of(constArgs)
                            .map(Object::getClass)
                            .toArray(Class[]::new);

                    Constructor<?> constructor = type.getConstructor(constArgTypes);
                    instance = (T) constructor.newInstance(constArgs);
                }
            }
            return instance;
        } catch (IllegalAccessException |
                IllegalArgumentException |
                InstantiationException |
                NoSuchMethodException |
                SecurityException |
                InvocationTargetException e) {
            LoggingUtil.INSTANCE.debug("Could not create instance of type '{}'", type
                    .getSimpleName(), e);

            return (T) ObjenesisHelper.getInstantiatorOf(type).newInstance();
        }
    }

    /**
     * Remove final modifier from the given member.
     *
     * @param member the member final modifier will be removed from.
     */
    public void removeFinalModifier(Member member) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(member, member.getModifiers() & ~Modifier.FINAL);

                return null;
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException |
                    SecurityException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could not remove final modifier from field  '{}' in class '{}'.",
                        e, member.getName(), member.getDeclaringClass().getSimpleName());
            }
        });

    }

    /**
     * Remove final modifier from the given type.
     *
     * @param type the executable final modifier will be removed from.
     */
    public void removeFinalModifier(Class type) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Field modifiersField = Class.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(type, type.getModifiers() & ~Modifier.FINAL);

                return null;
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException |
                    SecurityException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could not remove final modifier from field  '{}' in class '{}'.",
                        e, type.getName(), type.getDeclaringClass().getSimpleName());
            }
        });

    }

    /**
     * Invoke the given method using the given object and arguments.
     *
     * @param <T> the return type
     * @param method the method that will be invoked
     * @param obj the object the underlying method is invoked from
     * @param args the argument passed to the method
     * @return the result of invoking the method
     */
    public <T> T invoke(Method method, Object obj, Object... args) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> {
            try {
                return (T) method.invoke(obj, args);
            } catch (IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could invoke method '{}' in class '{}'.",
                        e, method.getName(), method.getDeclaringClass().getSimpleName());
            }
        });

    }

}
