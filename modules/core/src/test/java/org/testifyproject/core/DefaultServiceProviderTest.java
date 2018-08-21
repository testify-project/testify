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
package org.testifyproject.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.util.AnalyzerUtil;
import org.testifyproject.fixture.analyzer.AnalyzedTestClass;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultServiceProviderTest {

    DefaultServiceProvider sut;

    @Before
    public void init() {
        sut = new DefaultServiceProvider();
    }

    @Test
    public void callToCreateShouldReturnServiceContext() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Collection<MethodDescriptor> methodDescriptors = ImmutableList.of();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProviders()).willReturn(methodDescriptors);

        Map<ServiceKey, Object> result = sut.create(testContext);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToConfigureShouldReturnServiceInstance() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Collection<MethodDescriptor> methodDescriptors = ImmutableList.of();
        Map<ServiceKey, Object> serviceContext = mock(Map.class);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProviders()).willReturn(methodDescriptors);

        ServiceInstance result = sut.configure(testContext, serviceContext);

        assertThat(result).isNotNull();
    }

    @Test
    public void callToPostConfigureReturnServiceInstance() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Collection<MethodDescriptor> methodDescriptors = ImmutableList.of();
        Map<ServiceKey, Object> serviceContext = mock(Map.class);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getCollaboratorProviders()).willReturn(methodDescriptors);

        ServiceInstance result = sut.configure(testContext, serviceContext);

        assertThat(result).isNotNull();
    }

    @Test
    public void callConfigureShouldReturnServiceInstanceWithCollaboratorProviderServices()
            throws NoSuchMethodException {
        Class<AnalyzedTestClass> testClass = AnalyzedTestClass.class;
        Method testMethod = testClass.getDeclaredMethod("verifyTest");
        TestContext testContext = AnalyzerUtil.INSTANCE.analyzeAndCreate(testClass, testMethod);
        Map<ServiceKey, Object> serviceContext = new HashMap<>();

        ServiceInstance result = sut.configure(testContext, serviceContext);

        assertThat(result).isNotNull();
        assertThat(serviceContext).isNotEmpty();

    }

}
