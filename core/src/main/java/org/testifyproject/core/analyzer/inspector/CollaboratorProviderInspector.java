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
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.core.analyzer.DefaultMethodDescriptor;
import org.testifyproject.core.analyzer.TestDescriptorProperties;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.extension.AnnotationInspector;
import org.testifyproject.extension.annotation.Handles;
import org.testifyproject.tools.Discoverable;

/**
 * An annotation inspector that processes {@link CollaboratorProvider}
 * annotation.
 *
 * @author saden
 */
@Discoverable
@Handles(CollaboratorProvider.class)
public class CollaboratorProviderInspector implements AnnotationInspector<CollaboratorProvider> {

    @Override
    public void inspect(TestDescriptor testDescriptor, Class<?> annotatedType, CollaboratorProvider collaboratorProvider) {
        Class<?>[] providers = collaboratorProvider.value();

        ExceptionUtil.INSTANCE.raise(providers.length == 0,
                "@CollaboratorProvider value attribute on '{}' is not specified.",
                annotatedType.getSimpleName());

        for (Class<?> providerClass : providers) {
            Method[] methods = providerClass.getDeclaredMethods();
            Object providerInstance = ReflectionUtil.INSTANCE.newInstance(providerClass);

            for (Method method : methods) {
                if (!method.isSynthetic()) {
                    method.setAccessible(true);

                    MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method, providerInstance);
                    testDescriptor.addCollectionElement(TestDescriptorProperties.COLLABORATOR_PROVIDERS, methodDescriptor);
                }
            }

            //capture collaborator provider on the collaborator provider
            CollaboratorProvider providerCollaboratorProvider
                    = providerClass.getDeclaredAnnotation(CollaboratorProvider.class);

            if (providerCollaboratorProvider != null) {
                inspect(testDescriptor, providerClass, providerCollaboratorProvider);
            }
        }

        testDescriptor.addProperty(TestDescriptorProperties.COLLABORATOR_PROVIDER, collaboratorProvider);
    }

}
