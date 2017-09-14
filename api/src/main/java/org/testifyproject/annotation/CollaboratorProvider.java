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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation used to specifying a provider of the system under test's collaborators. This
 * annotation can be placed on a test class or method within a test class. This is useful for
 * configuring a system under test whose collaborator(s) can not be faked or virtualized (i.e. a
 * {@link java.net.URL} collaborator which is a final class). Note that if this annotation is placed
 * on:
 * </p>
 * <ul>
 * <li>
 * a test class method then this method will be called to provide the system under test's
 * collaborators.
 * </li>
 * <li>
 * the test class and {@link CollaboratorProvider#value() } is specified then a method within {@link CollaboratorProvider#value()
 * } class will be called to provide collaborators for the system under test.
 * </li>
 * </ul>
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE, METHOD, FIELD})
public @interface CollaboratorProvider {

    /**
     * Specifies a list of classes that contain methods that provide collaborator for the system
     * under test.
     *
     * @return a list of collaborator provider classes.
     */
    Class<?>[] value() default {};
}
