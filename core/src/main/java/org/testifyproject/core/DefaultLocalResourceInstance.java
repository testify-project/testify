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
import java.util.Optional;
import static java.util.Optional.ofNullable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.annotation.LocalResource;

/**
 * A class that contains resource, client, and properties of a local resource.
 *
 * @author saden
 * @param <R> the underlying resource type
 * @param <C> the underlying resource client type
 */
@ToString
@EqualsAndHashCode
public class DefaultLocalResourceInstance<R, C> implements LocalResourceInstance<R, C> {

    private final String fqn;
    private final LocalResource localResource;
    private final Instance<R> resource;
    private final Instance<C> client;
    private final Map<String, Object> properties;

    DefaultLocalResourceInstance(String fqn,
            LocalResource localResource,
            Instance<R> resource,
            Instance<C> client,
            Map<String, Object> properties) {
        this.fqn = fqn;
        this.localResource = localResource;
        this.resource = resource;
        this.client = client;
        this.properties = properties;
    }

    /**
     * Create a local resource instance based on the given resource, client and
     * properties.
     *
     * @param <R> the underlying resource type
     * @param <C> the underlying resource client type
     * @param fqn the local resource's fully qualified name
     * @param localResource the local resource annotation
     * @param resource the underlying local resource instance
     * @param client the client instance
     * @param properties the resource instance properties
     * @return a new resource instance
     */
    public static <R, C> LocalResourceInstance<R, C> of(String fqn,
            LocalResource localResource,
            Instance<R> resource,
            Instance<C> client,
            Map<String, Object> properties) {
        return new DefaultLocalResourceInstance<>(fqn, localResource, resource, client, properties);
    }

    @Override
    public String getFqn() {
        return fqn;
    }

    @Override
    public LocalResource getLocalResource() {
        return localResource;
    }

    @Override
    public Instance<R> getResource() {
        return resource;
    }

    @Override
    public Optional<Instance<C>> getClient() {
        return ofNullable(client);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
