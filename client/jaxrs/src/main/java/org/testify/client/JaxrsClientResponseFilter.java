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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import org.testify.TestContext;

/**
 * A JAX-RS client response filter that stores server error responses in the
 * test context object.
 *
 * @author saden
 */
public class JaxrsClientResponseFilter implements ClientResponseFilter {

    private final TestContext testContext;

    JaxrsClientResponseFilter(TestContext testContext) {
        this.testContext = testContext;
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        URI uri = requestContext.getUri();
        Response.StatusType statusInfo = responseContext.getStatusInfo();

        if (statusInfo.getFamily() == Response.Status.Family.SERVER_ERROR) {
            if (responseContext.hasEntity()) {
                InputStream entityStream = responseContext.getEntityStream();

                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = entityStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }

                String resposneBody = result.toString("UTF-8");
                testContext.error("Resource {} request failed due to '{} ({})':\n%s",
                        uri.getPath(),
                        statusInfo.getReasonPhrase(),
                        statusInfo.getStatusCode(),
                        resposneBody);
            } else {
                testContext.error("Resource '{}' request failed due to '{} ({})'",
                        uri.getPath(),
                        statusInfo.getReasonPhrase(),
                        statusInfo.getStatusCode());
            }

        }
    }
}
