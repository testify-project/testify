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
package org.testify.core.analyzer.inspector;

import static org.testify.guava.common.base.Preconditions.checkState;
import org.testify.MethodDescriptor;
import org.testify.annotation.ConfigHandler;
import org.testify.core.analyzer.DefaultInvokableDescriptor;
import org.testify.core.analyzer.DefaultMethodDescriptor;
import org.testify.core.analyzer.TestAnnotationInspector;
import org.testify.core.analyzer.TestDescriptorBuilder;
import org.testify.tools.Discoverable;
import java.lang.reflect.Method;

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
    public void inspect(TestDescriptorBuilder builder, Class<?> annotatedType, ConfigHandler configHandler) throws Exception {
        Class<?>[] handlerClasses = configHandler.value();

        checkState(handlerClasses.length != 0,
                "The value of @ConfigHandler annotation on '%s' must be specified.",
                annotatedType.getName());

        for (Class<?> handlerClass : handlerClasses) {
            Object instance = handlerClass.newInstance();
            Method[] methods = handlerClass.getDeclaredMethods();
            for (Method method : methods) {
                method.setAccessible(true);

                MethodDescriptor methodDescriptor = new DefaultMethodDescriptor(method);
                DefaultInvokableDescriptor invokableDescriptor = new DefaultInvokableDescriptor(methodDescriptor, instance);
                builder.addConfigHandler(invokableDescriptor);
            }
        }
    }

}
