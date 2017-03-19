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
package org.testifyproject.core;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testifyproject.TestContext;

/**
 *
 * @author saden
 */
public class TestContextHolderTest {

    TestContextHolder cut;
    InheritableThreadLocal<TestContext> inheritableThreadLocal;

    @Before
    public void init() {
        inheritableThreadLocal = new InheritableThreadLocal<>();

        cut = TestContextHolder.of(inheritableThreadLocal);
    }

    @Test
    public void givenTestContextSetShouldSetIheritableThreadLocal() {
        TestContext testContext = mock(TestContext.class);

        cut.set(testContext);

        assertThat(inheritableThreadLocal.get()).isEqualTo(testContext);
    }

    @Test
    public void callToGetShouldReturnTestContext() {
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        Optional<TestContext> result = cut.get();

        assertThat(result).contains(testContext);
    }

    @Test
    public void callToRemoveShouldRemoveTestContext() {
        TestContext testContext = mock(TestContext.class);
        inheritableThreadLocal.set(testContext);

        cut.remove();

        assertThat(inheritableThreadLocal.get()).isNull();
    }

}
