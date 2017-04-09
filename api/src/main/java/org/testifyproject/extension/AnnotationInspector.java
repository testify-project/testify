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
package org.testifyproject.extension;

import java.lang.annotation.Annotation;
import org.testifyproject.TestDescriptor;

/**
 * Interface for inspecting annotations on a class.
 *
 * @author saden
 * @param <T> the annotation type
 */
@FunctionalInterface
public interface AnnotationInspector<T extends Annotation> {

    /**
     * Inspect the given annotation on the given annotated type.
     *
     * @param testDescriptor the test descriptor
     * @param annotatedType the type the annotation is on
     * @param annotation the annotation
     */
    void inspect(TestDescriptor testDescriptor, Class<?> annotatedType, T annotation);

}
