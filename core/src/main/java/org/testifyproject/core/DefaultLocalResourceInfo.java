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

import org.testifyproject.LocalResourceInfo;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.LocalResourceProvider;
import org.testifyproject.annotation.LocalResource;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Local resource implementation of {@link LocalResourceInfo} contract.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultLocalResourceInfo implements LocalResourceInfo {

    private final LocalResource annotation;
    private final LocalResourceProvider provider;
    private final LocalResourceInstance value;

    DefaultLocalResourceInfo(LocalResource resource, LocalResourceProvider provider,
            LocalResourceInstance value) {
        this.annotation = resource;
        this.provider = provider;
        this.value = value;
    }

    /**
     * Create a new resource info instance based on the given parameters.
     *
     * @param annotation the resource annotation
     * @param provider the resource provider
     * @param value the underlying resource instance
     * @return a new ResourceInfo
     */
    public static LocalResourceInfo of(LocalResource annotation, LocalResourceProvider provider,
            LocalResourceInstance value) {
        return new DefaultLocalResourceInfo(annotation, provider, value);
    }

    @Override
    public LocalResource getAnnotation() {
        return annotation;
    }

    @Override
    public LocalResourceProvider getProvider() {
        return provider;
    }

    @Override
    public LocalResourceInstance getValue() {
        return value;
    }
}
