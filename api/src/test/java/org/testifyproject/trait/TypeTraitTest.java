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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.TestifyException;
import org.testifyproject.fixture.PrimaryTestService;
import org.testifyproject.fixture.TestContract;
import org.testifyproject.fixture.TestServiceSubtype;

/**
 *
 * @author saden
 */
public class TypeTraitTest {

    TypeTrait sut;

    @Before
    public void init() {
        sut = mock(TypeTrait.class, Answers.CALLS_REAL_METHODS);

        Class type = PrimaryTestService.class;
        given(sut.getType()).willReturn(type);
        given(sut.getGenericType()).willReturn(type);
    }

    @Test
    public void callToGetTypeShouldReturnTestService() {
        Class<?> result = sut.getType();

        assertThat(result).isEqualTo(PrimaryTestService.class);
    }

    @Test
    public void callToGetGenericTypeShouldReturnTestService() {
        Type result = sut.getGenericType();

        assertThat(result).isEqualTo(PrimaryTestService.class);
    }

    @Test
    public void callToGetTypeNameShouldReturnTestServiceSimpleName() {
        String result = sut.getTypeName();

        assertThat(result).isEqualTo(PrimaryTestService.class.getSimpleName());
    }

    @Test
    public void callToGetClassLoaderShouldReturnTestServiceClassLoader() {
        ClassLoader result = sut.getClassLoader();

        assertThat(result).isEqualTo(PrimaryTestService.class.getClassLoader());
    }

    @Test(expected = NullPointerException.class)
    public void givenNullIsSubtyoeOfShouldThrowException() {
        sut.isSubtypeOf(null);
    }

    @Test
    public void givenNonSubTypeIsSubtypeOfShouldReturnFalse() {
        Boolean result = sut.isSubtypeOf(String.class);

        assertThat(result).isFalse();
    }

    @Test
    public void givenContractTypeIsSubtypeOfShouldReturnTrue() {
        Boolean result = sut.isSubtypeOf(TestContract.class);

        assertThat(result).isTrue();
    }

    @Test
    public void givenServiceTypeIsSubtypeOfShouldReturnTrue() {
        Boolean result = sut.isSubtypeOf(PrimaryTestService.class);

        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullisSuperTypeOfShouldThrowException() {
        sut.isSupertypeOf(null);
    }

    @Test
    public void givenNonSubTypeIsSuperTypeOfShouldReturnFalse() {
        Boolean result = sut.isSupertypeOf(String.class);

        assertThat(result).isFalse();
    }

    @Test
    public void givenContractTypeIsSuperTypeOfShouldReturnFalse() {
        Boolean result = sut.isSupertypeOf(TestContract.class);

        assertThat(result).isFalse();
    }

    @Test
    public void givenServiceTypeIsSuperTypeOfShouldReturnTrue() {
        Boolean result = sut.isSupertypeOf(PrimaryTestService.class);

        assertThat(result).isTrue();
    }

    @Test
    public void givenTestServiceSubtypeIsSuperTypeOfShouldReturnTrue() {
        Boolean result = sut.isSupertypeOf(TestServiceSubtype.class);

        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceInvokeShouldThrowException() {
        Object instance = null;
        String methodName = "sayHello";
        Object[] methodArgs = new Object[]{"Marvin"};

        sut.invoke(instance, methodName, methodArgs);
    }

    @Test(expected = TestifyException.class)
    public void givenNonExistentMethodInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();
        String methodName = "saySalam";
        Object[] methodArgs = new Object[]{"Marvin"};

        sut.invoke(instance, methodName, methodArgs);
    }

    @Test(expected = TestifyException.class)
    public void givenNoArgumentsInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();
        String methodName = "sayHello";

        sut.invoke(instance, methodName);
    }

    @Test(expected = TestifyException.class)
    public void givenWrongArgumentTypesInvokeShouldThrowException() throws Exception {
        sut = mock(TypeTrait.class);
        Object instance = new PrimaryTestService();
        String methodName = "sayHello";
        Object[] methodArgs = new Object[]{42};
        Class[] methodArgsTypes = new Class[]{Integer.class};

        Method method = PrimaryTestService.class.getDeclaredMethod(methodName, String.class);

        given(sut.invoke(instance, methodName, methodArgs)).willCallRealMethod();
        given(sut.findMethod(PrimaryTestService.class, methodName, methodArgsTypes)).willReturn(method);

        sut.invoke(instance, methodName, methodArgs);
    }

    @Test
    public void givenNameInvokeShouldReturn() {
        Object instance = new PrimaryTestService();
        String methodName = "sayHello";
        Object[] methodArgs = new Object[]{"Marvin"};

        Optional<String> result = sut.invoke(instance, methodName, methodArgs);

        assertThat(result).contains("Hello Marvin!");
    }

    @Test
    public void givenNoArgumentGetMessageInvokeShouldReturn() {
        Object instance = new PrimaryTestService();
        String methodName = "getMessage";

        Optional<String> result = sut.invoke(instance, methodName);

        assertThat(result).contains("Hi!!!");
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTypeFindMethodShouldThrowException() {
        Class<?> type = null;
        String methodName = "getMessage";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        sut.findMethod(type, methodName, methodArgTypes);
    }

    @Test(expected = TestifyException.class)
    public void givenNonExistentMethodNameFindMethodShouldThrowException() {
        Class<?> type = PrimaryTestService.class;
        String methodName = "notAMethod";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        sut.findMethod(type, methodName, methodArgTypes);
    }

    @Test
    public void givenValidMethodNameFindMethodShouldReturnMethod() {
        Class<?> type = PrimaryTestService.class;
        String methodName = "getMessage";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        Method result = sut.findMethod(type, methodName, methodArgTypes);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenSubtypeFindMethodShouldReturnSupertypeMethod() {
        Class<?> type = TestServiceSubtype.class;
        String methodName = "getMessage";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        Method result = sut.findMethod(type, methodName, methodArgTypes);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenNonExistentMethodNameFindMethodShouldReturnEmptyOptional() {
        Class<?> type = PrimaryTestService.class;
        String methodName = "notAMethod";

        Optional<Method> result = sut.findMethod(methodName);
        assertThat(result).isEmpty();
    }

    @Test
    public void givenExistentMethodNameFindMethodShouldReturnOptionalWithMethod() {
        Class<?> type = PrimaryTestService.class;
        String methodName = "getMessage";

        Optional<Method> result = sut.findMethod(methodName);
        assertThat(result).isPresent();
    }
}
