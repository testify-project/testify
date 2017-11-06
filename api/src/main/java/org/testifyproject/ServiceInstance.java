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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * A contract that defines methods for working with various dependency injection frameworks to
 * add services and modules as well as retrieve services.
 *
 * @author saden
 */
public interface ServiceInstance {

    /**
     * Get the context object associated with the service instance.
     *
     * @param <T> the type of the context object
     * @return the service instance context object.
     */
    <T> T getContext();

    /**
     * Get a service with the given name.
     *
     * @param <T> the service type
     * @param type the service type
     * @param name the service name
     * @return an instance of the service
     */
    <T> T getService(Type type, String name);

    /**
     * Get a service with the given qualifiers.
     *
     * @param <T> the service type
     * @param type the service type
     * @param qualifiers the service qualifiers
     * @return an instance of the service
     */
    <T> T getService(Type type, Annotation... qualifiers);

    /**
     * Determine if the service instance is running.
     *
     * @return true if the service is running, false otherwise.
     */
    default Boolean isRunning() {
        return false;
    }

    /**
     * Destroy the service instance.
     */
    default void destroy() {

    }

    /**
     * Injected collaborators into the given object instance.
     *
     * @param instance the instance that will be injected.
     */
    default void inject(Object instance) {

    }

    /**
     * Get all the name qualifier annotation classes supported by the service locator.
     *
     * @return a set containing name qualifier annotation classes.
     */
    default Set<Class<? extends Annotation>> getNameQualifers() {
        return Collections.emptySet();
    }

    /**
     * Get all the custom qualifier annotations classes supported by the service locator.
     *
     * @return a set containing custom qualifier annotation classes.
     */
    default Set<Class<? extends Annotation>> getCustomQualifiers() {
        return Collections.emptySet();
    }

}
