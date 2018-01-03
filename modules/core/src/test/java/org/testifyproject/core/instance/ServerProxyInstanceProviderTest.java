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
package org.testifyproject.core.instance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.testifyproject.core.TestContextProperties.SERVER_PROVIDER;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.extension.ProxyInstance;

/**
 *
 * @author saden
 */
public class ServerProxyInstanceProviderTest {

    ServerProxyInstanceProvider sut;

    @Before
    public void init() {
        sut = spy(new ServerProxyInstanceProvider());
    }

    @Test
    public void givenTestContextWithoutServerProviderCallToGetShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        Optional<ServerProvider> foundServerProvider = Optional.empty();

        given(testContext.<ServerProvider>findProperty(SERVER_PROVIDER))
                .willReturn(foundServerProvider);

        List<ProxyInstance> result = sut.get(testContext);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenTestContextWithServerProviderCallToGetShouldReturnProxyInstances() {
        TestContext testContext = mock(TestContext.class);
        ServerProvider serverProvider = mock(ServerProvider.class);
        Optional<ServerProvider> foundServerProvider = Optional.of(serverProvider);
        ProxyInstance serverInstanceProxyInstance = mock(ProxyInstance.class);
        ProxyInstance serverProxyInstance = mock(ProxyInstance.class);
        Class serverType = Object.class;

        given(testContext.<ServerProvider>findProperty(SERVER_PROVIDER))
                .willReturn(foundServerProvider);
        willReturn(serverInstanceProxyInstance)
                .given(sut).createServerInstance(testContext);
        given(serverProvider.getServerType()).willReturn(serverType);
        willReturn(serverProxyInstance)
                .given(sut).createServer(testContext, serverType);

        List<ProxyInstance> result = sut.get(testContext);

        assertThat(result).contains(serverInstanceProxyInstance, serverProxyInstance);
    }

    @Test
    public void callToCreateServerInstanceShouldReturnProxyInstance() {
        TestContext testContext = mock(TestContext.class);
        ProxyInstance result = sut.createServerInstance(testContext);

        assertThat(result).isNotNull();
        assertThat(result.getDelegate()).isNotNull();
        assertThat(result.getType()).isEqualTo(ServerInstance.class);
    }

    @Test
    public void givenTestContextWithApplicationConfigCreateServerReturnProxyInstance() {
        TestContext testContext = mock(TestContext.class);
        Class<Object> serverType = Object.class;
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);
        String serverName = "serverName";
        Class serverContract = Object.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(application.serverName()).willReturn(serverName);
        given(application.serverContract()).willReturn(serverContract);

        ProxyInstance result = sut.createServer(testContext, serverType);

        assertThat(result).isNotNull();
        assertThat(result.getDelegate()).isNotNull();
        assertThat(result.getName()).isEqualTo(serverName);
        assertThat(result.getType()).isEqualTo(serverContract);
    }
    
    @Test
    public void givenTestContextWithoutApplicationConfigCreateServerReturnProxyInstance() {
        TestContext testContext = mock(TestContext.class);
        Class<Object> serverType = Object.class;
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Optional<Application> foundApplication = Optional.empty();
        String serverName = null;
        Class serverContract = serverType;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);

        ProxyInstance result = sut.createServer(testContext, serverType);

        assertThat(result).isNotNull();
        assertThat(result.getDelegate()).isNotNull();
        assertThat(result.getName()).isEqualTo(serverName);
        assertThat(result.getType()).isEqualTo(serverContract);
    }
}
