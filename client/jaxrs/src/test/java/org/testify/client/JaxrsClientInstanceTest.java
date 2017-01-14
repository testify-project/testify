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

import org.testify.annotation.CollaboratorProvider;
import org.testify.annotation.Cut;
import org.testify.junit.UnitTest;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class JaxrsClientInstanceTest {

    @Cut
    JaxrsClientInstance cut;

    @CollaboratorProvider
    public Object[] collaborators() {
        Client client = mock(Client.class);
        URI baseUri = URI.create("urn://test");
        given(client.target(baseUri)).willReturn(mock(WebTarget.class));

        return new Object[]{client, baseUri};
    }

    @Test
    public void callToGetBaseURIShouldReturnClient() {
        URI result = cut.getBaseURI();
        assertThat(result).isNotNull();
    }

    @Test
    public void callToGetTargetShouldReturnClient() {
        WebTarget result = cut.getTarget();
        assertThat(result).isNotNull();
    }
}
