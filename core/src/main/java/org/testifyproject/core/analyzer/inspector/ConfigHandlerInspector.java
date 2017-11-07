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
package org.testifyproject.core.analyzer.inspector;

import java.lang.reflect.Method;

import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.ConfigHandler;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.analyzer.DefaultMethodDescriptor;
import org.testifyproject.core.analyzer.TestDescriptorProperties;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.extension.AnnotationInspector;
import org.testifyproject.extension.annotation.Handles;

/**
 * An annotation inspector that processes {@link ConfigHandler} annotation.
 *
 * @author saden
 */
@Discoverable
@Handles(ConfigHandler.class)
public class ConfigHandlerInspector implements AnnotationInspector<ConfigHandler> {

    @Override
    public void inspect(TestDescriptor testDescriptor, Class<?> annotatedType,
            ConfigHandler configHandler) {
        Class<?>[] handlers = configHandler.value();

        ExceptionUtil.INSTANCE.raise(configHandler.value().length == 0,
                "@ConfigHandler value attribute on '{}' is not specified.",
                annotatedType.getSimpleName());

        for (Class<?> handlerClass : handlers) {
            Object handlerInstance = ReflectionUtil.INSTANCE.newInstance(handlerClass);
            Method[] methods = handlerClass.getDeclaredMethods();

            for (Method method : methods) {
                if (!method.isSynthetic()) {
                    method.setAccessible(true);

                    MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method,
                            handlerInstance);
                    testDescriptor
                            .addCollectionElement(TestDescriptorProperties.CONFIG_HANDLERS,
                                    methodDescriptor);
                }
            }
        }

        testDescriptor.addProperty(TestDescriptorProperties.CONFIG_HANDLER, configHandler);
    }

}
