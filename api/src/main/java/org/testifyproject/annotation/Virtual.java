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
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that can be placed on unit, integration and system test class
 * fields to denote the fields as a virtual collaborator. Virtual collaborators
 * are mock instances that delegate to a real instance of the collaborator of
 * the Class Under Test and are useful if you wish to mock certain functionality
 * (i.e. a method that calls a third-party service to process a credit card
 * charge) and delegate other functionality to the real collaborator instance.
 * </p>
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Virtual {

    /**
     * <p>
     * This value represents the field name of the class under test associated
     * with the test field. Please note that name based auto detection only
     * works if your code is compiled with parameter names or debug information
     * (javac -parameters or javac -g:vars).
     * </p>
     * <p>
     * By default this value is set to "" to enable auto detection. If you wish
     * to not rely on auto detection you can explicitly specify the name of the
     * class under test field associated with the test field.
     * </p>
     *
     * @return the class under test field name.
     */
    String value() default "";
}
