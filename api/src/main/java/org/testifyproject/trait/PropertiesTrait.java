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

import java.util.Map;

/**
 * <p>
 * A contracts that specifies Properties trait. Classes that implement this
 * interface have the ability to associate properties with the implementation
 * class.
 * </p>
 * <p>
 * Note that with respect to null keys and values the behavior the methods in
 * contract are dependent on the {@link Map} implementation returned by {@link #getProperties()
 * }
 * </p>
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
     * Determine if the map returned by {@link #getProperties() }
     * contains key-value mappings.
     *
     * @return true if properties contains no key-value mapping, false otherwise
     */
    default Boolean isEmpty() {
        return getProperties().isEmpty();
    }

}
