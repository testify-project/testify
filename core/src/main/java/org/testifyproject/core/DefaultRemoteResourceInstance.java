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
import org.testifyproject.RemoteResourceInstance;
import org.testifyproject.annotation.RemoteResource;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A class that contains client instance and properties of a remote resource.
 *
 * @author saden
 * @param <R> the underlying remote resource type
 */
@ToString
@EqualsAndHashCode
public class DefaultRemoteResourceInstance<R> implements RemoteResourceInstance< R> {

    private final String fqn;
    private final RemoteResource remoteResource;
    private final Instance<R> resource;
    private final Map<String, Object> properties;

    DefaultRemoteResourceInstance(String fqn, RemoteResource remoteResource,
            Instance<R> resource,
            Map<String, Object> properties) {
        this.fqn = fqn;
        this.remoteResource = remoteResource;
        this.resource = resource;
        this.properties = properties;
    }

    /**
     * Create a remote resource instance based on the given client and properties.
     *
     *
     * @param <R> the underlying remote resource type
     * @param fqn the remote resource's fully qualified name
     * @param remoteResource the remote resource annotation
     * @param resource the underlying remote resource instance
     * @param properties the resource instance properties
     * @return a new resource instance
     */
    public static <R> RemoteResourceInstance<R> of(String fqn,
            RemoteResource remoteResource,
            Instance<R> resource,
            Map<String, Object> properties) {
        return new DefaultRemoteResourceInstance<>(fqn, remoteResource, resource, properties);
    }

    @Override
    public String getFqn() {
        return fqn;
    }

    @Override
    public RemoteResource getRemoteResource() {
        return remoteResource;
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
