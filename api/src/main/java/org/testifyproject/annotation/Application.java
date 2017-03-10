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
package org.testifyproject.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import org.testifyproject.ClientProvider;
import org.testifyproject.ServerProvider;

/**
 * An annotation that can be placed on system test to specify an application
 * that should be loaded, configured, started, and stopped before and after each
 * test run (i.e. Jersey 2, Spring Boot, Spring MVC, etc).
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
public @interface Application {

    /**
     * The class of the application under test that will be configured, started
     * and stopped before and after each test run.
     *
     * @return the application class.
     */
    Class<?> value();

    /**
     * Specifies the class that provides
     * {@link ClientProvider client provider implementation}. If a provider is
     * not specified one will be discovered in the class path.
     *
     * @return client provider implementation class.
     */
    Class<? extends ClientProvider> clientProvider() default ClientProvider.class;

    /**
     * Specifies the class that provides
     * {@link ServerProvider server provider implementation}. If a provider is
     * not specified one will be discovered in the class path.
     *
     * @return server provider implementation class.
     */
    Class<? extends ServerProvider> serverProvider() default ServerProvider.class;

}
