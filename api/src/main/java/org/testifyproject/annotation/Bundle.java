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
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * <p>
 * A meta-annotation that identifies an annotation as test group. A test group
 * annotation provide the ability to define, group, and use one or more Testify
 * annotations in a reusable manner in your test classes and avoid annotation
 * bloat.
 * </p>
 * <pre>
 * <code>
 * {@literal @}Bundle
 * {@literal @}Module(MyModule.class)
 * {@literal @}RequiresContainer("postgres")
 * {@literal @}Target(ElementType.TYPE)
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 *  public @interface MyModuleGroup { }
 *
 *
 * {@literal @}MyModuleGroup
 *  public class MyModuleServiceiT {
 *
 *     ...
 *
 * }
 * </code>
 * </pre>
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Bundle {
}
