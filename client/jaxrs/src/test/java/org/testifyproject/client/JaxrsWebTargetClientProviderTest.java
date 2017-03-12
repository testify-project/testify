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
import javax.ws.rs.client.WebTarget;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Fake;
import org.testifyproject.junit.UnitTest;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class JaxrsWebTargetClientProviderTest {

    @Cut
    JaxrsWebTargetClientProvider cut;

    @Fake
    Client client;

    @Test
    public void givenNullConfigureShouldReturnClientBuilder() {
        cut.configure(mock(TestContext.class), null);
    }

    @Test
    public void givenServerInstanceConfigureShouldReturn() {
        TestContext testContext = mock(TestContext.class);
        URI baseURI = URI.create("http://test.server");
        ClientBuilder result = cut.configure(testContext, baseURI);

        assertThat(result).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullConfigurationCreateShouldThrowException() {
        TestContext testContext = mock(TestContext.class);
        URI baseURI = URI.create("http://test.server");
        ClientBuilder clientBuilder = null;

        cut.create(testContext, baseURI, clientBuilder);
    }

    @Test
    public void givenClientBuilderCreateShouldReturnClientInstance() {
        TestContext testContext = mock(TestContext.class);
        URI baseURI = URI.create("http://test.server");
        ClientBuilder clientBuilder = mock(ClientBuilder.class);
        WebTarget webTarget = mock(WebTarget.class);

        given(clientBuilder.build()).willReturn(client);
        given(client.target(baseURI)).willReturn(webTarget);

        Instance<WebTarget> result = cut.create(testContext, baseURI, clientBuilder);

        assertThat(result).isNotNull();
        assertThat(result.getInstance()).isEqualTo(webTarget);
        assertThat(result.getContract()).contains(WebTarget.class);

        verify(clientBuilder).build();
        verify(client).target(baseURI);
    }

    @Test
    public void callToCloseShouldCloseClient() {
        cut.destroy();

        verify(client).close();
    }
}
