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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation for:
 * </p>
 * <ul>
 * <li>denoting a method in a test class as a configuration handler method</li>
 * <li>specify at the test class level classes that contain configuration
 * handlers method</li>
 * </ul>
 * <p>
 * This is useful for configuring various functions before each integration and
 * system test run (i.e. HK2 ServiceLocator, Spring Application Context,
 * required container, required resources, etc) and sharing configuration
 * handler methods between multiple classes .
 * </p>
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE, METHOD})
public @interface ConfigHandler {

    /**
     * Specifies a list of classes that contain configuration method handles.
     * This is useful for sharing configuration method handlers between test
     * classes and avoid repetitive declaration of configuration methods in each
     * test class.
     *
     * @return a list of configuration handler classes.
     */
    Class<?>[] value() default {};

}
