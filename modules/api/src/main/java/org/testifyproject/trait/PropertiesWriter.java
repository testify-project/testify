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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

/**
 * <p>
 * A contracts that specifies methods for writing property values. Note that property keys are
 * immutable (added if they absent). Elements and entries can still be added to list and map
 * elements.
 * </p>
 * <p>
 * Note that with respect to null keys and values the behavior the methods in contract are
 * dependent on the {@link Map} implementation returned by {@link #getProperties()
 * }
 * </p>
 *
 * @author saden
 */
public interface PropertiesWriter extends PropertiesTrait {

    /**
     * If the specified key is not already associated with a value (or is mapped to null),
     * attempts to compute its value using the given mapping function and enters it into this
     * map unless null.
     *
     * @param <T> the value type
     * @param key the key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the specified key
     */
    default <T> T computeIfAbsent(String key, Function<String, T> mappingFunction) {
        Map<String, T> properties = getProperties();

        return properties.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Add the given key/value pair if it is absent.
     *
     * @param <T> the value type
     * @param key the key
     * @param value the value
     * @return this object
     */
    default <T> PropertiesWriter addProperty(String key, T value) {
        Map<String, T> properties = getProperties();

        properties.computeIfAbsent(key, p -> value);

        return this;
    }

    /**
     * Add the given element to a collection entry in the properties map with the given key.
     *
     * @param <E> the element type
     * @param key the properties map key
     * @param element the element that will be added
     * @return this object
     */
    default <E> PropertiesWriter addCollectionElement(String key, E element) {
        Map<String, Collection<E>> properties = getProperties();

        Collection<E> result = properties.computeIfAbsent(key, p ->
                new ConcurrentLinkedQueue<>());

        result.add(element);
        return this;
    }

    /**
     * Add the given entryKey/entryValue pair to a map entry in the properties map with the
     * given key.
     *
     * @param <K> the entry key type
     * @param <V> the entry value type
     * @param key the properties map key
     * @param entryKey the new entry key that will be added
     * @param entryValue the new entry value that will be added
     * @return this object
     */
    default <K, V> PropertiesWriter addMapEntry(String key, K entryKey, V entryValue) {
        Map<String, Map<K, V>> properties = getProperties();

        Map<K, V> result = properties.computeIfAbsent(key, p -> new ConcurrentHashMap<>());
        result.computeIfAbsent(entryKey, k -> entryValue);

        return this;
    }

}
