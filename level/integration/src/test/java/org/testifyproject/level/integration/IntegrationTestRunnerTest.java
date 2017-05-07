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
package org.testifyproject.level.integration;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.SutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestResourcesProvider;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorsReifier;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.WiringVerifier;
import org.testifyproject.extension.annotation.IntegrationTest;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.collect.ImmutableSet;

/**
 *
 * @author saden
 */
public class IntegrationTestRunnerTest {

    IntegrationTestRunner sut;
    ServiceLocatorUtil serviceLocatorUtil;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);

        sut = new IntegrationTestRunner(serviceLocatorUtil);
    }

    @Test
    public void callToDefaultConstructorShouldReturnNewInstance() {
        sut = new IntegrationTestRunner();

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
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);

        FieldReifier fieldReifier = mock(FieldReifier.class);
        List<FieldReifier> fieldReifiers = ImmutableList.of(fieldReifier);

        ConfigurationVerifier configurationVerifier = mock(ConfigurationVerifier.class);
        List<ConfigurationVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);

        ServiceProvider serviceProvider = mock(ServiceProvider.class);
        Object serviceContext = new Object();
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        TestResourcesProvider testResourcesProvider = mock(TestResourcesProvider.class);
        Set<Class<? extends Annotation>> nameQualifiers = ImmutableSet.of();
        Set<Class<? extends Annotation>> customQualifiers = ImmutableSet.of();
        Class sutType = Object.class;
        Annotation[] sutQualifiers = {};
        Object sutInstance = new Object();
        MethodDescriptor collaboratorProvider = mock(MethodDescriptor.class);
        Optional<MethodDescriptor> foundCollaboratorProvider = Optional.of(collaboratorProvider);

        CollaboratorsReifier collaboratorsReifier = mock(CollaboratorsReifier.class);
        List<CollaboratorsReifier> collaboratorsReifiers = ImmutableList.of(collaboratorsReifier);

        TestReifier testReifier = mock(TestReifier.class);
        List<TestReifier> testReifiers = ImmutableList.of(testReifier);

        WiringVerifier wiringVerifier = mock(WiringVerifier.class);
        List<WiringVerifier> wiringVerifiers = ImmutableList.of(wiringVerifier);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(serviceLocatorUtil.findAllWithFilter(FieldReifier.class, IntegrationTest.class))
                .willReturn(fieldReifiers);
        given(serviceLocatorUtil.findAllWithFilter(ConfigurationVerifier.class, IntegrationTest.class))
                .willReturn(configurationVerifiers);
        given(serviceLocatorUtil.getOne(ServiceProvider.class)).willReturn(serviceProvider);
        given(serviceProvider.create(testContext)).willReturn(serviceContext);
        given(serviceProvider.configure(testContext, serviceContext)).willReturn(serviceInstance);
        given(serviceLocatorUtil.getOne(TestResourcesProvider.class)).willReturn(testResourcesProvider);
        given(serviceInstance.getNameQualifers()).willReturn(nameQualifiers);
        given(serviceInstance.getCustomQualifiers()).willReturn(customQualifiers);
        given(sutDescriptor.getType()).willReturn(sutType);
        given(sutDescriptor.getMetaAnnotations(nameQualifiers, customQualifiers)).willReturn(sutQualifiers);
        given(serviceInstance.getService(sutType, sutQualifiers)).willReturn(sutInstance);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(serviceLocatorUtil.findAllWithFilter(CollaboratorsReifier.class, IntegrationTest.class))
                .willReturn(collaboratorsReifiers);
        given(serviceLocatorUtil.findAllWithFilter(TestReifier.class, IntegrationTest.class))
                .willReturn(testReifiers);
        given(serviceLocatorUtil.findAllWithFilter(WiringVerifier.class, IntegrationTest.class))
                .willReturn(wiringVerifiers);

        sut.start(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getTestConfigurer();
        verify(testContext).getSutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(serviceLocatorUtil).findAllWithFilter(FieldReifier.class, IntegrationTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(ConfigurationVerifier.class, IntegrationTest.class);
        verify(serviceLocatorUtil).getOne(ServiceProvider.class);
        verify(serviceProvider).create(testContext);
        verify(serviceProvider).configure(testContext, serviceContext);
        verify(testContext).addProperty(SERVICE_INSTANCE, serviceInstance);
        verify(serviceInstance).addConstant(testContext, null, TestContext.class);
        verify(serviceProvider).postConfigure(testContext, serviceInstance);
        verify(testConfigurer).configure(testContext, serviceContext);
        verify(serviceLocatorUtil).getOne(TestResourcesProvider.class);
        verify(testResourcesProvider).start(testContext, serviceInstance);
        verify(serviceInstance).init();
        verify(serviceInstance).getNameQualifers();
        verify(serviceInstance).getCustomQualifiers();
        verify(sutDescriptor).getType();
        verify(sutDescriptor).getMetaAnnotations(nameQualifiers, customQualifiers);
        verify(serviceInstance).getService(sutType, sutQualifiers);
        verify(sutDescriptor).setValue(testInstance, sutInstance);
        verify(testDescriptor).getCollaboratorProvider();
        verify(serviceLocatorUtil).findAllWithFilter(CollaboratorsReifier.class, IntegrationTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(TestReifier.class, IntegrationTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(WiringVerifier.class, IntegrationTest.class);
    }

    @Test
    public void callToStopShouldStopTest() {
        TestContext testContext = mock(TestContext.class);
        TestResourcesProvider testResourcesProvider = sut.testResourcesProvider = mock(TestResourcesProvider.class);
        ServiceInstance serviceInstance = sut.serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);

        sut.stop(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(fieldDescriptor).destroy(testInstance);
        verify(sutDescriptor).destroy(testInstance);
        verify(testResourcesProvider).stop(testContext);
        verify(serviceInstance).destroy();

    }
}
