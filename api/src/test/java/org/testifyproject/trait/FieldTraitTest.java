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
package org.testifyproject.trait;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.TestifyException;
import org.testifyproject.fixture.FieldService;
import org.testifyproject.fixture.TestContract;

/**
 *
 * @author saden
 */
public class FieldTraitTest {

    FieldTrait sut;

    @Before
    public void init() throws NoSuchFieldException {
        sut = mock(FieldTrait.class, Answers.CALLS_REAL_METHODS);

        Field field = FieldService.class.getDeclaredField("primary");
        Class type = field.getType();
        Type genericType = field.getGenericType();

        given(sut.getAnnotatedElement()).willReturn(field);
        given(sut.getType()).willReturn(type);
        given(sut.getGenericType()).willReturn(genericType);
    }

    @Test
    public void callToGetAnnotatedElementShouldReturnField() {
        Field result = sut.getAnnotatedElement();

        assertThat(result).isNotNull();
        assertThat(result.getType()).isAssignableFrom(TestContract.class);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceSetValueShouldThrowException() {
        sut.setValue(null, null);
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidInstanceTypeSetValueShouldThrowException() {
        sut.setValue("", null);
    }

    @Test
    public void givenInstanceSetValueShouldSetValue() {
        FieldService instance = new FieldService();

        sut.setValue(instance, null);
        assertThat(instance.getPrimary()).isNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceGetValueShouldThrowException() {
        sut.getValue(null);
    }

    @Test(expected = TestifyException.class)
    public void givenInvalidInstanceTypeGetValueShouldThrowException() {
        sut.getValue("");
    }

    @Test
    public void givenInstanceGetValueShouldSetValue() {
        FieldService instance = new FieldService();
        TestContract value = instance.getPrimary();

        Optional<TestContract> result = sut.getValue(instance);
        assertThat(result).contains(value);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInitShouldThrowException() {
        sut.init(null);
    }

    @Test
    public void givenInstanceInitShouldChangeMessageValue() {
        FieldService instance = new FieldService();
        TestContract value = instance.getPrimary();
        assertThat(value.getMessage()).isEqualTo("Hi!!!");

        sut.init(instance);
        assertThat(value.getMessage()).contains("init");
    }

    @Test(expected = NullPointerException.class)
    public void givenNullDestroyShouldThrowException() {
        sut.destroy(null);
    }

    @Test
    public void givenInstanceDestroyShouldChangeMessageValue() {
        FieldService instance = new FieldService();
        TestContract value = instance.getPrimary();
        assertThat(value.getMessage()).isEqualTo("Hi!!!");

        sut.destroy(instance);
        assertThat(value.getMessage()).contains("destroy");
    }

    @Test
    public void givenAutoCloseableInstanceDestroyShouldChangeMessage() throws
            NoSuchFieldException {
        Field field = FieldService.class.getDeclaredField("secondary");
        Class type = field.getType();
        Type genericType = field.getGenericType();

        given(sut.getAnnotatedElement()).willReturn(field);
        given(sut.getType()).willReturn(type);
        given(sut.getGenericType()).willReturn(genericType);

        FieldService instance = new FieldService();
        TestContract value = instance.getSecondary();
        assertThat(value.getMessage()).isEqualTo("Hi!!!");

        sut.destroy(instance);

        assertThat(value.getMessage()).contains("destroy");
    }

}
