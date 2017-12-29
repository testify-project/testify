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

import org.testifyproject.ClientInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 * A builder class used to construction {@link ClientInstance} instances.
 *
 * @author saden
 * @param <C> the client type
 * @param <P> the client supplier type
 */
public class ClientInstanceBuilder<C, P> {

    private C client;
    private Class<C> clientContract;
    private P clientSupplier;
    private Class<P> clientSupplierContract;
    private final ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();

    /**
     * Create a new instance of ClientInstanceBuilder.
     *
     * @return a new client instance builder
     */
    public static ClientInstanceBuilder builder() {
        return new ClientInstanceBuilder<>();
    }

    /**
     * Set the underlying client to the given client.
     *
     * @param client the underlying client
     * @return this object
     */
    public ClientInstanceBuilder<C, P> client(C client) {
        this.client = client;

        return this;
    }

    /**
     * Set the underlying client to the given client and contract.
     *
     * @param client the underlying client
     * @param contract the underlying client contract
     * @return this object
     */
    public ClientInstanceBuilder<C, P> client(C client, Class<C> contract) {
        this.client = client;
        this.clientContract = contract;

        return this;
    }

    /**
     * Set the underlying client clientSupplier to the given client clientSupplier.
     *
     * @param clientSupplier the underlying client clientSupplier
     * @return this object
     */
    public ClientInstanceBuilder<C, P> clientSupplier(P clientSupplier) {
        this.clientSupplier = clientSupplier;

        return this;
    }

    /**
     * Set the underlying client clientSupplier to the given client clientSupplier and contract.
     *
     * @param clientSupplier the underlying client clientSupplier
     * @param clientSupplierContract the underlying client clientSupplier contract
     * @return this object
     */
    public ClientInstanceBuilder<C, P> clientSupplier(P clientSupplier,
            Class<P> clientSupplierContract) {
        this.clientSupplier = clientSupplier;
        this.clientSupplierContract = clientSupplierContract;

        return this;
    }

    /**
     * Associate the specified value with the specified key in the resource resource.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return this object
     */
    public ClientInstanceBuilder<C, P> property(String key, Object value) {
        this.properties.put(key, value);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public ClientInstanceBuilder<C, P> properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a client instance based on the builder state and given application
     * annotation.
     *
     * @param fqn the fully qualified name of the local resource
     * @param application the application annotation
     * @return a virtual resource instance
     */
    public ClientInstance<C, P> build(String fqn, Application application) {
        String clientName = application.clientName();
        String clientNameSupplier = application.clientSupplierName();

        return DefaultClientInstance.of(fqn,
                application,
                DefaultInstance.of(client, clientName, clientContract),
                DefaultInstance.of(clientSupplier, clientNameSupplier, clientSupplierContract),
                properties.build());

    }

}
