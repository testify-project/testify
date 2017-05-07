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
import org.testifyproject.MethodDescriptor;
import org.testifyproject.MockProvider;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.annotation.VirtualResource;
import static org.testifyproject.container.docker.DockerVirtualResourceProvider.DEFAULT_DAEMON_URI;
import org.testifyproject.core.DefaultTestContextBuilder;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.github.dockerjava.core.DockerClientConfig;
import static org.testifyproject.github.dockerjava.core.DockerClientConfig.createDefaultConfigBuilder;

/**
 *
 * @author saden
 */
public class DockerVirtualResourceProviderTest {

    DockerVirtualResourceProvider sut;

    @Before
    public void init() {
        sut = new DockerVirtualResourceProvider();
    }

    @After
    public void destroy() {
        sut.stop();
    }

    @Test
    public void callToConfigureShouldReturnBuilder() {
        TestContext testContext = mock(TestContext.class);
        DockerClientConfig.DockerClientConfigBuilder result = sut.configure(testContext);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenValidParametersCallToStartAndStopContainerShouldSucceed() {
        StartStrategy resourceStartStrategy = StartStrategy.EAGER;
        Object testInstance = new Object();
        MethodDescriptor methodDescriptor = mock(MethodDescriptor.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        MockProvider mockProvider = mock(MockProvider.class);
        Map<String, Object> properties = mock(Map.class);
        Map<String, String> dependencies = mock(Map.class);

        TestContext testContext = new DefaultTestContextBuilder()
                .resourceStartStrategy(resourceStartStrategy)
                .testInstance(testInstance)
                .testDescriptor(testDescriptor)
                .testMethodDescriptor(methodDescriptor)
                .testConfigurer(testConfigurer)
                .mockProvider(mockProvider)
                .properties(properties)
                .dependencies(dependencies)
                .build();

        VirtualResource delegate = ReflectionUtil.INSTANCE.newInstance(VirtualResource.class);
        VirtualResource virtualResource = mock(VirtualResource.class, delegatesTo(delegate));
        given(virtualResource.value()).willReturn("postgres");
        given(virtualResource.version()).willReturn("9.4");
        given(testContext.getTestName()).willReturn("TestClass");
        given(testContext.getMethodName()).willReturn("testMethod");

        DockerClientConfig.DockerClientConfigBuilder builder = createDefaultConfigBuilder()
                .withDockerHost(DEFAULT_DAEMON_URI);
        VirtualResourceInstance result = sut.start(testContext, virtualResource, builder);
        assertThat(result).isNotNull();
    }

}
