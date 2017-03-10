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
import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.ServerInstance;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultClientInstance;

/**
 * A Jersey Client implementation of the ClientProvider SPI contract that
 * provides a usable {@link Client} instance.
 *
 * @author saden
 */
public class JaxrsClientProvider implements ClientProvider<ClientBuilder, Client> {

    private ServerInstance serverInstance;
    private Client client;

    @Override
    public ClientBuilder configure(TestContext testContext, ServerInstance serverInstance) {
        this.serverInstance = serverInstance;

        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.register(new ErrorClientResponseFilter());

        return builder;
    }

    @Override
    public ClientInstance<Client> create(TestContext testContext, ClientBuilder clientBuilder) {
        this.client = clientBuilder.build();

        URI baseURI = serverInstance.getBaseURI();

        return DefaultClientInstance.of(baseURI, client, Client.class);
    }

    @Override
    public void destroy() {
        client.close();
    }

}