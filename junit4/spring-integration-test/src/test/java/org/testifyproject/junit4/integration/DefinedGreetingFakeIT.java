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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.annotation.Fake;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Name;
import org.testifyproject.annotation.Sut;
import org.testifyproject.junit4.fixture.common.DefinedGreeting;
import org.testifyproject.junit4.fixture.common.GreeterConfig;
import org.testifyproject.junit4.fixture.common.impl.Hello;

/**
 *
 * @author saden
 */
@Module(GreeterConfig.class)
@RunWith(SpringIntegrationTest.class)
public class DefinedGreetingFakeIT {

    @Sut
    DefinedGreeting sut;

    @Name("greeting1")
    @Fake
    Hello hello1;

    @Name("greeting2")
    @Fake
    Hello hello2;

    @Test
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(hello1).isNotNull().isSameAs(sut.getGreeting1());
        assertThat(hello2).isNotNull().isSameAs(sut.getGreeting2());

        assertThat(Mockito.mockingDetails(hello1).isMock()).isTrue();
        assertThat(Mockito.mockingDetails(hello2).isMock()).isTrue();
    }

}
