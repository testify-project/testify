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
package org.testify.junit.integration;

import javax.inject.Provider;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testify.annotation.Cut;
import org.testify.annotation.Fake;
import org.testify.junit.fixture.ProviderGreeter;
import org.testify.junit.fixture.common.impl.Hello;

/**
 *
 * @author saden
 */
@RunWith(HK2IntegrationTest.class)
public class ProviderGreeterFakeIT {

    @Cut
    ProviderGreeter cut;

    @Fake
    Provider<Hello> greeting;

    @Test
    public void verifyInjection() {
        assertThat(cut).isNotNull();
        assertThat(greeting).isNotNull().isSameAs(cut.getGreeting());
        assertThat(Mockito.mockingDetails(greeting).isMock()).isTrue();
    }

    @Test
    public void callToGreetShouldReturnPhrase() {
        String phrase = "Konnichiwa";
        Hello hello = mock(Hello.class);

        given(greeting.get()).willReturn(hello);
        given(hello.phrase()).willReturn(phrase);

        String result = cut.greet();

        assertThat(result).isEqualTo(phrase);
        verify(greeting).get();
        verify(hello).phrase();
    }

}
