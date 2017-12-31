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
import static org.testifyproject.core.TestContextProperties.CLIENT_PROVIDER;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.extension.ProxyInstance;

/**
 *
 * @author saden
 */
public class ClientProxyInstanceProviderTest {

    ClientProxyInstanceProvider sut;

    @Before
    public void init() {
        sut = spy(new ClientProxyInstanceProvider());
    }

    @Test
    public void givenTestContextWithoutClientProviderCallToGetShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        Optional<ClientProvider> foundClientProvider = Optional.empty();

        given(testContext.<ClientProvider>findProperty(CLIENT_PROVIDER))
                .willReturn(foundClientProvider);

        List<ProxyInstance> result = sut.get(testContext);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenTestContextWithClientProviderCallToGetShouldReturnProxyInstances() {
        TestContext testContext = mock(TestContext.class);
        ClientProvider clientProvider = mock(ClientProvider.class);
        Optional<ClientProvider> foundClientProvider = Optional.of(clientProvider);
        ProxyInstance clientInstanceProxyInstance = mock(ProxyInstance.class);
        ProxyInstance clientProxyInstance = mock(ProxyInstance.class);
        Class clientType = Object.class;
        Class clientSupplierType = Object.class;

        given(testContext.<ClientProvider>findProperty(CLIENT_PROVIDER))
                .willReturn(foundClientProvider);
        willReturn(clientInstanceProxyInstance)
                .given(sut).createClientInstance(testContext);
        given(clientProvider.getClientType()).willReturn(clientType);
        willReturn(clientProxyInstance)
                .given(sut).createClient(testContext, clientType);
        given(clientProvider.getClientSupplierType()).willReturn(clientSupplierType);
        willReturn(clientProxyInstance)
                .given(sut).createClientSupplier(testContext, clientType);

        List<ProxyInstance> result = sut.get(testContext);

        assertThat(result).contains(clientInstanceProxyInstance, clientProxyInstance);
    }

    @Test
    public void callToCreateClientInstanceShouldReturnProxyInstance() {
        TestContext testContext = mock(TestContext.class);
        ProxyInstance result = sut.createClientInstance(testContext);

        assertThat(result).isNotNull();
        assertThat(result.getDelegate()).isNotNull();
        assertThat(result.getType()).isEqualTo(ClientInstance.class);
    }

    @Test
    public void givenTestContextCreateClientReturnProxyInstance() {
        TestContext testContext = mock(TestContext.class);
        Class<Object> clientType = Object.class;
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);
        String clientName = "clientName";
        Class clientContract = Object.class;
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Class sutType = Object.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(application.clientName()).willReturn(clientName);
        given(application.clientContract()).willReturn(clientContract);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.isSubtypeOf(clientType)).willReturn(true);
        given(sutDescriptor.getType()).willReturn(sutType);

        ProxyInstance result = sut.createClient(testContext, clientType);

        assertThat(result).isNotNull();
        assertThat(result.getDelegate()).isNotNull();
        assertThat(result.getName()).isEqualTo(clientName);
        assertThat(result.getType()).isEqualTo(sutType);
    }

    @Test
    public void givenTestContextCreateClientSupplierReturnProxyInstance() {
        TestContext testContext = mock(TestContext.class);
        Class<Object> clientType = Object.class;
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);
        String clientSupplierName = "clientSupplierName";
        Class clientSupplierContract = Object.class;
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Class sutType = Object.class;

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(application.clientSupplierName()).willReturn(clientSupplierName);
        given(application.clientSupplierContract()).willReturn(clientSupplierContract);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(sutDescriptor.isSubtypeOf(clientType)).willReturn(true);
        given(sutDescriptor.getType()).willReturn(sutType);

        ProxyInstance result = sut.createClientSupplier(testContext, clientType);

        assertThat(result).isNotNull();
        assertThat(result.getDelegate()).isNotNull();
        assertThat(result.getName()).isEqualTo(clientSupplierName);
        assertThat(result.getType()).isEqualTo(sutType);
    }
}
