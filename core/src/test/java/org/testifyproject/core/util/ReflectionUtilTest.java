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
package org.testifyproject.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.fixture.reflection.CustomAnnotation;
import org.testifyproject.fixture.reflection.CustomConstructorService;
import org.testifyproject.fixture.reflection.DefaultConstructorService;

/**
 *
 * @author saden
 */
public class ReflectionUtilTest {

    ReflectionUtil sut;

    @Before
    public void init() {
        sut = new ReflectionUtil();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullNewInstanceShouldThrowException() {
        sut.newInstance(null);
    }

    @Test
    public void givenAnnotationClassNewInstanceShouldReturnAnnotationInstance() {
        CustomAnnotation result = sut.newInstance(CustomAnnotation.class);

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo("custom");
    }

    @Test
    public void givenClassWithDefaultConstrsutorAndArgsNewInstanceShouldIgnoreArgs() {
        sut.newInstance(DefaultConstructorService.class, "test");
    }

    @Test
    public void givenClassWithDefaultConstrsutorNewInstanceShouldReturnInstance() {
        DefaultConstructorService result = sut.newInstance(DefaultConstructorService.class);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenClassWithCustomConstrsutorAndNoArgsNewInstanceShouldIgnoreArgs() {
        sut.newInstance(CustomConstructorService.class);
    }

    @Test
    public void givenClassWithCustomConstrsutorAndArgsNewInstanceShouldReturnInstance() {
        CustomConstructorService result = sut.newInstance(CustomConstructorService.class,
                "Hello!");

        assertThat(result).isNotNull();
    }

}
