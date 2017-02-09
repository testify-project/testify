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
package org.testify.core.annotation;

import java.lang.annotation.Annotation;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testify.annotation.Module;

/**
 *
 * @author saden
 */
public class DefaultModuleTest {

    Module cut;
    Class<?> value;

    @Before
    public void init() {
        value = Object.class;

        cut = new DefaultModule(value);
    }

    @Test
    public void givenValueOfShouldReturnModuleInstance() {
        Module result = DefaultModule.of(value);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetValueShouldReturnValue() {
        Class<?> result = cut.value();

        assertThat(result).isEqualTo(value);
    }

    @Test
    public void callToAnnotationTypeShouldReturnAnnotation() {
        Class<? extends Annotation> result = cut.annotationType();

        assertThat(result).isEqualTo(Module.class);
    }

}
