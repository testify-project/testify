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

import java.util.Map;

import org.testifyproject.Instance;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.annotation.VirtualResource;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An implementation of {@link VirtualResourceInstance SPI Contract} that provides information
 * about running container.
 *
 * @author saden
 * @param <R> the underlying virtual resource type
 */
@ToString
@EqualsAndHashCode
public class DefaultVirtualResourceInstance<R> implements VirtualResourceInstance<R> {

    private final String fqn;
    private final VirtualResource virtualResource;
    private final Instance<R> resource;
    private final Map<String, Object> properties;

    DefaultVirtualResourceInstance(String fqn,
            VirtualResource virtualResource,
            Instance<R> resource,
            Map<String, Object> properties) {
        this.fqn = fqn;
        this.virtualResource = virtualResource;
        this.resource = resource;
        this.properties = properties;
    }

    /**
     * Create a new container instance with the given parameters.
     *
     * @param <R> the underlying virtual resource type
     * @param fqn the virtual resource's fully qualified name
     * @param virtualResource the virtual resource annotation
     * @param resource the underlying virtual resource instance
     * @param properties properties associated with the instance
     * @return a new container instance.
     */
    public static <R> VirtualResourceInstance of(String fqn,
            VirtualResource virtualResource,
            Instance<R> resource,
            Map<String, Object> properties) {
        return new DefaultVirtualResourceInstance(fqn, virtualResource, resource, properties);
    }

    @Override
    public String getFqn() {
        return fqn;
    }

    @Override
    public VirtualResource getVirtualResource() {
        return virtualResource;
    }

    @Override
    public Instance<R> getResource() {
        return resource;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
