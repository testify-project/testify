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
package org.testify.annotation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import org.testify.ResourceProvider;

/**
 * <p>
 * An annotation that can be placed on integration and system tests to specify
 * resources that must be loaded, configured, started, stopped before and after
 * each test case (i.e. an in-memory database).
 * </p>
 * <p>
 * Note that an external resource consists of a server component and client
 * component (optional). For example, if a test class requires an in-memory
 * database then the database {@link javax.sql.DataSource} can be thought of as
 * the server component and the {@link java.sql.Connection} to the DataSource as
 * the client component.
 * </p>
 *
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
@Repeatable(RequiresResources.class)
public @interface RequiresResource {

    /**
     * Specifies the required resource's implementation provider class.
     *
     * @return the required resource implementation provider class.
     */
    Class<? extends ResourceProvider> value();

    /**
     * <p>
     * Specifies the name of the required resource. This is useful for injecting
     * the client and server supplied by the resource provider into your test
     * code.
     * </p>
     * <p>
     * Note that if a test class requires multiple resources then name must be
     * specified to resolve ambiguity as to which required resource should be
     * injected into your test code.
     * </p>
     *
     * @return the required resource name.
     */
    String name() default "";

    /**
     * <p>
     * Specifies the required resource's server component name. This useful for
     * giving the server resource instance a unique name that can be used to
     * qualify and distinguish it from other similar services.
     * </p>
     * <p>
     * Note that if the name is not specified the name provided by the required
     * resource implementation will be used.
     * </p>
     *
     * @return a the required resource's server component name.
     */
    String serverName() default "";

    /**
     * <p>
     * Specifies the required resource's server component contract class. This
     * useful for getting the server resource by its contract as well or its
     * implementation class.
     * </p>
     * <p>
     * Note that if the server contract class is not specified the server
     * resource instance will be injectable by its implementation class only.
     * </p>
     *
     * @return a the required resource's server component contract class.
     */
    Class<?> serverContract() default Class.class;

    /**
     * <p>
     * Specifies the required resource's client component name. This useful for
     * giving the client resource instance a unique name that can be used to
     * qualify and distinguish it from other similar services.
     * </p>
     * <p>
     * Note that if the name is not specified the name provided by the required
     * resource implementation will be used.
     * </p>
     *
     * @return a the required resource's client component name.
     */
    String clientName() default "";

    /**
     * <p>
     * Specifies the required resource's client component contract class. This
     * useful for getting the client resource by its contract as well or its
     * implementation class.
     * </p>
     * <p>
     * Note that if the client contract class is not specified the client
     * resource instance will be injectable by its implementation class only.
     * </p>
     *
     * @return a the required resource's client component contract class.
     */
    Class<?> clientContract() default Class.class;

}
