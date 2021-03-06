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
package org.testifyproject.junit4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Name;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit4.fixture.ExplicitNameIndistinctType;
import org.testifyproject.junit4.fixture.common.Hello;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class ExplicitNameIndistinctTypeTest {

    @Sut
    ExplicitNameIndistinctType sut;

    @Fake
    Hello english;

    @Name("spanish")
    @Fake
    Hello spanishi;

    @Before
    public void verifyInjections() {
        assertThat(sut).isNotNull();
        assertThat(spanishi).isNotNull();
        assertThat(english).isNotNull();
        assertThat(sut.getEnglish()).isSameAs(english);
        assertThat(sut.getSpanish()).isSameAs(spanishi);
    }

    @Test
    public void givenNothingClassToExecuteShouldReturnHello() {
        String helloGreeting = "Hello";
        String worldGreeting = "Hola";
        given(english.greet()).willReturn(helloGreeting);
        given(spanishi.greet()).willReturn(worldGreeting);

        String result = sut.execute();

        assertThat(result).isEqualTo(helloGreeting + " " + worldGreeting);
        verify(english).greet();
        verify(spanishi).greet();
        verifyNoMoreInteractions(english, spanishi);
    }

}
