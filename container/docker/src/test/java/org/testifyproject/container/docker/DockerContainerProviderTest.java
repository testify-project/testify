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
package org.testifyproject.container.docker;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.ContainerInstance;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.annotation.RequiresContainer;
import static org.testifyproject.container.docker.DockerContainerProvider.DEFAULT_DAEMON_URI;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.github.dockerjava.core.DockerClientConfig;
import static org.testifyproject.github.dockerjava.core.DockerClientConfig.createDefaultConfigBuilder;

/**
 *
 * @author saden
 */
public class DockerContainerProviderTest {

    DockerContainerProvider cut;

    @Before
    public void init() {
        cut = new DockerContainerProvider();
    }

    @After
    public void destroy() {
        cut.stop();
    }

    @Test
    public void callToConfigureShouldReturnBuilder() {
        TestContext testContext = mock(TestContext.class);
        DockerClientConfig.DockerClientConfigBuilder result = cut.configure(testContext);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenValidParametersCallToStartAndStopContainerShouldSucceed() {
        StartStrategy resourceStartStrategy = StartStrategy.Eager;
        Object testInstance = new Object();
        MethodDescriptor methodDescriptor = mock(MethodDescriptor.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        TestReifier testReifier = mock(TestReifier.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Map<String, Object> properties = mock(Map.class);
        Map<String, String> dependencies = mock(Map.class);

        TestContext testContext = new DefaultTestContextBuilder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .methodDescriptor(methodDescriptor)
                .testReifier(testReifier)
                .mockProvider(mockProvider)
                .properties(properties)
                .dependencies(dependencies)
                .build();

        RequiresContainer delegate = ReflectionUtil.INSTANCE.newInstance(RequiresContainer.class);
        RequiresContainer requiresContainer = mock(RequiresContainer.class, delegatesTo(delegate));
        given(requiresContainer.value()).willReturn("postgres");
        given(requiresContainer.version()).willReturn("9.4");
        given(testContext.getTestName()).willReturn("TestClass");
        given(testContext.getMethodName()).willReturn("testMethod");

        DockerClientConfig.DockerClientConfigBuilder builder = createDefaultConfigBuilder()
                .withDockerHost(DEFAULT_DAEMON_URI);
        ContainerInstance result = cut.start(testContext, requiresContainer, builder);
        assertThat(result).isNotNull();
    }

}
