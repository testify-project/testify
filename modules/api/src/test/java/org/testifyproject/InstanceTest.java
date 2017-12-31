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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

/**
 *
 * @author saden
 */
public class InstanceTest {

    Instance<String> sut;

    @Before
    public void init() {
        sut = mock(Instance.class, Answers.CALLS_REAL_METHODS);
    }

    @After
    public void destroy() {
        verifyNoMoreInteractions(sut);
    }

    @Test
    public void callToGetNameShouldReturnEmptyOptional() {
        String result = sut.getName();

        assertThat(result).isNull();
        verify(sut).getName();
    }

    @Test
    public void callToGetContractShouldReturnEmptyOptional() {
        Class<? extends String> result = sut.getContract();

        assertThat(result).isNull();
        verify(sut).getContract();
    }

    @Test
    public void callToQueryShouldReturnResult() {
        String test = "test";
        Function<String, String> function = s -> test;
        String result = sut.query(function);

        assertThat(result).isEqualTo(test);
        verify(sut).query(function);
        verify(sut).getValue();
    }

    @Test
    public void callToCommandShouldDoNothing() {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        Consumer<String> consumer = s -> atomicBoolean.set(true);
        sut.command(consumer);

        assertThat(atomicBoolean).isTrue();
        verify(sut).command(consumer);
        verify(sut).getValue();
    }

}
