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

import java.net.URI;
import java.util.Map;

import org.testifyproject.Instance;
import org.testifyproject.ServerInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 * A builder class used to construction a ServerInstance instances.
 *
 * @author saden
 * @param <T> the underlying server type
 * @see ServerInstance
 */
public class ServerInstanceBuilder<T> {

    private URI baseURI;
    private Instance<T> server;
    private final ImmutableMap.Builder<String, Object> properties = ImmutableMap.builder();

    /**
     * Create a new resource of ServerInstanceBuilder.
     *
     * @return a new server instance builder
     */
    public static ServerInstanceBuilder builder() {
        return new ServerInstanceBuilder();
    }

    /**
     * Set the server base URI.
     *
     * @param baseURI server base URI.
     * @return this object
     */
    public ServerInstanceBuilder<T> baseURI(URI baseURI) {
        this.baseURI = baseURI;

        return this;
    }

    /**
     * Set the underlying server of the given instance.
     *
     * @param server the underlying server
     * @return this object
     */
    public ServerInstanceBuilder<T> server(T server) {
        this.server = DefaultInstance.of(server);

        return this;
    }

    /**
     * Set the underlying server instance and contract.
     *
     * @param server the underlying server
     * @param contract the underlying server's contract
     * @return this object
     */
    public ServerInstanceBuilder<T> server(T server, Class<? extends T> contract) {
        this.server = DefaultInstance.of(server, contract);

        return this;
    }

    /**
     * Associate the specified value with the specified key in the resource resource.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return this object
     */
    public ServerInstanceBuilder property(String key, Object value) {
        this.properties.put(key, value);

        return this;
    }

    /**
     * Associate the given properties with the resource resource.
     *
     * @param properties a map that contains key value pairs.
     * @return this object
     */
    public ServerInstanceBuilder properties(Map<String, Object> properties) {
        this.properties.putAll(properties);

        return this;
    }

    /**
     * Build and return a server instance based on the builder state, application and the given
     * fqn (fully qualified name). When choosing a fqn for the resource it is best to choose a
     * fqn that reflect the resource being provided to avoid potential collision with names used
     * by other virtual resource provider implementations.
     *
     * @param fqn the fully qualified name of the virtual resource
     * @param application the application annotation
     * @return a server instance
     */
    public ServerInstance<T> build(String fqn, Application application) {
        return DefaultServerInstance.of(fqn, application, baseURI, server, properties.build());
    }
}
