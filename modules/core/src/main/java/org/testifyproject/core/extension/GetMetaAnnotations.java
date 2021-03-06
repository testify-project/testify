/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.core.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.testifyproject.extension.Operation;

/**
 * Get annotations with meta annotations for a parameter. This operation can discover
 * annotations on a parameter with the specified meta annotations. This is useful for
 * discovering custom qualifiers.
 *
 * @author saden
 */
public class GetMetaAnnotations implements Operation<Annotation[]> {

    private final Parameter parameter;
    private final Collection<Class<? extends Annotation>>[] metaAnnotationTypes;

    public GetMetaAnnotations(Parameter parameter,
            Collection<Class<? extends Annotation>>... metaAnnotationTypes) {
        this.parameter = parameter;
        this.metaAnnotationTypes = metaAnnotationTypes;
    }

    @Override
    public Annotation[] execute() {
        return Stream.of(metaAnnotationTypes).parallel()
                .flatMap(Collection::parallelStream)
                .distinct()
                .map(annotationType -> {
                    Annotation declaredAnnotation =
                            parameter.getDeclaredAnnotation(annotationType);

                    if (declaredAnnotation == null) {
                        for (Annotation annotation : parameter.getDeclaredAnnotations()) {
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
    }

}
