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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.testifyproject.Instance;
import org.testifyproject.RemoteResourceInstance;

/**
 * A class that contains client instance and properties of a remote resource.
 *
 * @author saden
 * @param <C> the remote resource client type
 */
@ToString
@EqualsAndHashCode
public class DefaultRemoteResourceInstance<C> implements RemoteResourceInstance< C> {

    private final Instance<C> client;
    private final Map<String, Object> properties;

    DefaultRemoteResourceInstance(Instance<C> client, Map<String, Object> properties) {
        this.client = client;
        this.properties = properties;
    }

    /**
     * Create a remote resource instance based on the given client and
     * properties.
     *
     * @param <C> client the remote resource client type
     * @param client the client instance
     * @param properties the resource instance properties
     * @return a new resource instance
     */
    public static < C> RemoteResourceInstance< C> of(Instance<C> client, Map<String, Object> properties) {
        return new DefaultRemoteResourceInstance<>(client, properties);
    }

    @Override
    public Instance<C> getClient() {
        return client;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
