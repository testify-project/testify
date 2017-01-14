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
package org.testify.mock;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.mockito.internal.util.MockUtil;
import org.testify.fixture.Mockable;

/**
 *
 * @author saden
 */
public class MockitoMockProviderTest {

    MockitoMockProvider cut;

    @Before
    public void init() {
        cut = new MockitoMockProvider();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCreateShouldThrowException() {
        cut.createFake(null);
    }

    @Test
    public void givenTypeCreateShouldReturnMockInstance() {
        Mockable result = cut.createFake(Mockable.class);
        assertThat(MockUtil.isMock(result)).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenTypeAndNullDelegateCreateShouldThrowException() {
        Mockable result = cut.createVirtual(Mockable.class, null);
        result.getUpdated();
    }

    @Test
    public void givenTypeAndDelegateCreateShouldReturnMockInstance() {
        Mockable delegate = new Mockable();

        Mockable result = cut.createVirtual(Mockable.class, delegate);
        assertThat(MockUtil.isMock(result)).isTrue();

        result.setUpdated(Boolean.TRUE);
        assertThat(delegate.getUpdated()).isTrue();
    }

    @Test
    public void givenNullIsMockShouldReturnFalse() {
        Mockable instance = null;
        Boolean result = cut.isMock(instance);
        assertThat(result).isFalse();
    }

    @Test
    public void givenNonMockIsMockShouldReturnFalse() {
        Mockable instance = new Mockable();
        Boolean result = cut.isMock(instance);
        assertThat(result).isFalse();
    }

    @Test
    public void givenMockIsMockShouldReturnFalse() {
        Mockable instance = mock(Mockable.class);
        Boolean result = cut.isMock(instance);
        assertThat(result).isTrue();
    }

}
