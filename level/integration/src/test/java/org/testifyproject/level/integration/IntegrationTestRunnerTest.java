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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ResourceController;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.InitialReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.Verifier;
import org.testifyproject.extension.annotation.IntegrationCategory;
import org.testifyproject.extension.annotation.Strict;
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

        sut = spy(new IntegrationTestRunner(serviceLocatorUtil));
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

        CollaboratorReifier collaboratorReifier = mock(CollaboratorReifier.class);
        List<CollaboratorReifier> collaboratorReifiers = ImmutableList.of(
                collaboratorReifier);

        PreVerifier configurationVerifier = mock(PreVerifier.class);
        List<PreVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);

        ServiceProvider serviceProvider = mock(ServiceProvider.class);

        Object serviceContext = new Object();
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        ResourceController resourceController = mock(ResourceController.class);

        Set<Class<? extends Annotation>> nameQualifiers = ImmutableSet.of();
        Set<Class<? extends Annotation>> customQualifiers = ImmutableSet.of();
        Class sutType = Object.class;
        Annotation[] sutQualifiers = {};
        Object sutInstance = new Object();
        CollaboratorProvider collaboratorProvider = mock(CollaboratorProvider.class);
        Optional<CollaboratorProvider> foundCollaboratorProvider = Optional.of(
                collaboratorProvider);

        InitialReifier collaboratorsReifier = mock(InitialReifier.class);
        List<InitialReifier> collaboratorsReifiers = ImmutableList
                .of(collaboratorsReifier);

        FinalReifier testReifier = mock(FinalReifier.class);
        List<FinalReifier> testReifiers = ImmutableList.of(testReifier);

        Verifier wiringVerifier = mock(Verifier.class);
        List<Verifier> wiringVerifiers = ImmutableList.of(wiringVerifier);
        List<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(serviceLocatorUtil.getOne(ResourceController.class)).willReturn(
                resourceController);
        given(serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class,
                IntegrationCategory.class))
                .willReturn(collaboratorReifiers);
        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(serviceLocatorUtil.findAllWithFilter(
                PreVerifier.class,
                guidelines,
                IntegrationCategory.class)
        ).willReturn(configurationVerifiers);

        given(serviceLocatorUtil.getFromHintWithFilter(
                eq(testContext),
                eq(ServiceProvider.class),
                any())
        ).willReturn(serviceProvider);

        given(serviceProvider.create(testContext)).willReturn(serviceContext);
        given(serviceProvider.configure(testContext, serviceContext))
                .willReturn(serviceInstance);

        given(serviceInstance.getNameQualifers()).willReturn(nameQualifiers);
        given(serviceInstance.getCustomQualifiers()).willReturn(customQualifiers);
        given(sutDescriptor.getType()).willReturn(sutType);
        given(sutDescriptor.getMetaAnnotations(nameQualifiers, customQualifiers))
                .willReturn(sutQualifiers);
        given(serviceInstance.getService(sutType, sutQualifiers)).willReturn(sutInstance);
        given(testDescriptor.getCollaboratorProvider())
                .willReturn(foundCollaboratorProvider);
        given(serviceLocatorUtil.findAllWithFilter(InitialReifier.class,
                IntegrationCategory.class))
                .willReturn(collaboratorsReifiers);
        given(serviceLocatorUtil.findAllWithFilter(FinalReifier.class,
                IntegrationCategory.class))
                .willReturn(testReifiers);
        given(serviceLocatorUtil.findAllWithFilter(Verifier.class, guidelines,
                IntegrationCategory.class))
                .willReturn(wiringVerifiers);

        sut.start(testContext);

        verify(testContext).getTestInstance();
        verify(testContext).getTestConfigurer();
        verify(testContext).getSutDescriptor();
        verify(testContext).getTestDescriptor();
        verify(serviceLocatorUtil).getOne(ResourceController.class);
        verify(serviceLocatorUtil).findAllWithFilter(CollaboratorReifier.class,
                IntegrationCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(
                PreVerifier.class,
                guidelines,
                IntegrationCategory.class);
        verify(serviceLocatorUtil).getFromHintWithFilter(
                eq(testContext),
                eq(ServiceProvider.class),
                any());
        verify(serviceProvider).create(testContext);
        verify(testConfigurer).configure(testContext, serviceContext);
        verify(resourceController).start(testContext);
        verify(serviceInstance).getNameQualifers();
        verify(serviceInstance).getCustomQualifiers();
        verify(sutDescriptor).getType();
        verify(sutDescriptor).getMetaAnnotations(nameQualifiers, customQualifiers);
        verify(serviceInstance).getService(sutType, sutQualifiers);
        verify(sutDescriptor).setValue(testInstance, sutInstance);
        verify(testDescriptor).getCollaboratorProvider();
        verify(serviceLocatorUtil).findAllWithFilter(InitialReifier.class,
                IntegrationCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(FinalReifier.class,
                IntegrationCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(Verifier.class, guidelines,
                IntegrationCategory.class);
        verify(testContext, times(2)).verify();
    }

    @Test
    public void callToStopShouldStopTest() {
        TestContext testContext = mock(TestContext.class);
        ResourceController resourceController = sut.resourceController = mock(
                ResourceController.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.of(serviceInstance);

        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);

        PostVerifier postVerifier = mock(PostVerifier.class);
        List<PostVerifier> postVerifiers = ImmutableList.of(postVerifier);
        List<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(serviceLocatorUtil.findAllWithFilter(PostVerifier.class, guidelines,
                IntegrationCategory.class))
                .willReturn(postVerifiers);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(
                foundServiceInstance);

        sut.stop(testContext);

        verify(postVerifier).verify(testContext);
        verify(testContext).verify();
        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(fieldDescriptor).destroy(testInstance);
        verify(sutDescriptor).destroy(testInstance);
        verify(resourceController).stop(testContext);
        verify(serviceInstance).destroy();
        verify(resourceController).stop(testContext);

    }

}
