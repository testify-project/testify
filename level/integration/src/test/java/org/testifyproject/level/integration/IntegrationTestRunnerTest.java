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
import org.testifyproject.CutDescriptor;
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

    IntegrationTestRunner cut;
    ServiceLocatorUtil serviceLocatorUtil;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);

        cut = new IntegrationTestRunner(serviceLocatorUtil);
    }

    @Test
    public void callToDefaultConstructorShouldReturnNewInstance() {
        cut = new IntegrationTestRunner();

        assertThat(cut).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullStartTestContextShouldThrowException() {
        TestContext testContext = null;

        cut.start(testContext);
    }

    @Test
    public void givenTestContextStartShouldStartTest() {
        TestContext testContext = mock(TestContext.class);
        Object testInstance = new Object();
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);
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
        Class cutType = Object.class;
        Annotation[] cutQualifiers = {};
        Object cutInstance = new Object();
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
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
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
        given(cutDescriptor.getType()).willReturn(cutType);
        given(cutDescriptor.getMetaAnnotations(nameQualifiers, customQualifiers)).willReturn(cutQualifiers);
        given(serviceInstance.getService(cutType, cutQualifiers)).willReturn(cutInstance);
        given(testDescriptor.getCollaboratorProvider()).willReturn(foundCollaboratorProvider);
        given(serviceLocatorUtil.findAllWithFilter(CollaboratorsReifier.class, IntegrationTest.class))
                .willReturn(collaboratorsReifiers);
        given(serviceLocatorUtil.findAllWithFilter(TestReifier.class, IntegrationTest.class))
                .willReturn(testReifiers);
        given(serviceLocatorUtil.findAllWithFilter(WiringVerifier.class, IntegrationTest.class))
                .willReturn(wiringVerifiers);

        cut.start(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getTestConfigurer();
        verify(testContext).getCutDescriptor();
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
        verify(cutDescriptor).getType();
        verify(cutDescriptor).getMetaAnnotations(nameQualifiers, customQualifiers);
        verify(serviceInstance).getService(cutType, cutQualifiers);
        verify(cutDescriptor).setValue(testInstance, cutInstance);
        verify(testDescriptor).getCollaboratorProvider();
        verify(serviceLocatorUtil).findAllWithFilter(CollaboratorsReifier.class, IntegrationTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(TestReifier.class, IntegrationTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(WiringVerifier.class, IntegrationTest.class);
    }

    @Test
    public void callToStopShouldStopTest() {
        TestContext testContext = cut.testContext = mock(TestContext.class);
        TestResourcesProvider testResourcesProvider = cut.testResourcesProvider = mock(TestResourcesProvider.class);
        ServiceInstance serviceInstance = cut.serviceInstance = mock(ServiceInstance.class);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);

        cut.stop();

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(fieldDescriptor).destroy(testInstance);
        verify(cutDescriptor).destroy(testInstance);
        verify(testResourcesProvider).stop(testContext);
        verify(serviceInstance).destroy();

    }
}
