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

import java.nio.file.Paths;
import java.util.Map;
import org.testifyproject.ClientInstance;
import org.testifyproject.Instance;
import org.testifyproject.annotation.Application;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 * A builder class used to construction {@link ClientInstance} instances.
 *
 * @author saden
 * @param <C> the underlying client type
 */
public class ClientInstanceBuilder<C> {

    private C client;
    private Class<C> clientContract;
    private Object clientProvider;
    private Class providerContract;
    private final ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();

    /**
     * Create a new resource of VirtualResourceInstanceBuilder.
     *
     * @return a new resource builder
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
    public ClientInstanceBuilder<C> client(C client) {
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
    public ClientInstanceBuilder<C> client(C client, Class<C> contract) {
        this.client = client;
        this.clientContract = contract;

        return this;
    }

    /**
     * Set the underlying client clientProvider to the given client
     * clientProvider.
     *
     * @param <P> the underlying client clientProvider
     * @param clientProvider the underlying client clientProvider
     * @return this object
     */
    public <P> ClientInstanceBuilder<C> clientProvider(P clientProvider) {
        this.clientProvider = clientProvider;

        return this;
    }

    /**
     * Set the underlying client clientProvider to the given client
     * clientProvider and contract.
     *
     * @param <P> the underlying client clientProvider
     * @param clientProvider the underlying client clientProvider
     * @param contract the underlying client clientProvider contract
     * @return this object
     */
    public <P> ClientInstanceBuilder<C> clientProvider(P clientProvider, Class<P> contract) {
        this.clientProvider = clientProvider;
        this.providerContract = contract;

        return this;
    }

    /**
     * Associate the specified value with the specified key in the resource
     * resource.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return this object
     */
    public ClientInstanceBuilder<C> property(String key, Object value) {
        this.properties.put(key, value);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public ClientInstanceBuilder<C> properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a client instance based on the builder state and given
     * application annotation.
     *
     * @param fqn the fully qualified name of the local resource
     * @param application the application annotation
     * @return a virtual resource instance
     */
    public ClientInstance<C> build(String fqn, Application application) {
        Instance<C> clientInstance = createClient(fqn, application);
        Instance clientProviderInstance = createClientProvider(fqn, application);

        return DefaultClientInstance.of(fqn,
                application,
                clientInstance,
                clientProviderInstance,
                properties.build());

    }

    Instance<C> createClient(String fqn, Application application) {
        if (client == null) {
            return null;
        }

        String clientName;

        if ("".equals(application.clientName())) {
            clientName = Paths.get("application:/", fqn, "client").toString();
        } else {
            clientName = Paths.get("application:/", fqn, application.clientName()).toString();
        }

        if (!void.class.equals(application.clientContract())) {
            clientContract = application.clientContract();
        }

        return DefaultInstance.of(client, clientName, clientContract);
    }

    Instance createClientProvider(String fqn, Application application) {
        if (clientProvider == null) {
            return null;
        }

        String resourceName;

        if ("".equals(application.clientProviderName())) {
            resourceName = Paths.get("application:/", fqn, "clientProvider").toString();
        } else {
            resourceName = Paths.get("application:/", fqn, application.clientProviderName()).toString();
        }

        if (!void.class.equals(application.clientProviderContract())) {
            providerContract = application.clientProviderContract();
        }

        return DefaultInstance.of(clientProvider, resourceName, providerContract);
    }
}
