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
package org.testifyproject.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.ClientInstance;
import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;

/**
 *
 * @author saden
 */
public class WebTargetClientProviderTest {

    WebTargetClientProvider sut;

    TestContext testContext;
    Application application;
    URI baseURI;

    @Before
    public void init() {
        testContext = mock(TestContext.class);
        application = mock(Application.class, Answers.RETURNS_MOCKS);
        baseURI = URI.create("uri://test");
        sut = new WebTargetClientProvider();
    }

    @Test
    public void givenValidConfigurationConfigureShouldReturnClientBuilder() {
        ClientBuilder result = sut.configure(testContext, application, baseURI);

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullConfigurationCreateShouldThrowException() {
        sut.create(testContext, application, baseURI, null);
    }

    @Test
    public void givenValidConfigurationCreateShouldReturnClientInstance() {
        Client client = mock(Client.class);
        ClientBuilder clientBuilder = mock(ClientBuilder.class);
        WebTarget webTarget = mock(WebTarget.class);

        given(clientBuilder.build()).willReturn(client);
        given(client.target(baseURI)).willReturn(webTarget);

        ClientInstance<WebTarget, Client> result = sut.create(testContext, application, baseURI,
                clientBuilder);

        assertThat(result).isNotNull();
        assertThat(result.getFqn()).isEqualTo("jerseyClient");
        assertThat(result.getApplication()).isEqualTo(application);
        assertThat(result.getClient()).isNotNull();
        assertThat(result.getClientSupplier()).isNotNull();

        verify(clientBuilder).build();
        verify(client).target(baseURI);

        verifyNoMoreInteractions(testContext, client, clientBuilder, webTarget);
    }

    @Test
    public void callToCloseShouldCloseClient() {
        ClientInstance<WebTarget, Client> clientInstance = mock(ClientInstance.class);
        Client client = mock(Client.class);
        Instance instance = mock(Instance.class);
        Optional<Instance<Client>> foundInstance = Optional.of(instance);

        given(clientInstance.getClientSupplier()).willReturn(foundInstance);
        given(instance.getValue()).willReturn(client);

        sut.destroy(clientInstance);

        verify(clientInstance).getClientSupplier();
        verify(instance).getValue();
        verify(client).close();

        verifyNoMoreInteractions(testContext, application, clientInstance, instance,
                client);
    }
}
