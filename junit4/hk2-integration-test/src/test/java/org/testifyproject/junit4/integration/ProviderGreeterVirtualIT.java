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
package org.testifyproject.junit4.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testifyproject.di.hk2.HK2Properties.DEFAULT_DESCRIPTOR;

import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.Sut;
import org.testifyproject.annotation.Virtual;
import org.testifyproject.junit4.fixture.ProviderGreeter;
import org.testifyproject.junit4.fixture.common.impl.Hello;

/**
 *
 * @author saden
 */
@Scan(DEFAULT_DESCRIPTOR)
@RunWith(HK2IntegrationTest.class)
public class ProviderGreeterVirtualIT {

    @Sut
    ProviderGreeter sut;

    @Virtual
    Provider<Hello> greeting;

    @Test
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(greeting).isNotNull().isSameAs(sut.getGreeting());
        assertThat(Mockito.mockingDetails(greeting).isMock()).isTrue();
    }

    @Test
    public void callToGreetShouldReturnPhrase() {
        String phrase = "Konnichiwa";
        Hello hello = mock(Hello.class);

        given(greeting.get()).willReturn(hello);
        given(hello.phrase()).willReturn(phrase);

        String result = sut.greet();

        assertThat(result).isEqualTo(phrase);
        verify(greeting).get();
        verify(hello).phrase();
    }

}
