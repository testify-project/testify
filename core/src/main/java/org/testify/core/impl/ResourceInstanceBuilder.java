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
 * @see ResourceInstance
 */
public class ResourceInstanceBuilder<S, C> {

    private Instance<S> server;
    private Instance<C> client;
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Set the server with the given instance.
     *
     * @param server the server instance
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S server) {
        this.server = new DefaultInstance(server, null, null);

        return this;
    }

    /**
     * Set the server with the given instance and name.
     *
     * @param server the server instance
     * @param name the server name
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S server, String name) {
        this.server = new DefaultInstance(server, name, null);

        return this;
    }

    /**
     * Set the server with the given instance, name, contract.
     *
     * @param server the server instance
     * @param contract the server contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S server, Class<? super S> contract) {
        this.server = new DefaultInstance(server, null, contract);

        return this;
    }

    /**
     * Set the server with the given instance, name, contract.
     *
     * @param server the server instance
     * @param name the server name
     * @param contract the server contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> server(S server, String name, Class<? super S> contract) {
        this.server = new DefaultInstance(server, name, contract);

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

    /**
     * Associate the specified value with the specified key in the resource
     * instance.
     *
     * @param name the name with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> property(String name, Object value) {
        this.properties.put(name, value);

        return this;
    }

    /**
     * Associate the given properties with the resource instance.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a resource instance based on the builder state.
     *
     * @return a resource instance
     */
    public ResourceInstance<S, C> build() {
        return DefaultResourceInstance.of(server, client, properties);
    }

}
