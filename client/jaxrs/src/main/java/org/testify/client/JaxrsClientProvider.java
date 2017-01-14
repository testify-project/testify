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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.testify.ClientInstance;
import org.testify.ClientProvider;
import org.testify.ServerInstance;
import org.testify.TestContext;
import org.testify.tools.Discoverable;

/**
 * A Jersey Client implementation of the ClientProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class JaxrsClientProvider implements ClientProvider<ClientBuilder, WebTarget> {

    private ServerInstance serverInstance;
    private Client client;

    @Override
    public ClientBuilder configure(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;

        return ClientBuilder.newBuilder();
    }

    @Override
    public ClientInstance<WebTarget> create(TestContext testContext, ClientBuilder config) {
        JaxrsClientResponseFilter responseFilter = new JaxrsClientResponseFilter(testContext);
        config.register(responseFilter);
        this.client = config.build();

        return new JaxrsClientInstance(client, serverInstance.getBaseURI());
    }

    @Override
    public void destroy() {
        client.close();
    }

}
