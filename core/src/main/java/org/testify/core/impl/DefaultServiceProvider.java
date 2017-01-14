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
package org.testify.core.impl;

import java.lang.annotation.Annotation;
import javax.inject.Provider;
import org.testify.ServiceInstance;

/**
 * DefaultServiceProvider is a class that enables the creation of a provider
 * instance that retrieves a service from the service instance.
 *
 * @author saden
 */
public class DefaultServiceProvider implements Provider {

    private final ServiceInstance serviceInstance;
    private final Class type;
    private final Annotation[] qualifiers;

    public DefaultServiceProvider(ServiceInstance serviceInstance, Class type) {
        this(serviceInstance, type, null);
    }

    public DefaultServiceProvider(ServiceInstance serviceInstance, Class<?> type, Annotation[] qualifiers) {
        this.serviceInstance = serviceInstance;
        this.type = type;
        this.qualifiers = qualifiers;
    }

    /**
     * Provides a fully-constructed and injected instance of the service from
     * the service instance.
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
