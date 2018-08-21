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
package org.testifyproject.junit5.system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Hint;
import org.testifyproject.annotation.Name;
import org.testifyproject.annotation.Real;
import org.testifyproject.core.DefaultServiceProvider;
import org.testifyproject.junit5.SystemTest;
import org.testifyproject.junit5.fixture.GenericApplication;

/**
 * TODO.
 *
 * @author saden
 */
@Hint(serviceProvider = DefaultServiceProvider.class)
@Application(value = GenericApplication.class, start = "start", stop = "stop")
@SystemTest
public class ServiceResolverSystemTest {

    @Real
    TestContext sut;

    @Before
    public void verifyInjections() {
        assertThat(sut).isNotNull();
    }

    @Test
    public void verifyFakeParameter(@Name TestContext testContext) {
        assertThat(testContext).isNotNull();
        assertThat(Mockito.mockingDetails(testContext).isMock()).isFalse();
    }
}
