/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.junit5;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.testifyproject.junit5.resolver.CollaboratorParameterResolver;
import org.testifyproject.junit5.resolver.FakeParameterResolver;
import org.testifyproject.junit5.resolver.RealParameterResolver;
import org.testifyproject.junit5.resolver.VirtualParameterResolver;

/**
 * A JUnit 5 annotation that can be placed on unit test classes. This annotation provides the
 * ability to create a system under test (SUT) instance and substitute instances of its
 * collaborators with fake, virtual or real instances.
 *
 * @author saden
 */
@ExtendWith({
    UnitTestExtension.class,
    TestifyExtension.class,
    FakeParameterResolver.class,
    VirtualParameterResolver.class,
    RealParameterResolver.class,
    CollaboratorParameterResolver.class
})
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface UnitTest {

}
