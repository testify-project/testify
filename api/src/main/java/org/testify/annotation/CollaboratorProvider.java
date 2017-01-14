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
 * <li>denoting a method in a test class as a provider of collaborators for the
 * class under test</li>
 * <li>specifying at the test class level a class that contains a method that
 * provides collaborators for the class under test.</li>
 * </ul>
 * <p>
 * This is useful for configuring a class under test whose collaborator(s) can
 * not be faked or virtualized (i.e. a collaborator that final class).
 * </p>
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE, METHOD})
public @interface CollaboratorProvider {

    /**
     * Specifies the class that contains methods that provide collaborator for
     * the class under test.
     *
     * @return the class.
     */
    Class<?> value() default void.class;
}
