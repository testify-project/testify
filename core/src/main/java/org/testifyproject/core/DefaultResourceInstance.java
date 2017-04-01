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
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.testifyproject.Instance;
import org.testifyproject.ResourceInstance;

/**
 * A class that contains server and client instances of a local resource.
 *
 * @author saden
 * @param <S> server resource instance type
 * @param <C> client resource instance type
 */
public class DefaultResourceInstance<S, C> implements ResourceInstance<S, C> {

    private final Instance<S> instance;
    private final Instance<C> client;
    private final Map<String, Object> properties;

    /**
     * Create a resource instance based on the given server, client and
     * properties.
     *
     * @param <S> server resource instance type
     * @param <C> client resource instance type
     * @param instance the underlying resource instance
     * @param client the client instance
     * @param properties the resource instance properties
     * @return a new resource instance
     */
    public static <S, C> ResourceInstance<S, C> of(Instance<S> instance,
            Instance<C> client,
            Map<String, Object> properties) {
        return new DefaultResourceInstance<>(instance, client, properties);
    }

    DefaultResourceInstance(Instance<S> instance,
            Instance<C> client,
            Map<String, Object> properties) {
        this.instance = instance;
        this.client = client;
        this.properties = properties;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.instance);
        hash = 97 * hash + Objects.hashCode(this.client);
        hash = 97 * hash + Objects.hashCode(this.properties);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultResourceInstance<?, ?> other = (DefaultResourceInstance<?, ?>) obj;
        if (!Objects.equals(this.instance, other.instance)) {
            return false;
        }
        if (!Objects.equals(this.client, other.client)) {
            return false;
        }
        return Objects.equals(this.properties, other.properties);
    }

    @Override
    public String toString() {
        return "DefaultResourceInstance{"
                + "instance=" + instance
                + ", client=" + client
                + ", properties=" + properties
                + '}';
    }

}
