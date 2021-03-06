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

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Sut;
import org.testifyproject.annotation.Virtual;
import org.testifyproject.junit4.IntegrationTest;
import org.testifyproject.junit4.fixture.GreetingModule;
import org.testifyproject.junit4.fixture.common.Greeting;
import org.testifyproject.junit4.fixture.service.SetGreeter;

/**
 *
 * @author saden
 */
@Module(GreetingModule.class)
@RunWith(IntegrationTest.class)
public class SetGreeterRealDelegateIT {

    @Sut
    SetGreeter sut;

    @Virtual
    Set<Greeting> greetings;

    @Test
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(greetings).isNotNull().isSameAs(sut.getGreetings()).hasSize(3);
        assertThat(Mockito.mockingDetails(greetings).isMock()).isTrue();
    }

}
