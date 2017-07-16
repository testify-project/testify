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
package org.testifyproject.core;

import java.lang.annotation.Annotation;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.ResourceInstance;

/**
 * Default implementation of {@link ResourceInstance} contract.
 *
 * @author saden
 * @param <A> the resource annotation type
 * @param <P> the resource provider type
 * @param <I> the resource instance type
 */
@ToString
@EqualsAndHashCode
public class DefaultResourceInstance<A extends Annotation, P, I> implements ResourceInstance<A, P, I> {

    private final A annotation;
    private final P provider;
    private final I value;

    DefaultResourceInstance(A resource, P provider, I value) {
        this.annotation = resource;
        this.provider = provider;
        this.value = value;
    }

    /**
     * Create a new resource instance based on the given parameters.
     *
     * @param <A> the resource annotation type
     * @param <P> the resource provider type
     * @param <I> the resource instance type
     * @param annotation the resource annotation
     * @param provider the resource provider
     * @param value the underlying resource instance
     * @return a new ResourceInstance
     */
    public static <A extends Annotation, P, I> ResourceInstance of(A annotation, P provider, I value) {
        return new DefaultResourceInstance(annotation, provider, value);
    }

    @Override
    public A getAnnotation() {
        return annotation;
    }

    @Override
    public P getProvider() {
        return provider;
    }

    @Override
    public I getValue() {
        return value;
    }

}
