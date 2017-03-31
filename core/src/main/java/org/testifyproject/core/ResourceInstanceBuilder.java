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

import java.util.LinkedHashMap;
import java.util.Map;
import org.testifyproject.Instance;
import org.testifyproject.ResourceInstance;

/**
 * A builder class used to construction ResourceInstance instances.
 *
 * @author saden
 * @param <S> instance resource instance type
 * @param <C> client resource instance type
 * @see ResourceInstance
 */
public class ResourceInstanceBuilder<S, C> {

    private Instance<S> instance;
    private Instance<C> client;
    private final Map<String, Object> properties = new LinkedHashMap<>();

    /**
     * Create a new instance of ResourceInstanceBuilder.
     *
     * @return a new instance
     */
    public static ResourceInstanceBuilder builder() {
        return new ResourceInstanceBuilder<>();
    }

    /**
     * Set the underlying resource instance to the given instance and name. When
     * choosing a name for the resource instance it is best to choose a name
     * that reflect the resource being provided to avoid potential collision
     * with names used by other resource provider (i.e.
     * "myAwesomeResourceServer").
     *
     * @param instance the underlying resource instance
     * @param name the underlying resource instance name
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> instance(S instance, String name) {
        this.instance = new DefaultInstance(instance, name, null);

        return this;
    }

    /**
     * Set the underlying resource instance to the given instance, name,
     * contract. When choosing a name for the resource instance it is best to
     * choose a name that reflect the resource being provided to avoid potential
     * collision with names used by other resource provider (i.e.
     * "myAwesomeResourceServer").
     *
     * @param instance the underlying resource instance
     * @param name the underlying resource instance name
     * @param contract the underlying resource instance contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> instance(S instance, String name, Class<? extends S> contract) {
        this.instance = new DefaultInstance(instance, name, contract);

        return this;
    }

    /**
     * Set the client of the underlying resource to the given client and name.
     * When choosing a name for the resource client it is best to choose a name
     * that reflect the resource being provided to avoid potential collision
     * with names used by other resource provider (i.e.
     * "myAwesomeResourceClient").
     *
     * @param client the underlying resource client instance
     * @param name the underlying resource client name
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> client(C client, String name) {
        this.client = new DefaultInstance(client, name, null);

        return this;
    }

    /**
     * Set the client of the underlying resource to the given client, name,
     * contract. When choosing a name for the resource client it is best to
     * choose a name that reflect the resource being provided to avoid potential
     * collision with names used by other resource provider (i.e.
     * "myAwesomeResourceClient").
     *
     * @param client the underlying resource client instance
     * @param name the underlying resource client name
     * @param contract the underlying resource client contract
     * @return this object
     */
    public ResourceInstanceBuilder<S, C> client(C client, String name, Class<? extends C> contract) {
        this.client = new DefaultInstance(client, name, contract);

        return this;
    }

    /**
     * Associate the specified value with the specified name in the resource
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
        return DefaultResourceInstance.of(instance, client, properties);
    }

}
