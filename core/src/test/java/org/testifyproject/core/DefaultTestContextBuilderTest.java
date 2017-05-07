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

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.guava.common.collect.ImmutableMap;

/**
 *
 * @author saden
 */
public class DefaultTestContextBuilderTest {

    DefaultTestContextBuilder sut;

    @Before
    public void init() {
        sut = DefaultTestContextBuilder.builder();
    }

    @Test
    public void givenResourceStartStrategyBuildShouldReturnTestContext() {
        StartStrategy value = StartStrategy.EAGER;
        TestContext result = sut.resourceStartStrategy(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getResourceStartStrategy()).isEqualTo(value);
    }

    @Test
    public void givenTestInstanceBuildShouldReturnTestContext() {
        Object value = new Object();
        TestContext result = sut.testInstance(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getTestInstance()).isEqualTo(value);
    }

    @Test
    public void givenTestDescriptorBuildShouldReturnTestContext() {
        TestDescriptor value = mock(TestDescriptor.class);
        TestContext result = sut.testDescriptor(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getTestDescriptor()).isEqualTo(value);
    }

    @Test
    public void givenMethodDescriptorBuildShouldReturnTestContext() {
        MethodDescriptor value = mock(MethodDescriptor.class);
        TestContext result = sut.testMethodDescriptor(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getTestMethodDescriptor()).isEqualTo(value);
    }

    @Test
    public void givenTestRunnerBuildShouldReturnTestContext() {
        TestRunner value = mock(TestRunner.class);
        TestContext result = sut.testRunner(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getTestRunner()).isEqualTo(value);
    }

    @Test
    public void givenTestConfigurerBuildShouldReturnTestContext() {
        TestConfigurer value = mock(TestConfigurer.class);
        TestContext result = sut.testConfigurer(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getTestConfigurer()).isEqualTo(value);
    }

    @Test
    public void givenMockProviderBuildShouldReturnTestContext() {
        MockProvider value = mock(MockProvider.class);
        TestContext result = sut.mockProvider(value).build();

        assertThat(result).isNotNull();
        assertThat(result.getMockProvider()).isEqualTo(value);
    }

    @Test
    public void givenPropertiesBuildShouldReturnTestContext() {
        String key = "key";
        String value = "value";
        Map<String, Object> map = ImmutableMap.of(key, value);
        TestContext result = sut.properties(map).build();

        assertThat(result).isNotNull();
        assertThat(result.getProperties()).containsEntry(key, value);
    }

    @Test
    public void givenDependenciesBuildShouldReturnTestContext() {
        String key = "key";
        String value = "value";
        Map<String, String> map = ImmutableMap.of(key, value);
        TestContext result = sut.dependencies(map).build();

        assertThat(result).isNotNull();
        assertThat(result.getDependencies()).containsEntry(key, value);
    }
}
