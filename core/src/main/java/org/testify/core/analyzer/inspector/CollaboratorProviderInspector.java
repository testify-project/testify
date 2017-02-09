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

import java.lang.reflect.Method;
import org.testify.MethodDescriptor;
import org.testify.TestDescriptor;
import org.testify.annotation.CollaboratorProvider;
import org.testify.core.analyzer.DefaultMethodDescriptor;
import org.testify.core.analyzer.TestAnnotationInspector;
import org.testify.core.analyzer.TestDescriptorProperties;
import static org.testify.guava.common.base.Preconditions.checkState;
import org.testify.tools.Discoverable;

/**
 * An annotation inspector that processes {@link CollaboratorProvider}
 * annotation.
 *
 * @author saden
 */
@Discoverable
public class CollaboratorProviderInspector implements TestAnnotationInspector<CollaboratorProvider> {

    @Override
    public boolean handles(Class<?> type) {
        return CollaboratorProvider.class.isAssignableFrom(type);
    }

    @Override
    public void inspect(TestDescriptor testDescriptor, Class<?> annotatedType, CollaboratorProvider collaboratorProvider) throws Exception {
        Class<?> providerClass = collaboratorProvider.value();
        checkState(providerClass != void.class,
                "The value of @CollaboratorProvider annotation on '%s' must be specified.",
                annotatedType.getName());

        Method[] methods = providerClass.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getReturnType().equals(Object[].class)
                    && method.getParameterCount() == 0) {
                method.setAccessible(true);

                Object instance = providerClass.newInstance();
                MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method, instance);
                testDescriptor.addProperty(TestDescriptorProperties.COLLABORATOR_PROVIDER, methodDescriptor);

                return;
            }
        }
    }

}
