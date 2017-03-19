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

/**
 * A class that defines a list of common
 * {@link org.testifyproject.ApplicationInstance application instance}
 * properties.
 *
 * @author saden
 */
public class ApplicationInstanceProperties {

    public static final String SERVLET_CONTAINER_INITIALIZER = "servlet.container.initializer";
    public static final String SERVLET_HANDLERS = "servlet.handlers";

    private ApplicationInstanceProperties() {
    }

}
