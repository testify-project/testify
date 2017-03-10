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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that can be placed on test related classes or test class fields
 * to denote them as test fixture. Note that if this annotation is placed on:
 * </p>
 * <ul>
 * <li>
 * a test class field then the value of the field can be
 * {@link Fixture#init() initialized} or {@link Fixture#destroy() destroyed}
 * before and after each test run:
 * <pre>
 * <code>
 * {@literal @}Module(MyModule.class)
 *  public class MyModuleServiceiT {
 *
 *     {@literal @}Cut
 *     {@literal @}Fixture(init = "init", destroy = "destroy")
 *      MyService cut;
 *
 * }
 * </code>
 * </pre>
 * </li>
 * <li>
 * a module class the services defined in the module will take precedence over
 * services defined in other modules. This is useful if you wish to substitute
 * certain services for testing purpose (i.e. load a different DataSource than
 * the one for production during test runs):
 * <pre>
 * <code>
 * {@literal @}Fixture
 *  public class MyModule {
 *
 *     ...
 *
 * }
 * </code>
 * </pre>
 *
 * </li>
 * </ul>
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
