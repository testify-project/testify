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

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.testifyproject.ClientInstance;
import org.testifyproject.ServerInstance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Fake;
import org.testifyproject.junit.UnitTest;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class JaxrsClientProviderTest {

    @Cut
    JaxrsClientProvider cut;

    @Fake
    TestContext testContext;

    @Fake
    ServerInstance serverInstance;

    @Fake
    Client client;

    @Test
    public void givenNullConfigureShouldReturnClientBuilder() {
        cut.configure(testContext, null);
    }

    @Test
    public void givenServerInstanceConfigureShouldReturn() {
        ClientBuilder result = cut.configure(testContext, serverInstance);

        assertThat(result).isNotNull();
        verifyZeroInteractions(serverInstance);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullConfigurationCreateShouldThrowException() {
        ClientBuilder clientBuilder = null;
        cut.create(testContext, clientBuilder);
    }

    @Test
    public void givenClientBuilderCreateShouldReturnClientInstance() {
        ClientBuilder clientBuilder = mock(ClientBuilder.class);
        URI baseURI = URI.create("http://test.server");

        given(serverInstance.getBaseURI()).willReturn(baseURI);
        given(clientBuilder.build()).willReturn(client);

        ClientInstance<Client> result = cut.create(testContext, clientBuilder);

        assertThat(result).isNotNull();
        assertThat(result.getBaseURI()).isEqualTo(baseURI);
        assertThat(result.getClient()).isEqualTo(client);
        assertThat(result.getContract()).contains(Client.class);

        verify(clientBuilder).build();
        verify(serverInstance).getBaseURI();
    }

    @Test
    public void callToCloseShouldCloseClient() {
        cut.destroy();

        verify(client).close();
    }
}
