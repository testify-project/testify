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
package org.testifyproject.di.spring;

import java.lang.annotation.Annotation;

import javax.inject.Provider;

import org.testifyproject.ServiceInstance;

/**
 * SpringDefaultProvider is a class that enables the creation of a provider instance that
 * retrieves a service from the service instance.
 *
 * @author saden
 */
public class SpringDefaultProvider implements Provider {

    private final ServiceInstance serviceInstance;
    private final Class type;
    private final Annotation[] qualifiers;

    SpringDefaultProvider(ServiceInstance serviceInstance, Class<?> type,
            Annotation[] qualifiers) {
        this.serviceInstance = serviceInstance;
        this.type = type;
        this.qualifiers = qualifiers;
    }

    /**
     * Create a new provider instance with the given service instance and service type.
     *
     * @param serviceInstance the underlying service instance
     * @param type the service type
     * @return a provider instance
     */
    public static Provider of(ServiceInstance serviceInstance, Class type) {
        return new SpringDefaultProvider(serviceInstance, type, null);
    }

    /**
     * Create a new provider instance with the given service instance, service type, and service
     * qualifiers.
     *
     * @param serviceInstance the underlying service instance
     * @param type the service type
     * @param qualifiers the service qualifiers
     * @return a provider instance
     */
    public static Provider of(ServiceInstance serviceInstance, Class<?> type,
            Annotation[] qualifiers) {
        return new SpringDefaultProvider(serviceInstance, type, qualifiers);
    }

    /**
     * Provides a fully-constructed and injected instance of the service from the service
     * instance.
     *
     * @return an instance of the service.
     */
    @Override
    public Object get() {
        return qualifiers == null || qualifiers.length == 0
                ? serviceInstance.getService(type)
                : serviceInstance.getService(type, qualifiers);
    }

}
