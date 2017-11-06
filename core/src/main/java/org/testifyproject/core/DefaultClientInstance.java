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

import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Optional;

import org.testifyproject.ClientInstance;
import org.testifyproject.Instance;
import org.testifyproject.annotation.Application;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default implementation of {@link ClientInstance} contract.
 *
 * @author saden
 * @param <C> the client type
 * @param <P> the client supplier type
 */
@ToString
@EqualsAndHashCode
public class DefaultClientInstance<C, P> implements ClientInstance<C, P> {

    private final String fqn;
    private final Application application;
    private final Instance<C> client;
    private final Instance clientProvider;
    private final Map<String, Object> properties;

    DefaultClientInstance(
            String fqn,
            Application application,
            Instance<C> client,
            Instance clientProvider,
            Map<String, Object> properties) {
        this.fqn = fqn;
        this.application = application;
        this.client = client;
        this.clientProvider = clientProvider;
        this.properties = properties;
    }

    /**
     * Create a client instance based on the given parameters.
     *
     * @param <C> the client type
     * @param <P> the client supplier type
     * @param fqn the fully qualified name of the client
     * @param application the application annotation
     * @param clientProvider the underlying local provider instance
     * @param client the client instance
     * @param properties the provider instance properties
     * @return a new provider instance
     */
    public static <C, P> DefaultClientInstance<C, P> of(
            String fqn,
            Application application,
            Instance<C> client,
            Instance<P> clientProvider,
            Map<String, Object> properties) {
        return new DefaultClientInstance<>(fqn, application, client, clientProvider, properties);
    }

    @Override
    public String getFqn() {
        return fqn;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public Instance<C> getClient() {
        return client;
    }

    @Override
    public Optional<Instance<P>> getClientSupplier() {
        return ofNullable(clientProvider);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
