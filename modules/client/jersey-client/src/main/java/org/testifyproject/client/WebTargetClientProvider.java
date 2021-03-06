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
import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.ClientInstanceBuilder;

/**
 * A Jersey Client implementation of the ClientProvider SPI contract that provides a usable
 * {@link WebTarget} instance.
 *
 * @author saden
 */
@Discoverable
public class WebTargetClientProvider implements ClientProvider<ClientBuilder, WebTarget, Client> {

    @Override
    public ClientBuilder configure(TestContext testContext, Application application,
            URI baseURI) {
        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.register(new ErrorClientResponseFilter());

        return builder;
    }

    @Override
    public ClientInstance<WebTarget, Client> create(TestContext testContext,
            Application application,
            URI baseURI,
            ClientBuilder clientBuilder) {
        Client client = clientBuilder.build();
        WebTarget webTarget = client.target(baseURI);

        return ClientInstanceBuilder.builder()
                .client(webTarget, getClientType())
                .clientSupplier(client, getClientSupplierType())
                .build("jerseyClient", application);
    }

    @Override
    public void destroy(ClientInstance<WebTarget, Client> clientInstance) {
        clientInstance.getClientSupplier()
                .map(Instance::getValue)
                .map(Client.class::cast)
                .ifPresent(Client::close);
    }

    @Override
    public Class<WebTarget> getClientType() {
        return WebTarget.class;
    }

    @Override
    public Class<Client> getClientSupplierType() {
        return Client.class;
    }

}
