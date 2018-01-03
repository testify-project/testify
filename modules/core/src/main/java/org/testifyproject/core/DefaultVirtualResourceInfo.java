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

import org.testifyproject.VirtualResourceInfo;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Virtual resource implementation of {@link VirtualResourceInfo} contract.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultVirtualResourceInfo implements VirtualResourceInfo {

    private final VirtualResource annotation;
    private final VirtualResourceProvider provider;
    private final VirtualResourceInstance value;

    DefaultVirtualResourceInfo(VirtualResource resource, VirtualResourceProvider provider,
            VirtualResourceInstance value) {
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
    public static VirtualResourceInfo of(VirtualResource annotation,
            VirtualResourceProvider provider,
            VirtualResourceInstance value) {
        return new DefaultVirtualResourceInfo(annotation, provider, value);
    }

    @Override
    public VirtualResource getAnnotation() {
        return annotation;
    }

    @Override
    public VirtualResourceProvider getProvider() {
        return provider;
    }

    @Override
    public VirtualResourceInstance getValue() {
        return value;
    }
}
