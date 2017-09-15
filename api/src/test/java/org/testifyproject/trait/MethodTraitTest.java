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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.TestifyException;
import org.testifyproject.fixture.PrimaryTestService;

/**
 *
 * @author saden
 */
public class MethodTraitTest {

    MethodTrait sut;

    @Before
    public void init() throws NoSuchMethodException {
        sut = mock(MethodTrait.class, Answers.CALLS_REAL_METHODS);
        String methodName = "sayHello";
        Class<?>[] methodArgs = new Class[]{String.class};

        Method method = PrimaryTestService.class.getDeclaredMethod(methodName, methodArgs);

        given(sut.getAnnotatedElement()).willReturn(method);
    }

    @Test
    public void callToGetAnnotatedElementShouldReturnField() {
        Method result = sut.getAnnotatedElement();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetParameterTypesShouldReturn() {
        List<Class> result = sut.getParameterTypes();

        assertThat(result).containsExactly(String.class);
    }

    @Test
    public void callToGetParametersShouldReturn() {
        List<Parameter> result = sut.getParameters();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(String.class);
    }

    @Test
    public void callToGetReturnTypeShouldReturn() {
        Class<?> result = sut.getReturnType();

        assertThat(result).isEqualTo(String.class);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullHasReturnTypeShouldThrowException() {
        Type returnType = null;

        sut.hasReturnType(returnType);
    }

    @Test
    public void givenNonMatchingReturnTypeHasReturnTypeShouldReturnFalse() {
        Type returnType = Integer.class;

        Boolean result = sut.hasReturnType(returnType);
        assertThat(result).isFalse();
    }

    @Test
    public void givenMatchingReturnTypeHasReturnTypeShouldReturnTrue() {
        Type returnType = String.class;

        Boolean result = sut.hasReturnType(returnType);
        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullHasParameterTypesShouldThrowException() {
        Type[] parameterTypes = null;

        sut.hasParameterTypes(parameterTypes);
    }

    @Test
    public void givenNoParamterTypesHasParamterTypesShouldReturnFalse() {
        Type[] parameterTypes = new Type[]{};

        Boolean result = sut.hasParameterTypes(parameterTypes);

        assertThat(result).isFalse();
    }

    @Test
    public void givenInvalidParamterTypeHasParamterTypesShouldReturnFalse() {
        Type[] parameterTypes = new Type[]{Long.class};

        Boolean result = sut.hasParameterTypes(parameterTypes);

        assertThat(result).isFalse();
    }

    @Test
    public void givenValidParamterTypeHasParamterTypesShouldReturnTrue() {
        Type[] parameterTypes = new Type[]{String.class};

        Boolean result = sut.hasParameterTypes(parameterTypes);

        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceInvokeShouldThrowException() {
        Object instance = null;
        Object[] methodArgs = new Object[]{"Marvin"};

        sut.invoke(instance, methodArgs);
    }

    @Test(expected = TestifyException.class)
    public void givenNoArgumentsInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();

        sut.invoke(instance);
    }

    @Test(expected = TestifyException.class)
    public void givenWrongArgumentTypeInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();
        Object[] methodArgs = new Object[]{24};

        sut.invoke(instance, methodArgs);
    }

    @Test
    public void givenNameInvokeShouldReturn() {
        Object instance = new PrimaryTestService();
        Object[] methodArgs = new Object[]{"Marvin"};

        Optional<String> result = sut.invoke(instance, methodArgs);

        assertThat(result).contains("Hello Marvin!");
    }
}
