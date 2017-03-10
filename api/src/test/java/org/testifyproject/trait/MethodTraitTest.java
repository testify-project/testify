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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.fixture.PrimaryTestService;

/**
 *
 * @author saden
 */
public class MethodTraitTest {

    MethodTrait cut;

    @Before
    public void init() throws NoSuchMethodException {
        cut = mock(MethodTrait.class, Answers.CALLS_REAL_METHODS);
        String methodName = "sayHello";
        Class<?>[] methodArgs = new Class[]{String.class};

        Method method = PrimaryTestService.class.getDeclaredMethod(methodName, methodArgs);

        given(cut.getAnnotatedElement()).willReturn(method);
    }

    @Test
    public void callToGetAnnotatedElementShouldReturnField() {
        Method result = cut.getAnnotatedElement();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetParameterTypesShouldReturn() {
        List<Class> result = cut.getParameterTypes();

        assertThat(result).containsExactly(String.class);
    }

    @Test
    public void callToGetReturnTypeShouldReturn() {
        Class<?> result = cut.getReturnType();

        assertThat(result).isEqualTo(String.class);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullHasParameterTypesShouldThrowException() {
        Type[] parameterTypes = null;

        cut.hasParameterTypes(parameterTypes);
    }

    @Test
    public void givenNoParamterTypesHasParamterTypesShouldReturnFalse() {
        Type[] parameterTypes = new Type[]{};

        Boolean result = cut.hasParameterTypes(parameterTypes);

        assertThat(result).isFalse();
    }

    @Test
    public void givenInvalidParamterTypeHasParamterTypesShouldReturnFalse() {
        Type[] parameterTypes = new Type[]{Long.class};

        Boolean result = cut.hasParameterTypes(parameterTypes);

        assertThat(result).isFalse();
    }

    @Test
    public void givenValidParamterTypeHasParamterTypesShouldReturnTrue() {
        Type[] parameterTypes = new Type[]{String.class};

        Boolean result = cut.hasParameterTypes(parameterTypes);

        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceInvokeShouldThrowException() {
        Object instance = null;
        Object[] methodArgs = new Object[]{"Marvin"};

        cut.invoke(instance, methodArgs);
    }

    @Test(expected = IllegalStateException.class)
    public void givenNoArgumentsInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();

        cut.invoke(instance);
    }

    @Test(expected = IllegalStateException.class)
    public void givenWrongArgumentTypeInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();
        Object[] methodArgs = new Object[]{24};

        cut.invoke(instance, methodArgs);
    }

    @Test
    public void givenNameInvokeShouldReturn() {
        Object instance = new PrimaryTestService();
        Object[] methodArgs = new Object[]{"Marvin"};

        Optional<String> result = cut.invoke(instance, methodArgs);

        assertThat(result).contains("Hello Marvin!");
    }
}
