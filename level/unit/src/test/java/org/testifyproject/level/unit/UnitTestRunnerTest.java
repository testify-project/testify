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
package org.testifyproject.level.unit;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.extension.SutReifier;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.extension.annotation.UnitCategory;

/**
 *
 * @author saden
 */
public class UnitTestRunnerTest {

    UnitTestRunner sut;
    ServiceLocatorUtil serviceLocatorUtil;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);

        sut = new UnitTestRunner(serviceLocatorUtil);
    }

    @Test
    public void callToDefaultConstructorShouldReturnNewInstance() {
        sut = new UnitTestRunner();

        assertThat(sut).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullStartTestContextShouldThrowException() {
        TestContext testContext = null;

        sut.start(testContext);
    }

    @Test
    public void givenTestContextStartShouldStartTest() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = new Object();
        TestDescriptor testDescriptor = mock(TestDescriptor.class);

        PreVerifier configurationVerifier = mock(PreVerifier.class);
        List<PreVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);

        SutReifier sutReifier = mock(SutReifier.class);
        List<SutReifier> sutReifiers = ImmutableList.of(sutReifier);

        InitialReifier collaboratorsReifier = mock(InitialReifier.class);
        List<InitialReifier> collaboratorsReifiers = ImmutableList.of(collaboratorsReifier);

        FieldReifier fieldReifier = mock(FieldReifier.class);
        List<FieldReifier> fieldReifiers = ImmutableList.of(fieldReifier);

        FinalReifier testReifier = mock(FinalReifier.class);
        List<FinalReifier> testReifiers = ImmutableList.of(testReifier);

        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);

        PreiVerifier wiringVerifier = mock(PreiVerifier.class);
        List<PreiVerifier> wiringVerifiers = ImmutableList.of(wiringVerifier);

        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);

        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(serviceLocatorUtil.findAllWithFilter(PreVerifier.class, UnitCategory.class))
                .willReturn(configurationVerifiers);
        given(serviceLocatorUtil.findAllWithFilter(SutReifier.class, UnitCategory.class))
                .willReturn(sutReifiers);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(serviceLocatorUtil.findAllWithFilter(InitialReifier.class, UnitCategory.class))
                .willReturn(collaboratorsReifiers);
        given(serviceLocatorUtil.findAllWithFilter(FieldReifier.class, UnitCategory.class))
                .willReturn(fieldReifiers);
        given(serviceLocatorUtil.findAllWithFilter(FinalReifier.class, UnitCategory.class))
                .willReturn(testReifiers);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(serviceLocatorUtil.findAllWithFilter(PreiVerifier.class, UnitCategory.class))
                .willReturn(wiringVerifiers);

        sut.start(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getTestDescriptor();
        verify(serviceLocatorUtil).findAllWithFilter(PreVerifier.class, UnitCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(SutReifier.class, UnitCategory.class);
        verify(testDescriptor).getCollaboratorProvider();
        verify(serviceLocatorUtil).findAllWithFilter(InitialReifier.class, UnitCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(FieldReifier.class, UnitCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(FinalReifier.class, UnitCategory.class);
        verify(testDescriptor).getFieldDescriptors();
        verify(fieldDescriptor).init(testInstance);
        verify(testContext).getSutDescriptor();
        verify(sutDescriptor).init(testInstance);
        verify(serviceLocatorUtil).findAllWithFilter(PreiVerifier.class, UnitCategory.class);
    }

    @Test
    public void callToStopShouldStopTest() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();

        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        
        PostVerifier postVerifier = mock(PostVerifier.class);
        List<PostVerifier> postVerifiers = ImmutableList.of(postVerifier);

        given(serviceLocatorUtil.findAllWithFilter(PostVerifier.class, UnitCategory.class))
                .willReturn(postVerifiers);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);

        sut.stop(testContext);

        verify(postVerifier).verify(testContext);
        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(fieldDescriptor).destroy(testInstance);
        verify(sutDescriptor).destroy(testInstance);

    }
}
