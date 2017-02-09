/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.trait;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;

/**
 * A contracts that specifies Properties trait. Classes that implement this
 * interface have the ability to associate and manage properties with the
 * implementation class.
 *
 * @author saden
 */
public interface PropertiesTrait {

    /**
     * Get the properties associated with this implementation of
     * PropertiesTrait.
     *
     * @param <V> map entry value type
     * @return a map containing key/value pairs
     */
    <V> Map<String, V> getProperties();

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
            result = Collections.EMPTY_LIST;
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
            result = Collections.EMPTY_MAP;
        }

        return result;
    }

    /**
     * Add the given key/value pair if it is absent.
     *
     * @param <T> the value type
     * @param key the key
     * @param value the value
     */
    default <T> void addProperty(String key, T value) {
        Map<String, T> properties = getProperties();

        properties.computeIfAbsent(key, p -> value);
    }

    /**
     * Add the given entryKey/entryValue pair to a map entry in the properties
     * map with the given key.
     *
     * @param <K> the entry key type
     * @param <V> the entry value type
     * @param key the properties map key
     * @param entryKey the new entry key that will be added
     * @param entryValue the new entry value that will be added
     */
    default <K, V> void addMapEntry(String key, K entryKey, V entryValue) {
        Map<String, Map<K, V>> properties = getProperties();

        Map<K, V> result = properties.computeIfAbsent(key, p -> new HashMap<>());
        result.computeIfAbsent(entryKey, k -> entryValue);
    }

    /**
     * Add the given element to a collection entry in the properties map with
     * the given key.
     *
     * @param <E> the element type
     * @param key the properties map key
     * @param element the element that will be added
     */
    default <E> void addListElement(String key, E element) {
        Map<String, List<E>> properties = getProperties();

        List<E> result = properties.computeIfAbsent(key, p -> new LinkedList<>());
        result.add(element);
    }

}
