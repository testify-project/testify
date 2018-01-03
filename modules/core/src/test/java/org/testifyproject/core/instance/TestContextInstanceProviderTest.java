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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultInstance;

/**
 *
 * @author saden
 */
public class TestContextInstanceProviderTest {

    TestContextInstanceProvider sut;

    @Before
    public void init() {
        sut = new TestContextInstanceProvider();
    }

    @Test
    public void givenTestContextGetShouldReturnTestContext() {
        TestContext testContext = mock(TestContext.class);
        Instance<Object> instance = DefaultInstance.of(testContext, TestContext.class);
        List<Instance> result = sut.get(testContext);

        assertThat(result).containsExactly(instance);
    }
}
