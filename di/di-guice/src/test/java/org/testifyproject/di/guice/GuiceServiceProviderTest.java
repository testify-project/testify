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
package org.testifyproject.di.guice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;

import com.google.inject.Injector;

/**
 *
 * @author saden
 */
public class GuiceServiceProviderTest {

    GuiceServiceProvider sut;

    @Before
    public void init() {
        sut = new GuiceServiceProvider();
    }

    @Test
    public void givenTestContextCreateShouldReturnInjector() {
        TestContext testContext = mock(TestContext.class);

        Injector result = sut.create(testContext);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenTestContextAndInjectorConfigureShouldReturnInjector() {
        TestContext testContext = mock(TestContext.class);
        Injector injector = mock(Injector.class);

        ServiceInstance result = sut.configure(testContext, injector);

        assertThat(result).isNotNull();
        assertThat((Injector) result.getContext()).isEqualTo(injector);
    }

}
