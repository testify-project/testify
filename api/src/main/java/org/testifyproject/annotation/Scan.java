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
 * An annotation that can be placed on integration and system tests to load a resources that
 * contains services before each test run (i.e. Spring service fully qualified package name, HK2
 * service locator descriptor classpath).
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
@Repeatable(Scans.class)
public @interface Scan {

    /**
     * <p>
     * A value that represents a resource in the classpath that should be scanned and loaded.
     * </p>
     * <p>
     * Please note that to encourage simplicity and modular design scanning of resources is limited
     * to a single resource. If you absolutely need to scan and load multiple resources Scan
     * annotation is {@link Scans repeatable}.
     * </p>
     *
     * @return a resource name.
     */
    String value();

}
