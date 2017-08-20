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
 * fields to denote the fields as a fake collaborators. Fake collaborators are
 * mock instance of collaborators and allow us to mock functionality and verify
 * interaction between the System Under Test and the collaborator in isolation.
 * Note that if the value of the test class field is already initialized with:
 * </p>
 * <ul>
 * <li>a mock instance of the collaborator then this mock instance will be used
 * and injected into the system under test.
 * </li>
 * <li>
 * a concrete instance of the collaborator then a mock instances that delegates
 * to the field value will be created and injected into the system under test.
 * </li>
 * </ul>
 *
 * @author saden
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Fake {

}
