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
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 * A builder class used to construction VirtualResourceInstance instances.
 *
 * @author saden
 * @param <R> the underlying virtual resource type
 * @see VirtualResourceInstance
 */
public class VirtualResourceInstanceBuilder<R> {

    private String fqn;
    private Instance<R> resource;
    private final ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();

    /**
     * Create a new resource of VirtualResourceInstanceBuilder.
     *
     * @return a new resource builder
     */
    public static VirtualResourceInstanceBuilder builder() {
        return new VirtualResourceInstanceBuilder();
    }

    /**
     * Set the underlying resource to the given resource.
     *
     * @param resource the underlying resource
     * @return this object
     */
    public VirtualResourceInstanceBuilder<R> resource(R resource) {
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
    public VirtualResourceInstanceBuilder<R> resource(R resource, Class<? extends R> contract) {
        this.resource = DefaultInstance.of(resource, contract);

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
    public VirtualResourceInstanceBuilder property(String key, Object value) {
        this.properties.put(key, value);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public VirtualResourceInstanceBuilder properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a virtual resource instance based on the builder state
     * and the given fqn (fully qualified name). When choosing a fqn for the
     * resource it is best to choose a fqn that reflect the resource being
     * provided to avoid potential collision with names used by other virtual
     * resource provider implementations.
     *
     * @param fqn the fully qualified name of the virtual resource
     * @return a virtual resource instance
     */
    public VirtualResourceInstance build(String fqn) {
        return DefaultVirtualResourceInstance.of(fqn, resource, properties.build());
    }
}
