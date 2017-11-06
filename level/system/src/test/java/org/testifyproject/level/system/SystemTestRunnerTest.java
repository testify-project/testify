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
package org.testifyproject.level.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;

/**
 *
 * @author saden
 */
public class SystemTestRunnerTest {

    SystemTestRunner sut;

    ServiceLocatorUtil serviceLocatorUtil;
    ReflectionUtil reflectionUtil;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        reflectionUtil = mock(ReflectionUtil.class);

        sut = spy(new SystemTestRunner(serviceLocatorUtil, reflectionUtil));
    }

    @Test
    public void callToDefaultConstructorShouldReturnNewInstance() {
        sut = new SystemTestRunner();

        assertThat(sut).isNotNull();
    }

    @Test
    public void givenNoApplicationStartShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.empty();
        Optional<Application> foundApplication = Optional.empty();

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);

        sut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testContext).getSutDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getApplication();

        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor);
    }

}
