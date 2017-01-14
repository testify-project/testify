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
package org.testify.client;

import org.testify.ClientInstance;
import org.testify.ServerInstance;
import org.testify.TestContext;
import org.testify.annotation.Cut;
import org.testify.annotation.Fake;
import org.testify.junit.UnitTest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class JaxrsClientProviderTest {

    @Cut
    JaxrsClientProvider cut;

    @Fake
    ServerInstance serverInstance;

    @Fake
    Client client;

    @Test
    public void givenNullConfigureShouldReturnClientBuilder() {
        cut.configure(null);
    }

    @Test
    public void givenServerInstanceConfigureShouldReturn() {
        ClientBuilder result = cut.configure(serverInstance);

        assertThat(result).isNotNull();
        verifyZeroInteractions(serverInstance);
    }

    @Test(expected = NullPointerException.class)
    public void givenNullConfigurationCreateShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        ClientBuilder clientBuilder = null;
        cut.create(testContext, clientBuilder);
    }

    @Test
    public void givenClientBuilderCreateShouldReturnClientInstance() {
        ClientBuilder clientBuilder = mock(ClientBuilder.class);
        TestContext testContext = mock(TestContext.class);
        ClientInstance<WebTarget> result = cut.create(testContext, clientBuilder);

        assertThat(result).isNotNull();
        verify(clientBuilder).build();
        verify(serverInstance).getBaseURI();
    }

    @Test
    public void callToCloseShouldCloseClient() {
        cut.destroy();

        verify(client).close();
    }
}
