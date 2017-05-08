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

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import org.easymock.cglib.proxy.Factory;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.fixture.Mockable;

/**
 *
 * @author saden
 */
public class EasyMockMockProviderTest {

    EasyMockMockProvider sut;

    @Before
    public void init() {
        sut = new EasyMockMockProvider();
    }

    @Test(expected = NullPointerException.class)
    public void givenNullCreateShouldThrowException() {
        sut.createFake(null);
    }

    @Test
    public void givenTypeCreateShouldReturnMockInstance() {
        Mockable result = sut.createFake(Mockable.class);
        assertThat(result).isInstanceOf(Factory.class);
    }

    @Test(expected = NullPointerException.class)
    public void givenTypeAndNullDelegateCreateShouldThrowException() {
        sut.createVirtual(Mockable.class, null);
    }

    @Test
    public void givenTypeAndDelegateCreateShouldReturnMockInstance() {
        Mockable delegate = new Mockable();

        Mockable result = sut.createVirtual(Mockable.class, delegate);
        assertThat(result).isInstanceOf(Factory.class);

        result.setUpdated(Boolean.TRUE);
        assertThat(delegate.getUpdated()).isTrue();
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
    public void givenMockIsMockShouldReturnFalse() {
        Mockable instance = createMock(Mockable.class);
        Boolean result = sut.isMock(instance);
        assertThat(result).isTrue();
    }

}
