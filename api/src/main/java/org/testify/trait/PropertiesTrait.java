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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.WeakHashMap;

/**
 * A contracts that specifies Properties trait. Classes that implement this
 * interface have the ability to manage a set of properties associated with the
 * implementation.
 *
 * @author saden
 */
public interface PropertiesTrait {

    /**
     * A map instance that holds properties associated with the implementation
     * of PropertiesTrait.
     */
    Map<PropertiesTrait, Map<String, Object>> PROPERTIES = new WeakHashMap<>();

    /**
     * Create or get the properties associated with this implementation of
     * properties trait.
     *
     * @return a map containing key/value pairs
     */
    default Map<String, Object> getProperties() {
        return PROPERTIES.computeIfAbsent(this, p -> new HashMap<>());
    }

    /**
     * The the property associated with the given key.
     *
     * @param <T> the value type
     * @param key the key
     * @return an optional with the value, empty optional otherwise
     */
    default <T> Optional<T> getProperty(String key) {
        return ofNullable((T) getProperties().get(key));
    }

    /**
     * Add the given key/value pair.
     *
     * @param <T> the value type
     * @param key the key
     * @param value the value
     */
    default <T> void addProperty(String key, T value) {
        getProperties().put(key, value);
    }

}
