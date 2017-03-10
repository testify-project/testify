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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.stream.Stream;

/**
 * A utility class that provides useful reflection methods.
 *
 * @author saden
 */
public class ReflectionUtil {

    public static final ReflectionUtil INSTANCE = new ReflectionUtil();

    /**
     * Create a new instance of the given type with the given constructor
     * arguments. Not that this method is also capable of creating annotation
     * types by creating a proxy type.
     *
     * @param <T> the type that will be created
     * @param type the instance type
     * @param constArgs the constructor arguments
     * @return a new instance of the given type
     */
    public <T> T newInstance(Class<T> type, Object... constArgs) {
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> {
            try {
                T instance;

                if (Annotation.class.isAssignableFrom(type)) {
                    ClassLoader classLoader = type.getClassLoader();
                    Class[] interfaces = new Class[]{type};

                    InvocationHandler handler = (proxy, method, args) -> {
                        return method.getDefaultValue();
                    };

                    instance = (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
                } else {
                    if (constArgs.length == 0) {
                        instance = type.newInstance();
                    } else {
                        Class[] constArgTypes = Stream.of(constArgs)
                                .map(p -> p.getClass())
                                .toArray(Class[]::new);

                        Constructor<T> constructor = type.getConstructor(constArgTypes);
                        instance = constructor.newInstance(constArgs);
                    }
                }
                return instance;
            }
            catch (IllegalAccessException
                    | IllegalArgumentException
                    | InstantiationException
                    | NoSuchMethodException
                    | SecurityException
                    | InvocationTargetException e) {
                throw new IllegalStateException("Could not create instance", e);
            }
        });
    }

}
