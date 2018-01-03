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
package org.testifyproject.di.hk2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class HK2ServiceProviderTest {

    HK2ServiceProvider sut;

    @Before
    public void init() {
        sut = new HK2ServiceProvider();
    }

    @Test
    public void givenTestContextCreateShouldReturnServiceLocator() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        String testName = "TestClass";

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getName()).willReturn(testName);
        given(testConfigurer.configure(eq(testContext), any(ServiceLocator.class)))
                .willAnswer(invocation -> invocation.getArgument(1));

        ServiceLocator result = sut.create(testContext);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenTestContextAndServiceLocatorConfigureShouldReturnServiceLocator() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        ClassLoader classLoader = HK2ServiceProvider.class.getClassLoader();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestClassLoader()).willReturn(classLoader);
        given(testDescriptor.getModules()).willReturn(ImmutableList.of());
        given(testDescriptor.getScans()).willReturn(ImmutableList.of());

        ServiceLocator serviceLocator = ServiceLocatorUtilities
                .createAndPopulateServiceLocator();

        ServiceInstance result = sut.configure(testContext, serviceLocator);

        assertThat(result).isNotNull();
        assertThat((Object) result.getContext()).isEqualTo(serviceLocator);
    }

}
