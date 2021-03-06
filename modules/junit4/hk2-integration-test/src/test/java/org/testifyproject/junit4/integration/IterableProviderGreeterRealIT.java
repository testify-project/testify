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
import static org.testifyproject.di.hk2.HK2Properties.DEFAULT_DESCRIPTOR;

import org.glassfish.hk2.api.IterableProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.Scan;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit4.IntegrationTest;
import org.testifyproject.junit4.fixture.IterableProviderGreeter;
import org.testifyproject.junit4.fixture.common.Greeting;

/**
 *
 * @author saden
 */
@Scan(DEFAULT_DESCRIPTOR)
@RunWith(IntegrationTest.class)
public class IterableProviderGreeterRealIT {

    @Sut
    IterableProviderGreeter sut;

    @Real
    IterableProvider<Greeting> greetings;

    @Test
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(greetings).hasSize(4).isSameAs(sut.getGreetings());
        assertThat(Mockito.mockingDetails(greetings).isMock()).isFalse();
    }

}
