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
package org.testify.trait;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testify.fixture.PrimaryTestService;

/**
 *
 * @author saden
 */
public class MemberTraitTest {

    MemberTrait cut;

    @Before
    public void init() throws NoSuchMethodException {
        cut = mock(MemberTrait.class, Answers.CALLS_REAL_METHODS);
        String methodName = "sayHello";
        Class<?>[] methodArgs = new Class[]{String.class};

        Method method = PrimaryTestService.class.getDeclaredMethod(methodName, methodArgs);

        given(cut.getMember()).willReturn(method);
    }

    @Test
    public void callToGetMemberShouldReturn() {
        Member result = cut.getMember();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetNameShouldReturnMemberName() {
        String result = cut.getName();

        assertThat(result).isEqualTo("sayHello");
    }

    @Test
    public void callToGetDeclaringClassShouldReturn() {
        Class result = cut.getDeclaringClass();

        assertThat(result).isEqualTo(PrimaryTestService.class);
    }

    @Test
    public void callToGetDeclaringClassNameShouldReturn() {
        String result = cut.getDeclaringClassName();

        assertThat(result).isEqualTo(PrimaryTestService.class.getSimpleName());
    }

    @Test
    public void callToGetDeclaringClassLoader() {
        ClassLoader result = cut.getDeclaringClassLoader();

        assertThat(result).isEqualTo(PrimaryTestService.class.getClassLoader());
    }
}
