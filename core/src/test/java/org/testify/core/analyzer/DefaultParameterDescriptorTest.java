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
package org.testify.core.analyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testify.ParameterDescriptor;
import org.testify.fixture.analyzer.AnalyzedCutClass;

/**
 *
 * @author saden
 */
public class DefaultParameterDescriptorTest {

    ParameterDescriptor cut;
    Parameter parameter;
    Integer index;

    @Before
    public void init() throws NoSuchMethodException {
        Constructor<AnalyzedCutClass> constructor
                = AnalyzedCutClass.class.getDeclaredConstructor(Map.class);

        index = 0;
        parameter = constructor.getParameters()[index];

        cut = new DefaultParameterDescriptor(parameter, index);
    }

    @Test
    public void callToInstanceOfShouldReturnParameterDescriptor() {
        ParameterDescriptor result = DefaultParameterDescriptor.of(parameter, index);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetAnnotatedElementShouldReturnParameter() {
        Parameter result = cut.getAnnotatedElement();

        assertThat(result).isEqualTo(parameter);
    }

    @Test
    public void callToGetNameShouldReturnParameterName() {
        String result = cut.getName();

        assertThat(result).isEqualTo(parameter.getName());
    }

    @Test
    public void callToGetTypeShouldReturnParameterType() {
        Class<?> result = cut.getType();

        assertThat(result).isEqualTo(parameter.getType());
    }

    @Test
    public void callToGetGenericTypeShouldReturnParameterGenericType() {
        Type result = cut.getGenericType();

        assertThat(result).isEqualTo(parameter.getParameterizedType());
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(cut).isNotEqualTo(null);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        ParameterDescriptor unequal = DefaultParameterDescriptor.of(parameter, -1);

        assertThat(cut).isNotEqualTo(unequal);
        assertThat(cut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ParameterDescriptor equal = DefaultParameterDescriptor.of(parameter, index);

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultParameterDescriptor", "parameter", "index");
    }

}
