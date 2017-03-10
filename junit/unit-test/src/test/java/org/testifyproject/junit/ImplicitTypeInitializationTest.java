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
package org.testifyproject.junit;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Fake;
import org.testifyproject.junit.fixture.ImplicitType;
import org.testifyproject.junit.fixture.collaborator.Hello;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class ImplicitTypeInitializationTest {

    Hello delegate = new Hello();

    @Cut
    ImplicitType cut;

    @Fake
    Hello hello = delegate;

    @Before
    public void verifyInjections() {
        System.out.println("Executing test:" + this);
        assertThat(cut).isNotNull();
        assertThat(hello).isNotNull();
        assertThat(Mockito.mockingDetails(cut.getHello()).isMock()).isTrue();
        assertThat(Mockito.mockingDetails(hello).isMock()).isTrue();
    }

    @Test
    public void givenNothingClassToExecuteShouldReturnHello() {
        System.out.println("Executing test:" + this);
        String helloGreeting = "Hello";
        given(hello.greet()).willReturn(helloGreeting);

        String result = cut.execute();

        assertThat(result).isEqualTo(helloGreeting);
        assertThat(delegate.isCalled()).isTrue();

        verify(hello).greet();
        verifyNoMoreInteractions(hello);
    }

}
