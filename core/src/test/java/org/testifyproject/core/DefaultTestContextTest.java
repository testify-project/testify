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
import org.testifyproject.CutDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.TestRunner;

/**
 *
 * @author saden
 */
public class DefaultTestContextTest {

    TestContext cut;

    StartStrategy resourceStartStrategy;
    Object testInstance;
    TestDescriptor testDescriptor;
    MethodDescriptor methodDescriptor;
    TestRunner testRunner;
    TestReifier testReifier;
    MockProvider mockProvider;
    Map<String, Object> properties;
    Map<String, String> dependencies;

    @Before
    public void init() {
        resourceStartStrategy = StartStrategy.EAGER;
        testInstance = new Object();
        testDescriptor = mock(TestDescriptor.class);
        methodDescriptor = mock(MethodDescriptor.class);
        testReifier = mock(TestReifier.class);
        testRunner = mock(TestRunner.class);
        mockProvider = mock(MockProvider.class);
        properties = mock(Map.class, delegatesTo(new HashMap<>()));
        dependencies = mock(Map.class, delegatesTo(new HashMap<>()));

        cut = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .methodDescriptor(methodDescriptor)
                .testRunner(testRunner)
                .testReifier(testReifier)
                .mockProvider(mockProvider)
                .properties(properties)
                .dependencies(dependencies)
                .build();
    }

    @Test
    public void callToGetPropertiesShouldReturnProperties() {
        Map<String, Object> result = cut.getProperties();

        assertThat(result).isEqualTo(properties);
    }

    @Test
    public void callToGetTestNameShouldReturnClassName() {
        cut.getTestName();

        verify(testDescriptor).getTestClassName();
    }

    @Test
    public void callToGetMethodNameShouldReturnMethodName() {
        cut.getMethodName();

        verify(methodDescriptor).getName();
    }

    @Test
    public void callToGetNameShouldReturnName() {
        cut.getName();

        verify(methodDescriptor).getName();
        verify(testDescriptor).getTestClassName();
    }

    @Test
    public void callToGetTestClassShouldReturnReturnTestClass() {
        cut.getTestClass();

        verify(testDescriptor).getTestClass();
    }

    @Test
    public void callToGetResourceStartStrategyShouldReturn() {
        StartStrategy result = cut.getResourceStartStrategy();

        assertThat(result).isEqualTo(resourceStartStrategy);
    }

    @Test
    public void callToGetTestInstanceShouldReturn() {
        Object result = cut.getTestInstance();

        assertThat(result).isEqualTo(testInstance);
    }

    @Test
    public void callToGetTestDescriptorShouldReturn() {
        TestDescriptor result = cut.getTestDescriptor();

        assertThat(result).isEqualTo(testDescriptor);
    }

    @Test
    public void callToGetTestRunnerShouldReturn() {
        TestRunner result = cut.getTestRunner();

        assertThat(result).isEqualTo(testRunner);
    }

    @Test
    public void callToGetTestReifierShouldReturn() {
        TestReifier result = cut.getTestReifier();

        assertThat(result).isEqualTo(testReifier);
    }

    @Test
    public void callToGetMockProviderShouldReturn() {
        MockProvider result = cut.getMockProvider();

        assertThat(result).isEqualTo(mockProvider);
    }

    @Test
    public void callToGetDependenciesShouldReturn() {
        Map<String, String> result = cut.getDependencies();

        assertThat(result).isEqualTo(dependencies);
    }

    @Test
    public void callToGetCutDescriptorShouldReturn() {
        Optional<CutDescriptor> result = cut.getCutDescriptor();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetServiceInstanceShouldReturn() {
        Optional<ServiceInstance> result = cut.getServiceInstance();

        assertThat(result).isEmpty();
    }

    @Test
    public void callToGetCutInstanceShouldReturn() {
        Optional<Object> result = cut.getCutInstance();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenNullInstancesShouldNotBeEqual() {
        TestContext instance = null;

        assertThat(cut).isNotEqualTo(instance);
    }

    @Test
    public void givenDifferentTypeInstancesShouldNotBeEqual() {
        String differentType = "instance";

        assertThat(cut).isNotEqualTo(differentType);
        assertThat(cut.hashCode()).isNotEqualTo(differentType.hashCode());
    }

    @Test
    public void givenUnequalInstancesShouldNotBeEqual() {
        TestContext uneuqual = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .methodDescriptor(null)
                .testReifier(testReifier)
                .properties(properties)
                .dependencies(dependencies)
                .build();

        assertThat(cut).isNotEqualTo(uneuqual);
        assertThat(cut.hashCode()).isNotEqualTo(uneuqual.hashCode());
    }

    @Test
    public void givenSameInstancesShouldBeEqual() {
        assertThat(cut).isEqualTo(cut);
    }

    @Test
    public void givenEqualInstancesShouldBeEqual() {
        TestContext equal = DefaultTestContextBuilder.builder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .methodDescriptor(methodDescriptor)
                .testRunner(testRunner)
                .testReifier(testReifier)
                .mockProvider(mockProvider)
                .properties(properties)
                .dependencies(dependencies)
                .build();

        assertThat(cut).isEqualTo(equal);
        assertThat(cut.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void callToToStringShouldReturnHumanReadableString() {
        String result = cut.toString();

        assertThat(result).contains("DefaultTestContext", "resourceStartStrategy", "testDescriptor", "methodDescriptor");
    }
}
