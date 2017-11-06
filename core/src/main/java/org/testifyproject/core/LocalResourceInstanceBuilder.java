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

import org.testifyproject.Instance;
import org.testifyproject.LocalResourceInstance;
import org.testifyproject.annotation.LocalResource;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 * A builder class used to construction LocalResourceInstance instances.
 *
 * @author saden
 * @param <R> the underlying local resource type
 * @param <C> the local resource client type
 * @see LocalResourceInstance
 */
public class LocalResourceInstanceBuilder<R, C> {

    private final ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();
    private R resource;
    private Class<R> resourceContract;
    private C client;
    private Class<C> clientContract;

    /**
     * Create a new instance of LocalResourceInstanceBuilder.
     *
     * @return a new resource builder
     */
    public static LocalResourceInstanceBuilder builder() {
        return new LocalResourceInstanceBuilder<>();
    }

    /**
     * Set the underlying resource to the given resource.
     *
     * @param resource the underlying resource
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> resource(R resource) {
        this.resource = resource;

        return this;
    }

    /**
     * Set the underlying resource to the given resource and contract.
     *
     * @param resource the underlying resource
     * @param contract the underlying resource contract
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> resource(R resource, Class<R> contract) {
        this.resource = resource;
        this.resourceContract = contract;

        return this;
    }

    /**
     * Set the client of the underlying resource to the given client.
     *
     * @param client the underlying resource client
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> client(C client) {
        this.client = client;

        return this;
    }

    /**
     * Set the client of the underlying resource to the given client and contract.
     *
     * @param client the underlying resource client resource
     * @param contract the underlying resource client contract
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> client(C client, Class<C> contract) {
        this.client = client;
        this.clientContract = contract;

        return this;
    }

    /**
     * Associate the specified value with the specified key in the resource resource.
     *
     * @param ket the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> property(String ket, Object value) {
        this.properties.put(ket, value);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a local resource instance based on the builder state and the given fqn
     * (fully qualified name). When choosing a fqn for the resource it is best to choose a fqn
     * that reflect the resource being provided to avoid potential collision with names used by
     * other virtual resource provider implementations.
     *
     * @param fqn the fully qualified name of the local resource
     * @param localResource the local resource annotation
     * @return a local resource instance
     */
    public LocalResourceInstance<R, C> build(String fqn, LocalResource localResource) {
        Instance<C> clientInstance = createClient(fqn, localResource);
        Instance<R> resourceInstance = createResource(fqn, localResource);

        return DefaultLocalResourceInstance.of(fqn,
                localResource,
                resourceInstance,
                clientInstance,
                properties.build());
    }

    Instance<R> createResource(String fqn, LocalResource localResource) {
        if (resource == null) {
            return null;
        }

        String resourceName;

        if ("".equals(localResource.resourceName())) {
            resourceName = Paths.get("resource:/", fqn, "resource").toString();
        } else {
            resourceName = Paths.get("resource:/", fqn, localResource.resourceName()).toString();
        }

        if (!void.class.equals(localResource.resourceContract())) {
            resourceContract = localResource.resourceContract();
        }

        return DefaultInstance.of(resource, resourceName, resourceContract);

    }

    Instance<C> createClient(String fqn, LocalResource localResource) {
        if (client == null) {
            return null;
        }

        String clientName;

        if ("".equals(localResource.clientName())) {
            clientName = Paths.get("resource:/", fqn, "client").toString();
        } else {
            clientName = Paths.get("resource:/", fqn, localResource.clientName()).toString();
        }

        if (!void.class.equals(localResource.clientContract())) {
            clientContract = localResource.clientContract();
        }

        return DefaultInstance.of(client, clientName, clientContract);
    }

}
