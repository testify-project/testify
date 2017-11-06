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

import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ClientInstance;
import org.testifyproject.TestContext;

/**
 *
 * @author saden
 */
public class ClientProxyInstanceProviderTest {

    ClientProxyInstanceProvider sut;

    @Before
    public void init() {
        sut = new ClientProxyInstanceProvider();
    }

    @Test
    public void givenTestContextWithoutClientInstanceGetShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        Optional<ClientInstance> foundClientInstance = Optional.empty();

    }

}
