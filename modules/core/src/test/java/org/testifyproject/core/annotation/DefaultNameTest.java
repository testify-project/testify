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
package org.testifyproject.core.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.annotation.Name;

/**
 *
 * @author saden
 */
public class DefaultNameTest {

    Name sut;
    String value;

    @Before
    public void init() {
        value = "value";

        sut = new DefaultName(value);
    }

    @Test
    public void givenValueOfShouldReturnNameInstance() {
        Name result = DefaultName.of(value);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetValueShouldReturnValue() {
        String result = sut.value();

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void callToAnnotationTypeShouldReturnAnnotation() {
        Class<? extends Annotation> result = sut.annotationType();

        assertThat(result).isEqualTo(Name.class);
    }

}
