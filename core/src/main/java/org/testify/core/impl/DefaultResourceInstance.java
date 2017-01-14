/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.core.impl;

import org.testify.Instance;
import org.testify.ResourceInstance;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;

/**
 * A class that contains server and client instances of a required resource.
 *
 * @author saden
 * @param <S> server resource instance type
 * @param <C> client resource instance type
 */
public class DefaultResourceInstance<S, C> implements ResourceInstance<S, C> {

    private final Instance<S> server;
    private final Instance<C> client;
    private final Map<String, Object> properties;

    public DefaultResourceInstance(Instance<S> server,
            Instance<C> client,
            Map<String, Object> properties) {
        this.server = server;
        this.client = client;
        this.properties = properties;
    }

    @Override
    public Instance<S> getServer() {
        return server;
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
        hash = 97 * hash + Objects.hashCode(this.server);
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
        if (!Objects.equals(this.server, other.server)) {
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
                + "server=" + server
                + ", client=" + client
                + ", properties=" + properties
                + '}';
    }

}
