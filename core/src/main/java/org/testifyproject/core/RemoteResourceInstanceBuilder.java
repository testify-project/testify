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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;

/**
 * A builder class used to construction RemoteResourceInstance instances.
 *
 * @author saden
 * @param <C> the remote resource client type
 * @see RemoteResourceInstance
 */
public class RemoteResourceInstanceBuilder< C> {

    private Instance<C> client;
    private final Map<String, Object> properties = new LinkedHashMap<>();

    /**
     * Create a new resource of RemoteResourceInstanceBuilder.
     *
     * @return a new resource
     */
    public static RemoteResourceInstanceBuilder builder() {
        return new RemoteResourceInstanceBuilder<>();
    }

    /**
     * Set the client of the underlying resource to the given client and name.
     * When choosing a name for the resource client it is best to choose a name
     * that reflect the resource being provided to avoid potential collision
     * with names used by other resource provider (i.e.
     * "myAwesomeResourceClient").
     *
     * @param client the underlying resource client resource
     * @param name the underlying resource client name
     * @return this object
     */
    public RemoteResourceInstanceBuilder<C> client(C client, String name) {
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
     * @param client the underlying resource client resource
     * @param name the underlying resource client name
     * @param contract the underlying resource client contract
     * @return this object
     */
    public RemoteResourceInstanceBuilder<C> client(C client, String name, Class<? extends C> contract) {
        this.client = new DefaultInstance(client, name, contract);

        return this;
    }

    /**
     * Associate the specified value with the specified name in the resource
     * resource.
     *
     * @param name the name with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return this object
     */
    public RemoteResourceInstanceBuilder<C> property(String name, Object value) {
        this.properties.put(name, value);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public RemoteResourceInstanceBuilder<C> properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a remote resource instance based on the builder state.
     *
     * @return a remote resource instance
     */
    public RemoteResourceInstance<C> build() {
        return DefaultRemoteResourceInstance.of(client, Collections.unmodifiableMap(properties));
    }

}
