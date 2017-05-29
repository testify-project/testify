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
import org.testifyproject.LocalResourceInstance;
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

    private String fqn;
    private Instance<R> resource;
    private Instance<C> client;
    private final ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();

    /**
     * Create a new resource of LocalResourceInstanceBuilder.
     *
     * @return a new resource
     */
    public static LocalResourceInstanceBuilder builder() {
        return new LocalResourceInstanceBuilder<>();
    }

    /**
     * Set the fully qualified name of the local resource. When choosing a fqn
     * for the resource it is best to choose a fqn that reflect the resource
     * being provided to avoid potential collision with names used by other
     * virtual resource provider implementations.
     *
     * @param fqn the fully qualified name of the local resource
     * @return this object
     */
    public LocalResourceInstanceBuilder fqn(String fqn) {
        this.fqn = fqn;

        return this;
    }

    /**
     * Set the underlying resource to the given resource.
     *
     * @param resource the underlying resource
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> resource(R resource) {
        this.resource = DefaultInstance.of(resource);

        return this;
    }

    /**
     * Set the underlying resource to the given resource and contract.
     *
     * @param resource the underlying resource
     * @param contract the underlying resource contract
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> resource(R resource, Class<? extends R> contract) {
        this.resource = DefaultInstance.of(resource, contract);

        return this;
    }

    /**
     * Set the client of the underlying resource to the given client.
     *
     * @param client the underlying resource client
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> client(C client) {
        this.client = DefaultInstance.of(client);

        return this;
    }

    /**
     * Set the client of the underlying resource to the given client and
     * contract.
     *
     * @param client the underlying resource client resource
     * @param contract the underlying resource client contract
     * @return this object
     */
    public LocalResourceInstanceBuilder<R, C> client(C client, Class<? extends C> contract) {
        this.client = DefaultInstance.of(client, contract);

        return this;
    }

    /**
     * Associate the specified value with the specified key in the resource
     * resource.
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
     * Build and return a local resource instance based on the builder state.
     *
     * @return a local resource instance
     */
    public LocalResourceInstance<R, C> build() {
        //TODO: if fqn is not spcified then determine it from the caller class.
        //Once Java 9 is rleased use with JEP-259 StackWalker API.

        return DefaultLocalResourceInstance.of(fqn, resource, client, properties.build());
    }

}
