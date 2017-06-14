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
package org.testifyproject.mock;

import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.util.MockUtil;
import org.testifyproject.fixture.Mockable;

/**
 *
 * @author saden
 */
public class MockitoMockProviderTest {

    MockitoMockProvider sut;

    @Before
    public void init() {
        sut = new MockitoMockProvider();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCreateFakeShouldThrowException() {
        sut.createFake(null);
    }

    @Test(expected = MockitoException.class)
    public void givenFinalTypeCreateFakeShouldThrowException() {
        URI result = sut.createFake(URI.class);

        assertThat(MockUtil.isMock(result)).isTrue();
    }

    @Test
    public void givenNonFinalTypeCreateFakeShouldReturnFakeInstance() {
        Mockable result = sut.createFake(Mockable.class);

        assertThat(MockUtil.isMock(result)).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void givenTypeAndNullDelegateCreateVirtualShouldThrowException() {
        Mockable result = sut.createVirtual(Mockable.class, null);

        result.getUpdated();
    }

    @Test
    public void givenTypeAndDelegateCreateVirtualShouldReturnVirtualInstance() {
        Mockable delegate = new Mockable();

        Mockable result = sut.createVirtual(Mockable.class, delegate);
        result.setUpdated(Boolean.TRUE);

        assertThat(MockUtil.isMock(result)).isTrue();
        assertThat(delegate.getUpdated()).isTrue();
    }

    @Test
    public void givenTypeAndDelegateCreateVirtualSutShouldReturnVirtualInstance() {
        Mockable delegate = new Mockable();

        Mockable result = sut.createVirtualSut(Mockable.class, delegate);
        result.setUpdated(Boolean.TRUE);

        assertThat(MockUtil.isSpy(result)).isTrue();
        assertThat(delegate.getUpdated()).isFalse();
    }

    @Test
    public void givenNullIsMockShouldReturnFalse() {
        Mockable instance = null;

        Boolean result = sut.isMock(instance);

        assertThat(result).isFalse();
    }

    @Test
    public void givenNonMockIsMockShouldReturnFalse() {
        Mockable instance = new Mockable();

        Boolean result = sut.isMock(instance);

        assertThat(result).isFalse();
    }

    @Test
    public void givenMockIsMockShouldReturnTrue() {
        Mockable instance = mock(Mockable.class);

        Boolean result = sut.isMock(instance);

        assertThat(result).isTrue();
    }

}
