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
package org.testifyproject.extension.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * An annotation that can be placed on a class to donate it as capable of
 * handing a specific type of annotations. This is useful for writing custom
 * inspectors that handles a specific annotations.
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Handles {

    /**
     * The annotation classes that are inspected.
     *
     * @return the annotation classes.
     */
    Class<? extends Annotation>[] value();
}
