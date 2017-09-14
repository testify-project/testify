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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation used to mark source code or byte code that has been generated or altered
 * (sub-classed, redefined, rebased) for testing purpose and to differentiate it from user written
 * code. When used, the value element must have the name of the code generator. The recommended
 * convention is to use the fully qualified name of the code generator in the value field (i.e.
 * com.acme.generator.CodeGen).
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Testified {

    /**
     * The value element MUST have the name of the code generator. The recommended convention is to
     * use the fully qualified name of the code generator (i.e. com.acme.generator.CodeGen).
     *
     * @return the name of the code generator
     */
    String value();

    /**
     * Any comments that the code generator may want to include in the generated code.
     *
     * @return code comments.
     */
    String comments() default "";

}
