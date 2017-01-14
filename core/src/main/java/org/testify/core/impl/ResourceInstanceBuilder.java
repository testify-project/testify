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

import java.util.HashMap;
import java.util.Map;
import org.testify.Instance;
import org.testify.ResourceInstance;

/**
 * A builder class used to construction ResourceInstance instances.
 *
 * @author saden
 * @param <S> server resource instance type
 * @param <C> client resource instance type
 */
public class ResourceInstanceBuilder<S, C> {

    private Instance<S> server;
    private Instance<C> client;
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Set the server with the given instance.
     *
     * @param instance the server instance
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S instance) {
        this.server = new DefaultInstance(instance, null, null);

        return this;
    }

    /**
     * Set the server with the given instance and name.
     *
     * @param instance the server instance
     * @param name the server name
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S instance, String name) {
        this.server = new DefaultInstance(instance, name, null);

        return this;
    }

    /**
     * Set the server with the given instance, name, contract.
     *
     * @param instance the server instance
     * @param contract the server contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S instance, Class<? super S> contract) {
        this.server = new DefaultInstance(instance, null, contract);

        return this;
    }

    /**
     * Set the server with the given instance, name, contract.
     *
     * @param instance the server instance
     * @param name the server name
     * @param contract the server contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S instance, String name, Class<? super S> contract) {
        this.server = new DefaultInstance(instance, name, contract);

        return this;
    }

    /**
     * Set the client with the given instance.
     *
     * @param client the client instance
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> client(C client) {
        this.client = new DefaultInstance(client, null, null);

        return this;
    }

    /**
     * Set the client with the given instance and name.
     *
     * @param client the client instance
     * @param name the client name
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> client(C client, String name) {
        this.client = new DefaultInstance(client, name, null);

        return this;
    }

    /**
     * Set the client with the given instance, name, contract.
     *
     * @param client the client instance
     * @param contract the client contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> client(C client, Class<? super C> contract) {
        this.client = new DefaultInstance(client, null, contract);

        return this;
    }

    /**
     * Set the client with the given instance, name, contract.
     *
     * @param client the client instance
     * @param name the client name
     * @param contract the client contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> client(C client, String name, Class<? super C> contract) {
        this.client = new DefaultInstance(client, name, contract);

        return this;
    }

    public ResourceInstanceBuilder<S, C> property(String name, Object value) {
        this.properties.put(name, value);

        return this;
    }

    public ResourceInstance<S, C> build() {
        return new DefaultResourceInstance<>(server, client, properties);
    }

}
