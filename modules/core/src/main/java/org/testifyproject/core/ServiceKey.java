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

import java.lang.reflect.Type;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A key object used to find a service based on its name and type.
 *
 * @author saden
 */
@ToString
@EqualsAndHashCode
public class ServiceKey {

    private final Type type;
    private final String name;

    public ServiceKey(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Create a new service key instance from the given type.
     *
     * @param type the underlying type
     * @return a service key instance
     */
    public static ServiceKey of(Type type) {
        return new ServiceKey(type, null);
    }

    /**
     * Create a new service key instance from the given type and name.
     *
     * @param type the underlying type
     * @param name the underlying name
     * @return a service key instance
     */
    public static ServiceKey of(Type type, String name) {
        return new ServiceKey(type, name);
    }

    /**
     * The service type.
     *
     * @return service type
     */
    public Type getType() {
        return type;
    }

    /**
     * The service name.
     *
     * @return service name
     */
    public String getName() {
        return name;
    }
}
