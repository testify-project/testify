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
package org.testify;

import java.util.Collections;
import java.util.Set;
import org.testify.annotation.Application;

/**
 * A contract that defines methods for retrieving information about an
 * application.
 *
 * @author saden
 * @param <T> the application initializer type
 */
public interface ApplicationInstance<T> {

    /**
     * Get the application annotation.
     *
     * @return the application annotation.
     */
    Application getApplication();

    /**
     * Get the test context associated with the application.
     *
     * @return the test context instance.
     */
    TestContext getTestContext();

    /**
     * Get the application initializer class.
     *
     * @return the application initializer
     */
    T getInitializer();

    /**
     * Get the application initializer handler types.
     *
     * @return a set of application initializer handlers types
     */
    default Set<Class<?>> getHandlers() {
        return Collections.emptySet();
    }
}
