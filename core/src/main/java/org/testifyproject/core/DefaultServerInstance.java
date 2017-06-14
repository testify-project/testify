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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.Instance;
import org.testifyproject.ServerInstance;

/**
 * Default implementation of {@link ServerInstance} contract.
 *
 * @author saden
 * @param <T> the underlying server type
 */
@ToString
@EqualsAndHashCode
public class DefaultServerInstance<T> implements ServerInstance<T> {

    private final String fqn;
    private final URI baseURI;
    private final Instance<T> server;
    private final Map<String, Object> properties;

    DefaultServerInstance(String fqn, URI baseURI, Instance<T> server, Map<String, Object> properties) {
        this.fqn = fqn;
        this.baseURI = baseURI;
        this.server = server;
        this.properties = properties;
    }

    /**
     * Create a server instance with the given base URI and test context.
     *
     * @param <T> the underlying server type
     * @param fqn the instance's fully qualified name
     * @param baseURI the server's base uri
     * @param server the underlying server instance
     * @param properties the properties associated with the instance
     * @return a server instance
     */
    public static <T> ServerInstance<T> of(String fqn, URI baseURI, Instance<T> server, Map<String, Object> properties) {
        return new DefaultServerInstance<>(fqn, baseURI, server, properties);
    }

    @Override
    public String getFqn() {
        return fqn;
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public Instance<T> getServer() {
        return server;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
