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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import org.testify.annotation.Module;
import org.testify.annotation.Scan;
import org.testify.guava.common.collect.ImmutableSet;

/**
 * A contract that defines methods for working with various dependency injection
 * frameworks to add services and modules as well as retrieve services.
 *
 * @author saden
 */
public interface ServiceInstance {

    /**
     * Determine if the service instance is running.
     *
     * @return true if the service is running, false otherwise.
     */
    default boolean isRunning() {
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
     * name and instance.
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
     * Get all the injection annotations classes supported by the service
     * locator.
     *
     * @return a set containing injection annotation classes.
     */
    default Set<Class<? extends Annotation>> getInjectionAnnotations() {
        return ImmutableSet.of(Inject.class);
    }

    /**
     * Get all the name qualifier annotation classes supported by the service
     * locator.
     *
     * @return a set containing name qualifier annotation classes.
     */
    default Set<Class<? extends Annotation>> getNameQualifers() {
        return ImmutableSet.of(Named.class);
    }

    /**
     * Get all the custom qualifier annotations classes supported by the service
     * locator.
     *
     * @return a set containing custom qualifier annotation classes.
     */
    default Set<Class<? extends Annotation>> getCustomQualifiers() {
        return ImmutableSet.of(Qualifier.class);
    }

}
