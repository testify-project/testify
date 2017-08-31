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
import org.testifyproject.Instance;
import org.testifyproject.ServerInstance;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.TestContextProperties;

/**
 *
 * @author saden
 */
public class ServerInstanceProviderTest {

    ServerInstanceProvider sut;

    @Before
    public void init() {
        sut = new ServerInstanceProvider();
    }

    @Test
    public void givenTestContextWithoutServerInstanceGetShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        Optional<ServerInstance<Object>> foundServerInstance = Optional.empty();

        given(testContext.<ServerInstance<Object>>findProperty(TestContextProperties.APP_SERVER_INSTANCE))
                .willReturn(foundServerInstance);

        List<Instance> result = sut.get(testContext);

        assertThat(result).isEmpty();
        verify(testContext).findProperty(TestContextProperties.APP_SERVER_INSTANCE);
    }

    @Test
    public void givenTestContextWithServerInstanceGetShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        ServerInstance<Object> serverInstance = mock(ServerInstance.class);
        Optional<ServerInstance<Object>> foundServerInstance = Optional.of(serverInstance);
        Instance<Object> server = mock(Instance.class);
        Instance<Object> instance = DefaultInstance.of(serverInstance, ServerInstance.class);

        given(testContext.<ServerInstance<Object>>findProperty(TestContextProperties.APP_SERVER_INSTANCE))
                .willReturn(foundServerInstance);
        given(serverInstance.getServer()).willReturn(server);

        List<Instance> result = sut.get(testContext);

        assertThat(result).hasSize(2).contains(server, instance);
        verify(testContext).findProperty(TestContextProperties.APP_SERVER_INSTANCE);
        verify(serverInstance).getServer();
    }

}
