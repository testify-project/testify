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

import static org.testifyproject.bytebuddy.implementation.MethodDelegation.to;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static org.testifyproject.bytebuddy.matcher.ElementMatchers.not;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.testifyproject.ObjenesisHelper;
import org.testifyproject.bytebuddy.ByteBuddy;
import org.testifyproject.bytebuddy.description.type.TypeDescription;
import org.testifyproject.bytebuddy.dynamic.ClassFileLocator;
import org.testifyproject.bytebuddy.dynamic.DynamicType;
import org.testifyproject.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.testifyproject.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import org.testifyproject.bytebuddy.implementation.bind.annotation.BindingPriority;
import org.testifyproject.bytebuddy.pool.TypePool;

/**
 * A utility class that provides useful reflection methods.
 *
 * @author saden
 */
public class ReflectionUtil {

    public static final ReflectionUtil INSTANCE = new ReflectionUtil();
    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();
    private static final Map<String, DynamicType.Loaded> DYNAMIC_CLASSES =
            new ConcurrentHashMap<>();

    /**
     * Create a new instance of the given type with the given constructor arguments. Not that
     * this method is also capable of creating annotation types by creating a proxy type.
     *
     * @param <T> the type that will be created
     * @param type the instance type
     * @param constArgs the constructor arguments
     * @return a new instance of the given type
     */
    public <T> T newInstance(Class<T> type, Object... constArgs) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () ->
                createInstance(type, constArgs));
    }

    <T> T createInstance(Class<T> type, Object... constArgs) {
        try {
            T instance;
            if (Annotation.class.isAssignableFrom(type)) {
                ClassLoader classLoader = type.getClassLoader();
                Class[] interfaces = new Class[]{type};

                InvocationHandler handler = (proxy, method, args) -> method.getDefaultValue();

                instance = (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
            } else {
                if (constArgs.length == 0) {
                    instance = type.newInstance();
                } else {
                    Class[] constArgTypes = Stream.of(constArgs)
                            .map(Object::getClass)
                            .toArray(Class[]::new);

                    Constructor<T> constructor = type.getConstructor(constArgTypes);
                    instance = constructor.newInstance(constArgs);
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

            return ObjenesisHelper.getInstantiatorOf(type).newInstance();
        }
    }

    /**
     * Rebase the class with the given className using the given classLoader and interceptor.
     * Note that to rebase a class the class must not be loaded.
     *
     * @param className the fully qualified name of the class being rebased
     * @param classLoader the classloader used by the new class.
     * @param interceptor the interceptor instance used to intercept calls
     * @return the dynamic type that is loaded after the class is rebased
     */
    public DynamicType.Loaded rebase(String className, ClassLoader classLoader,
            Object interceptor) {
        ClassFileLocator locator = ClassFileLocator.ForClassLoader.ofClassPath();
        TypePool typePool = TypePool.Default.ofClassPath();

        return DYNAMIC_CLASSES.computeIfAbsent(className, p -> {
            TypeDescription typeDescription = typePool.describe(p).resolve();

            return BYTE_BUDDY
                    .rebase(typeDescription, locator)
                    .method(not(isDeclaredBy(Object.class)))
                    .intercept(to(interceptor)
                            .filter(not(isDeclaredBy(Object.class)))
                            .defineAmbiguityResolver(
                                    MethodNameEqualityResolver.INSTANCE,
                                    BindingPriority.Resolver.INSTANCE)
                    )
                    .make()
                    .load(classLoader, ClassLoadingStrategy.Default.INJECTION);
        });
    }

    /**
     * Subclass the class with the given type using the given classLoader and interceptor.
     *
     * @param <T> the type of the class being subclassed
     * @param type the class being subclassed
     * @param classLoader the classloader used by the new class.
     * @param interceptor the interceptor instance used to intercept calls
     * @return the dynamic type that is loaded after the class is subclassed
     */
    public <T> Class<? extends T> subclass(Class<T> type, ClassLoader classLoader,
            Object interceptor) {
        String className = type.getName();

        DynamicType.Loaded<T> loaded = DYNAMIC_CLASSES.computeIfAbsent(className, p ->
                BYTE_BUDDY.subclass(type)
                        .method(not(isDeclaredBy(Object.class)))
                        .intercept(
                                to(interceptor)
                                        .filter(not(isDeclaredBy(Object.class)))
                                        .defineAmbiguityResolver(
                                                MethodNameEqualityResolver.INSTANCE,
                                                BindingPriority.Resolver.INSTANCE)
                        )
                        .make()
                        .load(classLoader, ClassLoadingStrategy.Default.WRAPPER)
        );

        return loaded.getLoaded();
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

}
