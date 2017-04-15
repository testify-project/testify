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

    TypeTrait cut;

    @Before
    public void init() {
        cut = mock(TypeTrait.class, Answers.CALLS_REAL_METHODS);

        Class type = PrimaryTestService.class;
        given(cut.getType()).willReturn(type);
        given(cut.getGenericType()).willReturn(type);
    }

    @Test
    public void callToGetTypeShouldReturnTestService() {
        Class<?> result = cut.getType();

        assertThat(result).isEqualTo(PrimaryTestService.class);
    }

    @Test
    public void callToGetGenericTypeShouldReturnTestService() {
        Type result = cut.getGenericType();

        assertThat(result).isEqualTo(PrimaryTestService.class);
    }

    @Test
    public void callToGetTypeNameShouldReturnTestServiceSimpleName() {
        String result = cut.getTypeName();

        assertThat(result).isEqualTo(PrimaryTestService.class.getSimpleName());
    }

    @Test
    public void callToGetClassLoaderShouldReturnTestServiceClassLoader() {
        ClassLoader result = cut.getClassLoader();

        assertThat(result).isEqualTo(PrimaryTestService.class.getClassLoader());
    }

    @Test(expected = NullPointerException.class)
    public void givenNullIsSubtyoeOfShouldThrowException() {
        cut.isSubtypeOf(null);
    }

    @Test
    public void givenNonSubTypeIsSubtypeOfShouldReturnFalse() {
        Boolean result = cut.isSubtypeOf(String.class);

        assertThat(result).isFalse();
    }

    @Test
    public void givenContractTypeIsSubtypeOfShouldReturnTrue() {
        Boolean result = cut.isSubtypeOf(TestContract.class);

        assertThat(result).isTrue();
    }

    @Test
    public void givenServiceTypeIsSubtypeOfShouldReturnTrue() {
        Boolean result = cut.isSubtypeOf(PrimaryTestService.class);

        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullisSuperTypeOfShouldThrowException() {
        cut.isSupertypeOf(null);
    }

    @Test
    public void givenNonSubTypeIsSuperTypeOfShouldReturnFalse() {
        Boolean result = cut.isSupertypeOf(String.class);

        assertThat(result).isFalse();
    }

    @Test
    public void givenContractTypeIsSuperTypeOfShouldReturnFalse() {
        Boolean result = cut.isSupertypeOf(TestContract.class);

        assertThat(result).isFalse();
    }

    @Test
    public void givenServiceTypeIsSuperTypeOfShouldReturnTrue() {
        Boolean result = cut.isSupertypeOf(PrimaryTestService.class);

        assertThat(result).isTrue();
    }

    @Test
    public void givenTestServiceSubtypeIsSuperTypeOfShouldReturnTrue() {
        Boolean result = cut.isSupertypeOf(TestServiceSubtype.class);

        assertThat(result).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullInstanceInvokeShouldThrowException() {
        Object instance = null;
        String methodName = "sayHello";
        Object[] methodArgs = new Object[]{"Marvin"};

        cut.invoke(instance, methodName, methodArgs);
    }

    @Test(expected = TestifyException.class)
    public void givenNonExistentMethodInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();
        String methodName = "saySalam";
        Object[] methodArgs = new Object[]{"Marvin"};

        cut.invoke(instance, methodName, methodArgs);
    }

    @Test(expected = TestifyException.class)
    public void givenNoArgumentsInvokeShouldThrowException() {
        Object instance = new PrimaryTestService();
        String methodName = "sayHello";

        cut.invoke(instance, methodName);
    }

    @Test(expected = TestifyException.class)
    public void givenWrongArgumentTypesInvokeShouldThrowException() throws Exception {
        cut = mock(TypeTrait.class);
        Object instance = new PrimaryTestService();
        String methodName = "sayHello";
        Object[] methodArgs = new Object[]{42};
        Class[] methodArgsTypes = new Class[]{Integer.class};

        Method method = PrimaryTestService.class.getDeclaredMethod(methodName, String.class);

        given(cut.invoke(instance, methodName, methodArgs)).willCallRealMethod();
        given(cut.findMethod(PrimaryTestService.class, methodName, methodArgsTypes)).willReturn(method);

        cut.invoke(instance, methodName, methodArgs);
    }

    @Test
    public void givenNameInvokeShouldReturn() {
        Object instance = new PrimaryTestService();
        String methodName = "sayHello";
        Object[] methodArgs = new Object[]{"Marvin"};

        Optional<String> result = cut.invoke(instance, methodName, methodArgs);

        assertThat(result).contains("Hello Marvin!");
    }

    @Test
    public void givenNoArgumentGetMessageInvokeShouldReturn() {
        Object instance = new PrimaryTestService();
        String methodName = "getMessage";

        Optional<String> result = cut.invoke(instance, methodName);

        assertThat(result).contains("Hi!!!");
    }

    @Test(expected = NullPointerException.class)
    public void givenNullTypeFindMethodShouldThrowException() {
        Class<?> type = null;
        String methodName = "getMessage";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        cut.findMethod(type, methodName, methodArgTypes);
    }

    @Test(expected = TestifyException.class)
    public void givenNonExistentMethodNameFindMethodShouldThrowException() {
        Class<?> type = PrimaryTestService.class;
        String methodName = "notAMethod";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        cut.findMethod(type, methodName, methodArgTypes);
    }

    @Test
    public void givenValidMethodNameFindMethodShouldReturnMethod() {
        Class<?> type = PrimaryTestService.class;
        String methodName = "getMessage";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        Method result = cut.findMethod(type, methodName, methodArgTypes);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenSubtypeFindMethodShouldReturnSupertypeMethod() {
        Class<?> type = TestServiceSubtype.class;
        String methodName = "getMessage";
        Class<?>[] methodArgTypes = new Class<?>[]{};

        Method result = cut.findMethod(type, methodName, methodArgTypes);

        assertThat(result).isNotNull();
    }

}
