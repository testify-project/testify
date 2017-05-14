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
import java.util.Optional;
import java.util.Set;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Scan;

/**
 * A contract that defines methods for working with various dependency injection
 * frameworks to add services and modules as well as retrieve services.
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
     * Add a constant with the given name and contract.
     *
     * @param instance the constant instance
     * @param name the name associated with the constant
     * @param contract the contract associated with the constant
     */
    void addConstant(Object instance, String name, Class contract);

    /**
     * Replace all services that implement the given contract with the given
     * name and instance. If services that implement the contract are not found
     * this method should behave like
     * {@link #addConstant(java.lang.Object, java.lang.String, java.lang.Class)}.
     *
     * @param instance the service instance
     * @param name the name of the service
     * @param contract the contract implemented by the instance
     *
     */
    void replace(Object instance, String name, Class contract);

    /**
     * Add the given module(s) to the service instance.
     *
     * @param modules an array of modules
     */
    void addModules(Module... modules);

    /**
     * Replace all services that implement the given {@link Instance} as well as
     * override the name and/or contract defined in the instance with the given
     * overrideName and overrideContract. Note that if overrideName is used if
     * it is not null or empty and overrideConctract is used if it is not null
     * or equal to Class.class.
     *
     * @param <T> the instance type
     * @param instance the instance
     * @param overrideName the override name
     * @param overrideContract the override contract
     */
    default <T> void replace(Instance<T> instance, String overrideName, Class overrideContract) {
        T constant = instance.getInstance();
        Optional<String> foundName = instance.getName();
        Optional<Class<? extends T>> foundContract = instance.getContract();

        String name = null;

        if (overrideName != null && !overrideName.isEmpty()) {
            name = overrideName;
        } else if (foundName.isPresent()) {
            name = foundName.get();
        }

        Class<? extends T> contract = null;

        if (overrideContract != null && !void.class.equals(overrideContract)) {
            contract = overrideContract;
        } else if (foundContract.isPresent()) {
            contract = foundContract.get();
        }

        replace(constant, name, contract);
    }

    /**
     * Determine if the service instance is running.
     *
     * @return true if the service is running, false otherwise.
     */
    default Boolean isRunning() {
        return false;
    }

    /**
     * Initialize the service instance.
     */
    default void init() {

    }

    /**
     * Destroy the service instance.
     */
    default void destroy() {

    }

    /**
     * <p>
     * Add the given scans to the service instance.
     * </p>
     * <p>
     * Please note that for some DI a resource might be a package (Spring) and
     * for others it might be service descriptor file (HK2).
     * </p>
     *
     * @param scans an array of scans
     */
    default void addScans(Scan... scans) {
    }

    /**
     * Injected collaborators into the given object instance.
     *
     * @param instance the instance that will be injected.
     */
    default void inject(Object instance) {

    }

    /**
     * Get all the name qualifier annotation classes supported by the service
     * locator.
     *
     * @return a set containing name qualifier annotation classes.
     */
    default Set<Class<? extends Annotation>> getNameQualifers() {
        return Collections.emptySet();
    }

    /**
     * Get all the custom qualifier annotations classes supported by the service
     * locator.
     *
     * @return a set containing custom qualifier annotation classes.
     */
    default Set<Class<? extends Annotation>> getCustomQualifiers() {
        return Collections.emptySet();
    }

}
