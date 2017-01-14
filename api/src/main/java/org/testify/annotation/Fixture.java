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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation used on classes or fields to denote them as test fixture.
 * </p>
 * <p>
 * Please note that classes with this annotation will be managed. If values for
 * {@link #init()} and {@link #destroy()} are specified classes and fields with
 * this annotation will be initialized and destroyed before and after each test
 * case.
 * </p>
 * <p>
 * In the event that this annotation is placed on a module class the
 * bindings/configuration defined in the module will take precedence over other
 * bindings defined in other modules. This is useful if you wish to substitute
 * certain bindings for testing purpose only (i.e. bind and load a different
 * DataSource than the one for production).
 * </p>
 *
 * @author saden
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Fixture {

    /**
     * Indicates the fixture instance method name called to initialize the
     * fixture.
     *
     * @return the fixture initialization method name
     */
    String init() default "";

    /**
     * Indicates the fixture instance method name called to destroy the fixture.
     *
     * @return the fixture destroy method name
     */
    String destroy() default "";

    /**
     * <p>
     * Indicates whether fixtures that implement {@link java.lang.AutoCloseable}
     * interfaces should be closed automatically after the test run.
     * </p>
     * <p>
     * By default this is enabled. If the fixture does not implement
     * {@link java.lang.AutoCloseable} interface you can explicitly specify the
     * method to call by setting {@link #destroy() }
     * </p>
     *
     * @return true if auto destroy is enabled, false otherwise
     */
    boolean autoClose() default true;
}
