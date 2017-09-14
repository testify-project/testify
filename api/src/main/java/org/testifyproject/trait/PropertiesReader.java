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
package org.testifyproject.trait;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * A contracts that specifies methods for reading property values. class.
 * </p>
 * <p>
 * Note that with respect to null keys and values the behavior the methods in contract are dependent
 * on the {@link Map} implementation returned by {@link #getProperties()
 * }
 * </p>
 *
 * @author saden
 */
public interface PropertiesReader extends PropertiesTrait {

    /**
     * <p>
     * Get a PropertiesReader instance for the given mapKey. Note that:
     * </p>
     * <ul>
     * <li>If the key is empty then the this PropertiesReader instance will be returned</li>
     * <li>If the key is not found then an empty map will be returned</li>
     * </ul>
     * @param key the key associated with the map
     * @return an instance that wraps the found map
     */
    default PropertiesReader getPropertiesReader(String key) {
        if (key.isEmpty()) {
            return this;
        }

        return DefaultPropertiesReader.of(findMap(key));
    }

    /**
     * Get the value the value associated with the given key.
     *
     * @param <T> the value type
     * @param key the key
     * @return the value to which the specified key is mapped, null otherwise
     */
    default <T> T getProperty(String key) {
        return (T) getProperties().get(key);
    }

    /**
     * Find the value associated with the given key.
     *
     * @param <T> the value type
     * @param key the key associated with the property
     * @return an optional with the value, empty optional otherwise
     */
    default <T> Optional<T> findProperty(String key) {
        return ofNullable((T) getProperties().get(key));
    }

    /**
     * Find a collection value associated with the given key. If the queue is not found an
     * {@link Collections#emptyList()} will be returned.
     *
     * @param <E> the collection element type
     * @param key the key associated with the queue
     * @return the found collection, empty collection otherwise
     */
    default <E> Collection<E> findCollection(String key) {
        Map<String, Collection<E>> properties = getProperties();

        return properties.getOrDefault(key, Collections.emptyList());
    }

    /**
     * Find a map typed associated with the given key. If the map is not found an
     * {@link Collections#emptyMap()} is returned.
     *
     * @param <K> the map key type
     * @param <V> the map value type
     * @param key the key associated with the map
     * @return the found map, empty map otherwise
     */
    default <K, V> Map<K, V> findMap(String key) {
        Map<String, Map<K, V>> properties = getProperties();

        return properties.getOrDefault(key, Collections.emptyMap());
    }

}
