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

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An implementation of {@link PropertiesReader} contract.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class DefaultPropertiesReader implements PropertiesReader {

    private final Map<String, Object> properties;

    public DefaultPropertiesReader(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Create a new instance of {@code DefaultPropertiesReader} with the given properties.
     *
     * @param properties the underlying properties map
     * @return a new instance of {@code DefaultPropertiesReader}
     */
    public static PropertiesReader of(Map<String, Object> properties) {
        return new DefaultPropertiesReader(properties);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
