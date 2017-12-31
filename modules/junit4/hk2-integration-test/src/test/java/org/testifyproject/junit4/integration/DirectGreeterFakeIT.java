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
import static org.mockito.Mockito.verify;
import static org.testifyproject.di.hk2.HK2Properties.DEFAULT_DESCRIPTOR;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit4.IntegrationTest;
import org.testifyproject.junit4.fixture.DirectGreeter;
import org.testifyproject.junit4.fixture.common.impl.Hello;

/**
 *
 * @author saden
 */
@Scan(DEFAULT_DESCRIPTOR)
@RunWith(IntegrationTest.class)
public class DirectGreeterFakeIT {

    @Sut
    DirectGreeter sut;

    @Fake
    Hello greeting;

    @Test
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(greeting).isNotNull().isSameAs(sut.getGreeting());
        assertThat(Mockito.mockingDetails(greeting).isMock()).isTrue();
    }

    @Test
    public void callToGreetShouldReturnPhrase() {
        String phrase = "Konnichiwa";

        given(greeting.phrase()).willReturn(phrase);

        String result = sut.greet();

        assertThat(result).isEqualTo(phrase);
        verify(greeting).phrase();
    }

}
