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

import org.testifyproject.RemoteResourceInfo;
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.RemoteResourceProvider;
import org.testifyproject.annotation.RemoteResource;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Remote resource implementation of {@link RemoteResourceInfo} contract.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultRemoteResourceInfo implements RemoteResourceInfo {

    private final RemoteResource annotation;
    private final RemoteResourceProvider provider;
    private final RemoteResourceInstance value;

    DefaultRemoteResourceInfo(RemoteResource resource, RemoteResourceProvider provider,
            RemoteResourceInstance value) {
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
    public static RemoteResourceInfo of(RemoteResource annotation,
            RemoteResourceProvider provider,
            RemoteResourceInstance value) {
        return new DefaultRemoteResourceInfo(annotation, provider, value);
    }

    @Override
    public RemoteResource getAnnotation() {
        return annotation;
    }

    @Override
    public RemoteResourceProvider getProvider() {
        return provider;
    }

    @Override
    public RemoteResourceInstance getValue() {
        return value;
    }
}
