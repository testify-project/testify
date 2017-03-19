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

/**
 * A contract that defines methods for retrieving information about a client and
 * communicating with web resources running inside of the server.
 *
 * @author saden
 * @param <T> the target type
 */
public interface ClientInstance<T> extends Instance<T> {

    /**
     * Get a client instance that can be used to communicate with web resources
     * running inside of the server.
     *
     * @return a client instance.
     */
    @Override
    T getInstance();

}
