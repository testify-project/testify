/*
 * Copyright 2016-2018 Testify Project.
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
import java.util.Optional;
import java.util.stream.Stream;

import org.testifyprojects.objenesis.ObjenesisHelper;

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
            LoggingUtil.INSTANCE.debug("Could not create instance of type '{}', falling back "
                    + "to using Objenesis to create an instance",
                    type.getSimpleName(), e);

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
                        member.getName(), member.getDeclaringClass().getSimpleName(), e);
            }
        });
    }

    /**
     * Find the main method in the given type.
     *
     * @param type the type that will be inspected
     * @return an optional with the main method, empty optional otherwise
     */
    public Optional<Method> findMainMethod(Class type) {
        return AccessController.doPrivileged((PrivilegedAction<Optional<Method>>) () -> {
            try {
                Optional<Method> result = Optional.empty();
                Method method = type.getMethod("main", String[].class);
                int modifiers = method.getModifiers();
                Class<?> returnType = method.getReturnType();

                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)
                        && void.class.isAssignableFrom(returnType)) {
                    result = Optional.of(method);
                }

                return result;
            } catch (NoSuchMethodException | SecurityException e) {
                return Optional.empty();
            }
        });
    }

    /**
     * Find a method that takes no arguments and returns void in the given type with the given
     * name.
     *
     * @param type the type that will be inspected
     * @param methodName the method name
     * @return an optional with the main method, empty optional otherwise
     */
    public Optional<Method> findSimpleMethod(Class type, String methodName) {
        return AccessController.doPrivileged((PrivilegedAction<Optional<Method>>) () -> {
            try {
                Optional<Method> result = Optional.empty();
                Method method = type.getMethod(methodName);
                int modifiers = method.getModifiers();
                Class<?> returnType = method.getReturnType();

                if (void.class.isAssignableFrom(returnType)) {
                    result = Optional.of(method);
                }

                return result;
            } catch (NoSuchMethodException | SecurityException e) {
                return Optional.empty();
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
                method.setAccessible(true);
                return (T) method.invoke(obj, args);
            } catch (IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could invoke method '{}' in class '{}'.",
                        method.getName(), method.getDeclaringClass().getSimpleName(), e);
            }
        });

    }

    /**
     * Set the field with the given name on the given object to the given value.
     *
     * @param fieldName the field name of the object
     * @param obj the object whose field will be set
     * @param value the value the field will be set to
     */
    public void setDeclaredField(String fieldName, Object obj, Object value) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            Class<?> objectType = obj.getClass();
            try {
                Field field = objectType.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(obj, value);

                return null;
            } catch (IllegalAccessException |
                    IllegalArgumentException |
                    NoSuchFieldException |
                    SecurityException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could set field '{}' for object of type '{}''.",
                        fieldName, objectType.getSimpleName(), e);
            }
        });

    }

    /**
     * Find and load the given class.
     *
     * @param className the fully qualified name of the desired class
     * @return an optional with the loaded class, empty optional if class is not found
     */
    public Optional<Class> load(String className) {
        Class result;
        try {
            result = Class.forName(className);
        } catch (ClassNotFoundException e) {
            result = null;
        }

        return Optional.ofNullable(result);
    }

}
