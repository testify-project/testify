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
 * An annotation used on test class fields to inject mocks of the class under
 * test's collaborators. Please note that if the field is initialized with a
 * mock instance the value of the field will be used and injected into the class
 * under test.
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Fake {

    /**
     * <p>
     * This value represents the field name of the class under test associated
     * with the test field. Please note that name based auto detection only
     * works if your code is compiled with parameter names or debug information
     * (javac -parameters or javac -g:vars).
     * </p>
     * <p>
     * By default this value is set to "" to enable auto detection. If you wish
     * to not rely on auto detection you can explicitly specify the field name
     * of the class under test.
     * </p>
     *
     * @return the class under test field name.
     */
    String value() default "";

}
