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
import org.testifyproject.core.analyzer.DefaultMethodDescriptor;
import org.testifyproject.core.analyzer.TestAnnotationInspector;
import org.testifyproject.core.analyzer.TestDescriptorProperties;
import static org.testifyproject.guava.common.base.Preconditions.checkState;
import org.testifyproject.tools.Discoverable;

/**
 * An annotation inspector that processes {@link ConfigHandler} annotation.
 *
 * @author saden
 */
@Discoverable
public class ConfigHandlerInspector implements TestAnnotationInspector<ConfigHandler> {

    @Override
    public boolean handles(Class<?> annotationType) {
        return ConfigHandler.class.isAssignableFrom(annotationType);
    }

    @Override
    public void inspect(TestDescriptor testDescriptor, Class<?> annotatedType, ConfigHandler configHandler) throws Exception {
        Class<?>[] handlerClasses = configHandler.value();

        checkState(handlerClasses.length != 0,
                "@ConfigHandler value attribite on '%s' must be specified.",
                annotatedType.getName());

        for (Class<?> handlerClass : handlerClasses) {
            Object instance = handlerClass.newInstance();
            Method[] methods = handlerClass.getDeclaredMethods();

            for (Method method : methods) {
                method.setAccessible(true);

                MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method, instance);
                testDescriptor.addListElement(TestDescriptorProperties.CONFIG_HANDLERS, methodDescriptor);
            }
        }

    }

}
