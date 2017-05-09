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

import org.testifyproject.trait.PropertiesReadTrait;

/**
 * A contract that defines methods to get information about a remote resource
 * instance. A remote instance consists of a client that can be used to
 * communicate with the remote and properties associated with the remote
 * instance.
 *
 * @author saden
 * @param <C> the client type
 * @see ResourceProvider
 */
public interface RemoteResourceInstance<C> extends PropertiesReadTrait {

    /**
     * Get the client instance associated with the remote resource.
     *
     * @return a client instance
     */
    Instance<C> getClient();

}
