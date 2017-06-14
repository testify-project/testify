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
package org.testifyproject.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.SutDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.TestConfigurer;

/**
 *
 * @author saden
 */
public class DefaultTestContextTest {

    TestContext sut;

    StartStrategy resourceStartStrategy;
    Object testInstance;
    TestDescriptor testDescriptor;
    MethodDescriptor methodDescriptor;
    TestRunner testRunner;
    TestConfigurer testConfigurer;
    MockProvider mockProvider;
    Map<String, Object> properties;
    Map<String, String> dependencies;

    @Before
    public void init() {
        resourceStartStrategy = StartStrategy.EAGER;
        testInstance = new Object();
        testDescriptor = mock(TestDescriptor.class);
        methodDescriptor = mock(MethodDescriptor.class);
        testConfigurer = mock(TestConfigurer.class);
        testRunner = mock(TestRunner.class);
        mockProvider = mock(MockProvider.class);
        properties = mock(Map.class, delegatesTo(new HashMap<>()));
        dependencies = mock(Map.class, delegatesTo(new HashMap<>()));

        sut = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(methodDescriptor)
                .testRunner(testRunner)
                .testConfigurer(testConfigurer)
                .mockProvider(mockProvider)
                .properties(properties)
                .dependencies(dependencies)
                .build();
    }

    @Test
    public void callToGetPropertiesShouldReturnProperties() {
        Map<String, Object> result = sut.getProperties();

        assertThat(result).isEqualTo(properties);
    }

    @Test
    public void callToGetTestNameShouldReturnClassName() {
        sut.getTestName();

        verify(testDescriptor).getTestClassName();
    }

    @Test
    public void callToGetMethodNameShouldReturnMethodName() {
        sut.getMethodName();

        verify(methodDescriptor).getName();
    }

    @Test
    public void callToGetNameShouldReturnName() {
        sut.getName();

        verify(methodDescriptor).getName();
        verify(testDescriptor).getTestClassName();
    }

    @Test
    public void callToGetTestClassShouldReturnReturnTestClass() {
        sut.getTestClass();

        verify(testDescriptor).getTestClass();
    }

    @Test
    public void callToGetResourceStartStrategyShouldReturn() {
        StartStrategy result = sut.getResourceStartStrategy();

        assertThat(result).isEqualTo(resourceStartStrategy);
    }

    @Test
    public void callToGetTestInstanceShouldReturn() {
        Object result = sut.getTestInstance();

        assertThat(result).isEqualTo(testInstance);
    }

    @Test
    public void callToGetTestDescriptorShouldReturn() {
        TestDescriptor result = sut.getTestDescriptor();

        assertThat(result).isEqualTo(testDescriptor);
    }

    @Test
    public void callToGetTestRunnerShouldReturn() {
        TestRunner result = sut.getTestRunner();

        assertThat(result).isEqualTo(testRunner);
    }

    @Test
    public void callToGetFinalReifierShouldReturn() {
        TestConfigurer result = sut.getTestConfigurer();

        assertThat(result).isEqualTo(testConfigurer);
    }

    @Test
    public void callToGetMockProviderShouldReturn() {
        MockProvider result = sut.getMockProvider();

        assertThat(result).isEqualTo(mockProvider);
    }

    @Test
    public void callToGetDependenciesShouldReturn() {
        Map<String, String> result = sut.getDependencies();

        assertThat(result).isEqualTo(dependencies);
    }

    @Test
    public void callToGetSutDescriptorShouldReturn() {
        Optional<SutDescriptor> result = sut.getSutDescriptor();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetServiceInstanceShouldReturn() {
        Optional<ServiceInstance> result = sut.getServiceInstance();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetSutInstanceShouldReturn() {
        Optional<Object> result = sut.getSutInstance();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        TestContext instance = null;

        assertThat(sut).isNotEqualTo(instance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(sut).isNotEqualTo(differentType);
        assertThat(sut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        TestContext uneuqual = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(null)
                .testConfigurer(testConfigurer)
                .properties(properties)
                .dependencies(dependencies)
                .build();

        assertThat(sut).isNotEqualTo(uneuqual);
        assertThat(sut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(sut).isEqualTo(sut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        TestContext equal = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(methodDescriptor)
                .testRunner(testRunner)
                .testConfigurer(testConfigurer)
                .mockProvider(mockProvider)
                .properties(properties)
                .dependencies(dependencies)
                .build();

        assertThat(sut).isEqualTo(equal);
        assertThat(sut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = sut.toString();

        assertThat(result).contains("DefaultTestContext", "resourceStartStrategy", "testDescriptor", "methodDescriptor");
    }
}
