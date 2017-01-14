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
package org.testify.trait;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static java.util.stream.Stream.of;

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
     * @param <T> annotation type
     * @param type the annotation type
     * @return optional with annotation, empty optional otherwise
     */
    default <T extends Annotation> Optional<T> getAnnotation(Class<T> type) {
        return ofNullable(getAnnotatedElement().getDeclaredAnnotation(type));
    }

    /**
     * Get all the annotations on the annotated type.
     *
     * @return a list of annotations, empty list otherwise
     */
    default List<? extends Annotation> getAnnotations() {
        return of(getAnnotatedElement().getDeclaredAnnotations()).parallel()
                .collect(toList());
    }

    /**
     * Get all the annotations of the given type.
     *
     * @param <T> the annotation type
     * @param type the annotation type
     * @return optional with annotation, empty optional otherwise
     */
    default <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return of(getAnnotatedElement().getDeclaredAnnotations())
                .parallel()
                .filter(p -> p.annotationType().equals(type))
                .map(p -> (T) p)
                .collect(toList());
    }

    /**
     * Get all the annotations of the given types.
     *
     * @param <T> the annotation type
     * @param annotations the annotation types
     * @return optional with annotation, empty optional otherwise
     */
    default <T extends Annotation> List<T> getAnnotations(Collection<Class<? extends Annotation>> annotations) {
        return of(getAnnotatedElement().getDeclaredAnnotations())
                .parallel()
                .filter(p -> annotations.contains(p.annotationType()))
                .map(p -> (T) p)
                .collect(toList());
    }

    /**
     * Get the meta annotations declared on the annotated element itself or
     * annotations on the annotated element.
     *
     * @param metaAnnotations an array of meta annotations
     * @return an array of meta annotations
     */
    default Annotation[] getMetaAnnotations(Collection<Class<? extends Annotation>>... metaAnnotations) {
        T type = getAnnotatedElement();

        return Stream.of(metaAnnotations).parallel()
                .flatMap(p -> p.parallelStream())
                .distinct()
                .map(p -> {
                    Annotation declaredAnnotation = type.getDeclaredAnnotation(p);

                    if (declaredAnnotation == null) {
                        Annotation[] annotations = type.getDeclaredAnnotations();
                        for (Annotation annotation : annotations) {
                            if (annotation.annotationType().isAnnotationPresent(p)) {
                                return annotation;
                            }
                        }

                        return null;
                    }

                    return declaredAnnotation;
                })
                .filter(Objects::nonNull)
                .toArray(Annotation[]::new);
    }

    /**
     * Determine if the annotated element has any of the given annotations.
     *
     * @param annotations the annotations we are looking for
     * @return true if at least one annotation found, false otherwise
     */
    default boolean hasAnyAnnotations(Class<? extends Annotation>... annotations) {
        T type = getAnnotatedElement();

        return of(annotations)
                .parallel()
                .distinct()
                .anyMatch(p -> type.getDeclaredAnnotation(p) != null);
    }

    /**
     * Determine if the annotated element has any of the given annotations.
     *
     * @param annotations the annotations we are looking for
     * @return true if at least one annotation found, false otherwise
     */
    default boolean hasAnyAnnotations(Collection<Class<? extends Annotation>> annotations) {
        T type = getAnnotatedElement();

        return of(type.getDeclaredAnnotations())
                .parallel()
                .distinct()
                .anyMatch(p -> annotations.contains(p.annotationType()));
    }

}
