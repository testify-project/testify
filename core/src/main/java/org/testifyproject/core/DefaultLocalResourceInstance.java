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

/**
 * A class that contains server and client instances of a local resource.
 *
 * @author saden
 * @param <S> server resource instance type
 * @param <C> client resource instance type
 */
@ToString
@EqualsAndHashCode
public class DefaultLocalResourceInstance<S, C> implements LocalResourceInstance<S, C> {

    private final Instance<S> instance;
    private final Instance<C> client;
    private final Map<String, Object> properties;

    DefaultLocalResourceInstance(Instance<S> instance,
            Instance<C> client,
            Map<String, Object> properties) {
        this.instance = instance;
        this.client = client;
        this.properties = properties;
    }

    /**
     * Create a resource instance based on the given server, client and properties.
     *
     * @param <S> server resource instance type
     * @param <C> client resource instance type
     * @param instance the underlying resource instance
     * @param client the client instance
     * @param properties the resource instance properties
     * @return a new resource instance
     */
    public static <S, C> LocalResourceInstance<S, C> of(Instance<S> instance,
            Instance<C> client,
            Map<String, Object> properties) {
        return new DefaultLocalResourceInstance<>(instance, client, properties);
    }

    @Override
    public Instance<S> getResource() {
        return instance;
    }

    @Override
    public Optional<Instance<C>> getClient() {
        return ofNullable(client);
    }

    @Override
    public <T> Optional<T> findProperty(String name) {
        return ofNullable((T) properties.get(name));
    }

}
