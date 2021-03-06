/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.testifyproject.ClientProvider;
import org.testifyproject.ServiceProvider;

/**
 * An annotation that can be placed a test class to provide hints to the test class. This is
 * useful when being explicit is necessary due to the presence of multiple implementations of
 * discoverable contracts in the classpath.
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Hint {

    /**
     * The service provider implementation class. Note that if a service provider is not
     * specified an implementation will be detected.
     *
     * @return the service provider implementation class
     */
    Class<? extends ServiceProvider> serviceProvider() default ServiceProvider.class;

    /**
     * The client provider implementation class. Note that if a client provider is not specified
     * an implementation will be detected.
     *
     * @return the client provider implementation class
     */
    Class<? extends ClientProvider> clientProvider() default ClientProvider.class;
}
