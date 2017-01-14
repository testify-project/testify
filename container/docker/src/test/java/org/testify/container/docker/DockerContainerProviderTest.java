/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.container.docker;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testify.ContainerInstance;
import org.testify.CutDescriptor;
import org.testify.MethodDescriptor;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.annotation.RequiresContainer;
import static org.testify.container.docker.DockerContainerProvider.DEFAULT_DAEMON_URI;
import org.testify.core.impl.DefaultTestContext;
import org.testify.core.util.AnnotationUtil;
import org.testify.github.dockerjava.core.DockerClientConfig;
import static org.testify.github.dockerjava.core.DockerClientConfig.createDefaultConfigBuilder;
import org.testify.tools.category.ContainerTests;

/**
 *
 * @author saden
 */
@Category(ContainerTests.class)
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
        Boolean startResources = false;
        Object testInstance = new Object();
        MethodDescriptor methodDescriptor = mock(MethodDescriptor.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Map<String, String> dependencies = mock(Map.class);

        TestContext testContext = new DefaultTestContext(startResources, testInstance, methodDescriptor, testDescriptor, cutDescriptor, dependencies);
        RequiresContainer delegate = AnnotationUtil.INSTANCE.newInstance(RequiresContainer.class);
        RequiresContainer requiresContainer = mock(RequiresContainer.class, delegatesTo(delegate));
        given(requiresContainer.value()).willReturn("postgres");
        given(requiresContainer.version()).willReturn("9.4");
        given(testContext.getClassName()).willReturn("TestClass");
        given(testContext.getMethodName()).willReturn("testMethod");

        DockerClientConfig.DockerClientConfigBuilder builder = createDefaultConfigBuilder()
                .withDockerHost(DEFAULT_DAEMON_URI);
        ContainerInstance result = cut.start(testContext, requiresContainer, builder);
        assertThat(result).isNotNull();
    }

}
