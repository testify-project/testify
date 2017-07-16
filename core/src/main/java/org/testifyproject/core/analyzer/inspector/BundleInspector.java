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

import java.lang.annotation.Annotation;
import java.util.List;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Bundle;
import org.testifyproject.core.analyzer.TestDescriptorProperties;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.AnnotationInspector;
import org.testifyproject.extension.annotation.Handles;
import org.testifyproject.tools.Discoverable;

/**
 * An annotation inspector that processes {@link Bundle} annotation.
 *
 * @author saden
 */
@Discoverable
@Handles(Bundle.class)
public class BundleInspector implements AnnotationInspector<Bundle> {

    @Override
    public void inspect(TestDescriptor testDescriptor, Class<?> annotatedType, Bundle bundle) {
        List<AnnotationInspector> inspectors = ServiceLocatorUtil.INSTANCE.getAll(AnnotationInspector.class);
        Annotation[] annotations = annotatedType.getDeclaredAnnotations();

        for (Annotation annotation : annotations) {
            if (Bundle.class.isAssignableFrom(annotation.annotationType())) {
                continue;
            }

            Class<? extends Annotation> annotationClass = annotation.annotationType();

            inspectors.forEach((inspector) -> {
                Handles handles = inspector.getClass().getDeclaredAnnotation(Handles.class);
                Class<? extends Annotation>[] typesHandled = handles.value();

                for (Class<? extends Annotation> typeHandled : typesHandled) {
                    if (typeHandled.isAssignableFrom(annotationClass)) {
                        Annotation foundAnnotation = annotatedType.getDeclaredAnnotation(annotationClass);
                        inspector.inspect(testDescriptor, annotatedType, foundAnnotation);
                        testDescriptor.addListElement(TestDescriptorProperties.INSPECTED_ANNOTATIONS, foundAnnotation);
                    } else if (typeHandled.equals(Bundle.class) && annotationClass.isAnnotationPresent(Bundle.class)) {
                        Annotation foundAnnotation = annotationClass.getDeclaredAnnotation(Bundle.class);
                        inspector.inspect(testDescriptor, annotationClass, foundAnnotation);
                    }
                }

            });
        }
    }

}
