/*
 * Copyright 2016-2018 Testify Project.
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
package org.testifyproject.junit5.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit5.UnitTest;
import org.testifyproject.junit5.fixture.ImplicitType;
import org.testifyproject.junit5.fixture.common.Hello;

/**
 *
 * @author saden
 */
@UnitTest
public class VerifyInteractionTest {

    Hello delegate = new Hello();

    @Sut(verify = true)
    ImplicitType sut;

    @Fake
    Hello hello = delegate;

    @Before
    public void verifyInjections() {
        assertThat(sut).isNotNull();
        assertThat(hello).isNotNull();
        assertThat(Mockito.mockingDetails(sut.getHello()).isMock()).isTrue();
        assertThat(Mockito.mockingDetails(hello).isMock()).isTrue();
    }

    @Test
    public void givenNothingClassToExecuteShouldReturnHello() {
        String helloGreeting = "Hello";
        given(hello.greet()).willReturn(helloGreeting);

        String result = sut.execute();

        assertThat(result).isEqualTo(helloGreeting);
        assertThat(delegate.isCalled()).isTrue();

        verify(hello).greet();
    }

}
