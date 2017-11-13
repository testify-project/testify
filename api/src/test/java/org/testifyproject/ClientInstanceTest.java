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
package org.testifyproject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

/**
 *
 * @author saden
 */
public class ClientInstanceTest {

    ClientInstance<Object, Object> sut;

    @Before
    public void init() {
        sut = mock(ClientInstance.class, Answers.CALLS_REAL_METHODS);
    }

    @After
    public void destroy() {
        verifyNoMoreInteractions(sut);
    }

    @Test
    public void callToQueryShouldExecuteQuery() {
        Instance clientInstance = mock(Instance.class);
        Instance clientSupplierInstance = mock(Instance.class);

        Optional<Instance<Object>> foundClientSupplierInstance =
                Optional.of(clientSupplierInstance);
        Object functionResult = new Object();
        BiFunction<Object, Object, Object> function = (t, s) -> functionResult;

        given(sut.getClient()).willReturn(clientInstance);
        given(sut.getClientSupplier()).willReturn(foundClientSupplierInstance);

        Object result = sut.query(function);

        assertThat(result).isEqualTo(functionResult);
        verify(sut).query(function);
        verify(sut).getClient();
        verify(sut).getClientSupplier();
        verify(clientInstance).getValue();
        verify(clientSupplierInstance).getValue();
    }

    @Test
    public void callToCommandShouldExecuteCommand() {
        Instance clientInstance = mock(Instance.class);
        Instance clientSupplierInstance = mock(Instance.class);

        Optional<Instance<Object>> foundClientSupplierInstance =
                Optional.of(clientSupplierInstance);
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        BiConsumer<Object, Object> consumer = (t, s) -> atomicBoolean.set(true);

        given(sut.getClient()).willReturn(clientInstance);
        given(sut.getClientSupplier()).willReturn(foundClientSupplierInstance);

        sut.command(consumer);

        assertThat(atomicBoolean).isTrue();
        verify(sut).command(consumer);
        verify(sut).getClient();
        verify(sut).getClientSupplier();
        verify(clientInstance).getValue();
        verify(clientSupplierInstance).getValue();
    }

}
