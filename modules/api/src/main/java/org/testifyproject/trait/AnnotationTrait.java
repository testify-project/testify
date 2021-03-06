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
package org.testifyproject.trait;

import static java.util.Optional.ofNullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.testifyproject.annotation.Bundle;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 * A contract that specifies annotated element traits.
 *
 * @author saden
 * @param <T> the annotation element type
 */
public interface AnnotationTrait<T extends AnnotatedElement> {

    /**
     * Get the annotated element .
     *
     * @return an instance of the annotated element.
     */
    T getAnnotatedElement();

    /**
     * Get the given annotation on the annotated element.
     *
     * @param <A> annotation type
     * @param annotationType the annotation type
     * @return optional with annotation, empty optional otherwise
     */
    default <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationType) {
        T type = getAnnotatedElement();
        A declaredAnnotation = type.getDeclaredAnnotation(annotationType);

        if (declaredAnnotation == null) {
            for (Annotation annotation : type.getDeclaredAnnotations()) {
                Class<? extends Annotation> declaredAnnotationType = annotation.annotationType();
                Bundle bundle = declaredAnnotationType.getDeclaredAnnotation(Bundle.class);

                if (bundle != null) {
                    declaredAnnotation = declaredAnnotationType
                            .getDeclaredAnnotation(annotationType);
                    break;
                }
            }
        }

        return ofNullable(declaredAnnotation);
    }

    /**
     * Get all the annotations of the given type.
     *
     * @param <A> annotation type
     * @param annotationType the annotation type
     * @return optional with annotation, empty optional otherwise
     */
    default <A extends Annotation> List<A> getAnnotations(Class<A> annotationType) {
        T type = getAnnotatedElement();
        A[] declaredAnnotations = type.getDeclaredAnnotationsByType(annotationType);

        ImmutableList.Builder<A> listBuilder = ImmutableList.builder();

        listBuilder.add(declaredAnnotations);

        for (Annotation annotation : type.getDeclaredAnnotations()) {
            Class<? extends Annotation> declaredAnnotationType = annotation.annotationType();
            Bundle bundle = declaredAnnotationType.getDeclaredAnnotation(Bundle.class);

            if (bundle != null) {
                A[] bundleAnnotations =
                        declaredAnnotationType.getDeclaredAnnotationsByType(annotationType);
                listBuilder.add(bundleAnnotations);
            }
        }

        return listBuilder.build();
    }

    /**
     * Get the meta annotations declared on the annotated element itself or annotations on the
     * annotated element.
     *
     * @param metaAnnotationTypes an array of meta annotations
     * @return an array of meta annotations
     */
    default Annotation[] getMetaAnnotations(
            Collection<Class<? extends Annotation>>... metaAnnotationTypes) {
        T annotatedElement = getAnnotatedElement();

        //XXX: dont execute on this stream in parallel
        Annotation[] annotations = Stream.of(metaAnnotationTypes).parallel()
                .flatMap(Collection::parallelStream)
                .distinct()
                .map(annotationType -> {
                    Annotation declaredAnnotation = annotatedElement.getDeclaredAnnotation(
                            annotationType);

                    if (declaredAnnotation == null) {
                        for (Annotation annotation : annotatedElement.getDeclaredAnnotations()) {
                            if (annotation.annotationType().isAnnotationPresent(annotationType)) {
                                return annotation;
                            }
                        }
                    }

                    return declaredAnnotation;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toArray(Annotation[]::new);

        return annotations;
    }

    /**
     * Determine if the annotated element has any of the given annotations.
     *
     * @param annotationTypes the annotations we are looking for
     * @return true if at least one annotation found, false otherwise
     */
    default boolean hasAnyAnnotations(Class<? extends Annotation>... annotationTypes) {
        T type = getAnnotatedElement();

        return Stream.of(annotationTypes)
                .parallel()
                .distinct()
                .anyMatch(p -> type.getDeclaredAnnotation(p) != null);
    }

    /**
     * Determine if the annotated element has any of the given annotations.
     *
     * @param annotationTypes the annotations we are looking for
     * @return true if at least one annotation found, false otherwise
     */
    default boolean hasAnyAnnotations(Collection<Class<? extends Annotation>> annotationTypes) {
        T type = getAnnotatedElement();

        return annotationTypes
                .stream()
                .map(type::getDeclaredAnnotation)
                .filter(Objects::nonNull)
                .findAny()
                .isPresent();
    }

}
