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
package org.testifyproject.core.analyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.fixture.analyzer.AnalyzedSutClass;

/**
 *
 * @author saden
 */
public class DefaultParameterDescriptorTest {

    ParameterDescriptor sut;
    Parameter parameter;
    Integer index;

    @Before
    public void init() throws NoSuchMethodException {
        Constructor<AnalyzedSutClass> constructor
                = AnalyzedSutClass.class.getDeclaredConstructor(Map.class);

        index = 0;
        parameter = constructor.getParameters()[index];

        sut = new DefaultParameterDescriptor(parameter, index);
    }

    @Test
    public void callToInstanceOfShouldReturnParameterDescriptor() {
        ParameterDescriptor result = DefaultParameterDescriptor.of(parameter, index);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetAnnotatedElementShouldReturnParameter() {
        Parameter result = sut.getAnnotatedElement();

        assertThat(result).isEqualTo(parameter);
    }

    @Test
    public void callToGetNameShouldReturnParameterName() {
        String result = sut.getName();

        assertThat(result).isEqualTo(parameter.getName());
    }

    @Test
    public void callToGetTypeShouldReturnParameterType() {
        Class<?> result = sut.getType();

        assertThat(result).isEqualTo(parameter.getType());
    }

    @Test
    public void callToGetIndexShouldReturnParameterIndex() {
        Integer result = sut.getIndex();

        assertThat(result).isEqualTo(index);
    }

    @Test
    public void callToGetGenericTypeShouldReturnParameterGenericType() {
        Type result = sut.getGenericType();

        assertThat(result).isEqualTo(parameter.getParameterizedType());
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        assertThat(sut).isNotEqualTo(null);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        ParameterDescriptor unequal = DefaultParameterDescriptor.of(parameter, -1);

        assertThat(sut).isNotEqualTo(unequal);
        assertThat(sut.hashCode()).isNotEqualTo(unequal.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        ParameterDescriptor equal = DefaultParameterDescriptor.of(parameter, index);

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultParameterDescriptor", "parameter", "index");
    }

}
