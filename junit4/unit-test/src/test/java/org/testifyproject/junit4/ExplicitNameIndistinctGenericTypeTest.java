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

import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Fake;
import org.testifyproject.junit4.fixture.ExplicitNameIndistinctGenericType;
import org.testifyproject.junit4.fixture.collaborator.Hello;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class ExplicitNameIndistinctGenericTypeTest {

    @Cut
    ExplicitNameIndistinctGenericType cut;

    @Fake
    Supplier<Hello> english;

    @Fake("spanish")
    Supplier<Hello> esp;

    @Before
    public void verifyInjections() {
        assertThat(cut).isNotNull();
        assertThat(english).isNotNull();
        assertThat(esp).isNotNull();
        assertThat(cut.getEnglish()).isSameAs(english);
        assertThat(cut.getSpanish()).isSameAs(esp);
    }

    @Test
    public void givenNothingClassToExecuteShouldReturnHello() {
        String englishGreeting = "Hello";
        String spanishGreeting = "Hola";
        Hello englishInstance = mock(Hello.class);
        Hello spanishInstance = mock(Hello.class);

        given(english.get()).willReturn(englishInstance);
        given(esp.get()).willReturn(spanishInstance);
        given(englishInstance.greet()).willReturn(englishGreeting);
        given(spanishInstance.greet()).willReturn(spanishGreeting);

        String result = cut.execute();

        assertThat(result).isEqualTo(englishGreeting + " " + spanishGreeting);
        verify(english).get();
        verify(esp).get();
        verify(englishInstance).greet();
        verify(spanishInstance).greet();
        verifyNoMoreInteractions(english, esp);
    }

}
