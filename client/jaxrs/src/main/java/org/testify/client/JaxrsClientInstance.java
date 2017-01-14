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

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.testify.ClientInstance;

/**
 * Jersey Client based ClientInstance implementation.
 *
 * @author saden
 */
public class JaxrsClientInstance implements ClientInstance<WebTarget> {

    private final Client client;
    private final URI baseURI;

    JaxrsClientInstance(Client client, URI baseURI) {
        this.client = client;
        this.baseURI = baseURI;
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public WebTarget getTarget() {
        return client.target(baseURI);
    }

}
