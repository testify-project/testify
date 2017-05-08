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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.TestifyException;

/**
 *
 * @author saden
 */
public class ErrorClientResponseFilterTest {

    ErrorClientResponseFilter sut;

    @Before
    public void init() {
        sut = new ErrorClientResponseFilter();
    }

    @Test
    public void givenNonErrorFamilyCallToFilterShouldDoNothing() throws IOException {
        ClientRequestContext requestContext = mock(ClientRequestContext.class);
        ClientResponseContext responseContext = mock(ClientResponseContext.class);
        URI uri = URI.create("uri://test");
        Response.StatusType statusInfo = mock(Response.StatusType.class);
        Response.Status.Family family = Response.Status.Family.SUCCESSFUL;

        given(requestContext.getUri()).willReturn(uri);
        given(responseContext.getStatusInfo()).willReturn(statusInfo);
        given(statusInfo.getFamily()).willReturn(family);

        sut.filter(requestContext, responseContext);

        verify(requestContext).getUri();
        verify(responseContext).getStatusInfo();
        verify(statusInfo).getFamily();
        verifyNoMoreInteractions(requestContext, responseContext, statusInfo);
    }

    @Test(expected = TestifyException.class)
    public void givenErrorFamilyWithoutEntityCallToFilterShouldDoRaiseException() throws IOException {
        ClientRequestContext requestContext = mock(ClientRequestContext.class);
        ClientResponseContext responseContext = mock(ClientResponseContext.class);
        URI uri = URI.create("uri://test");
        Response.StatusType statusInfo = mock(Response.StatusType.class);
        Response.Status.Family family = Response.Status.Family.SERVER_ERROR;
        String reasonPhrase = INTERNAL_SERVER_ERROR.getReasonPhrase();
        Integer statusCode = INTERNAL_SERVER_ERROR.getStatusCode();

        given(requestContext.getUri()).willReturn(uri);
        given(responseContext.getStatusInfo()).willReturn(statusInfo);
        given(statusInfo.getFamily()).willReturn(family);
        given(statusInfo.getReasonPhrase()).willReturn(reasonPhrase);
        given(statusInfo.getStatusCode()).willReturn(statusCode);
        given(responseContext.hasEntity()).willReturn(false);

        try {
            sut.filter(requestContext, responseContext);
        } catch (TestifyException e) {
            assertThat(e.getMessage()).contains(uri.getPath(), reasonPhrase, statusCode.toString());
            verify(requestContext).getUri();
            verify(responseContext).getStatusInfo();
            verify(statusInfo).getFamily();
            verify(responseContext).hasEntity();
            verify(statusInfo).getReasonPhrase();
            verify(statusInfo).getStatusCode();
            verifyNoMoreInteractions(requestContext, responseContext, statusInfo);

            throw e;
        }

    }

    @Test(expected = TestifyException.class)
    public void givenErrorFamilyWithEntityCallToFilterShouldDoRaiseException() throws IOException {
        ClientRequestContext requestContext = mock(ClientRequestContext.class);
        ClientResponseContext responseContext = mock(ClientResponseContext.class);
        URI uri = URI.create("uri://test");
        Response.StatusType statusInfo = mock(Response.StatusType.class);
        Response.Status.Family family = Response.Status.Family.SERVER_ERROR;
        String reasonPhrase = INTERNAL_SERVER_ERROR.getReasonPhrase();
        Integer statusCode = INTERNAL_SERVER_ERROR.getStatusCode();
        String responseBody = "Server Error!!";
        InputStream entityStream = new ByteArrayInputStream(responseBody.getBytes(UTF_8));

        given(requestContext.getUri()).willReturn(uri);
        given(responseContext.getStatusInfo()).willReturn(statusInfo);
        given(statusInfo.getFamily()).willReturn(family);
        given(statusInfo.getReasonPhrase()).willReturn(reasonPhrase);
        given(statusInfo.getStatusCode()).willReturn(statusCode);
        given(responseContext.hasEntity()).willReturn(true);
        given(responseContext.getEntityStream()).willReturn(entityStream);
        
        try {
            sut.filter(requestContext, responseContext);
        } catch (TestifyException e) {
            assertThat(e.getMessage()).contains(uri.getPath(), reasonPhrase, statusCode.toString(), responseBody);
            verify(requestContext).getUri();
            verify(responseContext).getStatusInfo();
            verify(statusInfo).getFamily();
            verify(responseContext).hasEntity();
            verify(statusInfo).getReasonPhrase();
            verify(statusInfo).getStatusCode();
            verify(responseContext).getEntityStream();
            verifyNoMoreInteractions(requestContext, responseContext, statusInfo);
            
            throw e;
        }
    }

}
