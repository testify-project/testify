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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that can be placed on a test class or test class method to configure various
 * functions before each integration and system test run (i.e. HK2 ServiceLocator, Spring
 * Application Context, resources, etc). Note that if the annotation is placed on:
 * </p>
 * <ul>
 * <li>
 * a test class method then this configuration method will be called to perform pre-test run
 * configuration.
 * </li>
 * <li>
 * the test class and {@link ConfigHandler#value() } is specified then a configuration method
 * within {@link ConfigHandler#value()
 * } class will be called to perform pre-test run configuration. This is useful for sharing
 * configuration method handlers between test classes and avoid repetitive declaration of
 * configuration methods in each test class.
 * </li>
 * </ul>
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE, METHOD})
public @interface ConfigHandler {

    /**
     * Specifies a list of classes that contain configuration method handles.
     *
     * @return a list of configuration handler classes.
     */
    Class<?>[] value() default {};

}
