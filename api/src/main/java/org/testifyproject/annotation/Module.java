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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that can be placed on integration and system tests to load a module that
 * contains services before each test run (i.e. Spring's Java Config, HK2's AbstractBinder, or
 * Guice's AbstractModule)
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
@Repeatable(Modules.class)
public @interface Module {

    /**
     * <p>
     * A value that represents a module class that should be loaded before each test run.
     * </p>
     * <p>
     * Please note that to encourage simplicity and modular design loading of modules is limited
     * to a single module class. If you absolutely need to load multiple modules this annotation
     * is{@link Scans repeatable}.
     * </p>
     *
     * @return a module class.
     */
    Class<?> value();

    /**
     * An attribute that indicates whether the module is a for testing purpose. If the module is
     * for testing purpose the services defined in the module will take precedence over services
     * defined in other modules. This is useful if you wish to substitute certain services for
     * testing purpose (i.e. load a different DataSource than the one for production during test
     * runs):
     *
     * @return true if the module is for testing purpose, false otherwise.
     */
    boolean test() default false;

}
