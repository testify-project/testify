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

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.fixture.PrimaryTestService;

/**
 *
 * @author saden
 */
public class MemberTraitTest {

    MemberTrait sut;

    @Before
    public void init() throws NoSuchMethodException {
        sut = mock(MemberTrait.class, Answers.CALLS_REAL_METHODS);
        String methodName = "sayHello";
        Class<?>[] methodArgs = new Class[]{String.class};

        Method method = PrimaryTestService.class.getDeclaredMethod(methodName, methodArgs);

        given(sut.getMember()).willReturn(method);
    }

    @Test
    public void callToGetMemberShouldReturn() {
        Member result = sut.getMember();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetNameShouldReturnMemberName() {
        String result = sut.getName();

        assertThat(result).isEqualTo("sayHello");
    }

    @Test
    public void callToGetDeclaringClassShouldReturn() {
        Class result = sut.getDeclaringClass();

        assertThat(result).isEqualTo(PrimaryTestService.class);
    }

    @Test
    public void callToGetDeclaringClassNameShouldReturn() {
        String result = sut.getDeclaringClassName();

        assertThat(result).isEqualTo(PrimaryTestService.class.getSimpleName());
    }

    @Test
    public void callToGetDeclaringClassLoader() {
        ClassLoader result = sut.getDeclaringClassLoader();

        assertThat(result).isEqualTo(PrimaryTestService.class.getClassLoader());
    }
}
