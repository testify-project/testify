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
package org.testify.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A utility class that provides methods for working annotations.
 *
 * @author saden
 */
public class AnnotationUtil {

    public static final AnnotationUtil INSTANCE = new AnnotationUtil();

    public <T extends Annotation> T newInstance(Class<T> annotation) {
        ClassLoader classLoader = annotation.getClassLoader();
        Class[] interfaces = new Class[]{annotation};
        InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
            return method.getDefaultValue();
        };

        return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
}
