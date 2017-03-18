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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import org.testifyproject.trait.LoggingTrait;

/**
 * A JAX-RS client response filter that stores server error responses in the
 * test context object.
 *
 * @author saden
 */
public class ErrorClientResponseFilter implements ClientResponseFilter, LoggingTrait {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        URI uri = requestContext.getUri();
        Response.StatusType statusInfo = responseContext.getStatusInfo();

        if (statusInfo.getFamily() == Response.Status.Family.SERVER_ERROR) {
            if (responseContext.hasEntity()) {
                InputStream entityStream = responseContext.getEntityStream();

                int bufferSize = 1024;
                ByteArrayOutputStream output = new ByteArrayOutputStream(bufferSize);

                byte[] buffer = new byte[bufferSize];
                int length;

                while ((length = entityStream.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }

                String resposneBody = output.toString("UTF-8");
                error("Resource {} request failed due to '{} ({})':\n%s",
                        uri.getPath(),
                        statusInfo.getReasonPhrase(),
                        statusInfo.getStatusCode(),
                        resposneBody);
            } else {
                error("Resource '{}' request failed due to '{} ({})'",
                        uri.getPath(),
                        statusInfo.getReasonPhrase(),
                        statusInfo.getStatusCode());
            }

        }
    }
}
