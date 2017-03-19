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
import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultClientInstance;
import org.testifyproject.tools.Discoverable;

/**
 * A Jersey Client implementation of the ClientProvider SPI contract that
 * provides a usable {@link WebTarget} instance.
 *
 * @author saden
 */
@Discoverable
public class WebTargetClientProvider implements ClientProvider<ClientBuilder, WebTarget> {

    private Client client;

    @Override
    public ClientBuilder configure(TestContext testContext, URI baseURI) {
        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.register(new ErrorClientResponseFilter());

        return builder;
    }

    @Override
    public ClientInstance<WebTarget> create(TestContext testContext, URI baseURI, ClientBuilder clientBuilder) {
        client = clientBuilder.build();
        WebTarget webTarget = client.target(baseURI);

        return DefaultClientInstance.of(webTarget, WebTarget.class);
    }

    @Override
    public void destroy() {
        client.close();
    }

}
