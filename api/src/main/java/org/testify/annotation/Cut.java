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
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * An annotation used on single test class field to denote the field as the
 * Class Under Test (CUT).
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Cut {

    /**
     * <p>
     * Indicates whether the class under test instance is a virtual instance
     * (delegated mock). This is useful if you wish to stub or verify package
     * private methods of the class under test or verify certain interactions.
     * </p>
     * <p>
     * By default the cut class is not a virtual instance.
     * </p>
     *
     * @return true if a virtual instance is created, false otherwise.
     */
    boolean value() default false;
}
