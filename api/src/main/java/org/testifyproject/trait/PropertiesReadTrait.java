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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;

/**
 * <p>
 * A contracts that specifies methods for reading property values. class.
 * </p>
 * <p>
 * Note that with respect to null keys and values the behavior the methods in
 * contract are dependent on the {@link Map} implementation returned by {@link #getProperties()
 * }
 * </p>
 *
 * @author saden
 */
public interface PropertiesReadTrait extends PropertiesTrait {

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
     * @param key the key
     * @return an optional with the value, empty optional otherwise
     */
    default <T> Optional<T> findProperty(String key) {
        return ofNullable((T) getProperties().get(key));
    }

    /**
     * Find a list value associated with the given key. If the list is not found
     * an {@link Collections#EMPTY_LIST} will be returned.
     *
     * @param <T> the collection element type
     * @param key the key
     * @return an optional with the value, empty optional otherwise
     */
    default <T> List<T> findList(String key) {
        Map<String, List<T>> properties = getProperties();
        List<T> result = properties.get(key);

        if (result == null) {
            result = Collections.emptyList();
        }

        return result;
    }

    /**
     * Find a map typed associated with the given key. If the map is not found
     * an {@link Collections#EMPTY_MAP} is returned.
     *
     * @param <K> the map key type
     * @param <V> the map value type
     * @param key the key
     * @return the found map, empty map otherwise
     */
    default <K, V> Map<K, V> findMap(String key) {
        Map<String, Map<K, V>> properties = getProperties();
        Map<K, V> result = properties.get(key);

        if (result == null) {
            result = Collections.emptyMap();
        }

        return result;
    }

}
