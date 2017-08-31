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
package org.testifyproject.core.instance;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.testifyproject.ClientInstance;
import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.TestContextProperties;

/**
 *
 * @author saden
 */
public class ClientInstanceProviderTest {

    ClientInstanceProvider sut;

    @Before
    public void init() {
        sut = new ClientInstanceProvider();
    }

    @Test
    public void givenTestContextWithoutClientInstanceGetShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        Optional<ClientInstance<Object>> foundClientInstance = Optional.empty();

        given(testContext.<ClientInstance<Object>>findProperty(TestContextProperties.APP_CLIENT_INSTANCE))
                .willReturn(foundClientInstance);

        List<Instance> result = sut.get(testContext);

        assertThat(result).isEmpty();
        verify(testContext).findProperty(TestContextProperties.APP_CLIENT_INSTANCE);
    }

    @Test
    public void givenTestContextWithClientInstanceGetShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        ClientInstance<Object> clientInstance = mock(ClientInstance.class);
        Optional<ClientInstance<Object>> foundClientInstance = Optional.of(clientInstance);
        Instance<Object> client = mock(Instance.class);
        Instance<Object> clientProvider = mock(Instance.class);
        Optional<Instance<Object>> foundClientProvider = Optional.of(clientProvider);
        Instance<Object> instance = DefaultInstance.of(clientInstance, ClientInstance.class);

        given(testContext.<ClientInstance<Object>>findProperty(TestContextProperties.APP_CLIENT_INSTANCE))
                .willReturn(foundClientInstance);
        given(clientInstance.getClient()).willReturn(client);
        given(clientInstance.getClientProvider()).willReturn(foundClientProvider);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(3).contains(client, clientProvider, instance);
        verify(testContext).findProperty(TestContextProperties.APP_CLIENT_INSTANCE);
        verify(clientInstance).getClient();
        verify(clientInstance).getClientProvider();
    }

}
