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
package org.testifyproject;

import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.trait.PropertiesReader;

/**
 * A contract that defines methods for getting information about a virtual resource.
 *
 * @author saden
 * @param <R> the underlying virtual resource type
 */
public interface VirtualResourceInstance<R> extends PropertiesReader {

    /**
     * Get a unique fully qualified name associated with the virtual resource.
     *
     * @return the virtual resource's fully qualified name
     */
    String getFqn();

    /**
     * Get the virtual resource annotation associated with the virtual resource instance.
     *
     * @return the virtual resource annotation
     */
    VirtualResource getVirtualResource();

    /**
     * Get the resource instance associated with the virtual resource.
     *
     * @return a virtual resource instance
     */
    Instance<R> getResource();

}
