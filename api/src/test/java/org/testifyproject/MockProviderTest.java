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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 *
 * @author saden
 */
public class MockProviderTest {
    
    MockProvider sut;

    @Before
    public void init() {
        sut = mock(MockProvider.class, Answers.CALLS_REAL_METHODS);
    }

    @Test
    public void callToCreateVirtualSutShouldCallCreateVirtual() {
        Class type = Object.class;
        Object delegate = new Object();
        Object virtualInstance = new Object();
        
        given(sut.createVirtual(type, delegate)).willReturn(virtualInstance);
        
        Object result = sut.createVirtualSut(type, delegate);

        assertThat(result).isEqualTo(virtualInstance);
        verify(sut).createVirtualSut(type, delegate);
        verify(sut).createVirtual(type, delegate);
        verifyNoMoreInteractions(sut);
    }
    
}
