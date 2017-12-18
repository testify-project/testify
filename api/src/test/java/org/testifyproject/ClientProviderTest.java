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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

/**
 *
 * @author saden
 */
public class ClientProviderTest {

    ClientProvider<Object, Object, Object> sut;

    @Before
    public void init() {
        sut = mock(ClientProvider.class, Answers.CALLS_REAL_METHODS);
    }

    @After
    public void destroy() {
        verifyNoMoreInteractions(sut);
    }

    @Test
    public void callToGetClientSupplierTypeShouldReturnNull() {
        Class<Object> result = sut.getClientSupplierType();

        assertThat(result).isNull();
        verify(sut).getClientSupplierType();
    }

}
