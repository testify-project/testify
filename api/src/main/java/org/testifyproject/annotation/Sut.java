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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation used on single test class field to denote the field as the System Under Test (SUT).
 * Please note that what constitutes the system under test depends on the context of what we are
 * testing. It refer to a class, a service, or a client used to communicate with an application.
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Sut {

    /**
     * <p>
     * Indicates whether the system under test instance is a virtual instance (delegated mock). This
     * is useful if you wish to stub or verify package private methods of the system under test or
     * verify certain interactions.
     * </p>
     * <p>
     * By default the SUT is not a virtual instance.
     * </p>
     *
     * @return true if a virtual instance is created, false otherwise.
     */
    boolean value() default false;

    /**
     * Indicates the method in the system under test that should be used to create an instance of
     * the system under test.
     *
     * @return the factory method name.
     */
    String factoryMethod() default "";

    /**
     * <p>
     * Indicates whether all interaction between system under test and its collaborators should be
     * verified.
     * </p>
     * <p>
     * By default verification is not performed.
     * </p>
     *
     * @return true if interaction should be verified, false otherwise.
     */
    boolean verify() default false;

}
