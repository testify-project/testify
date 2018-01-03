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

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.testifyproject.annotation.Sut;
import org.testifyproject.fixture.InjectableFieldService;

/**
 *
 * @author saden
 */
public class SutDescriptorTest {

    SutDescriptor sut;

    @Before
    public void init() {
        sut = mock(SutDescriptor.class, Answers.CALLS_REAL_METHODS);
    }

    @Test
    public void givenSutFieldGetSutShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("sut");

        given(sut.getMember()).willReturn(field);

        Sut result = sut.getSut();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToIsVirtualSutOnNonVirtualSutFieldShouldReturnFalse() throws
            NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("sut");

        given(sut.getMember()).willReturn(field);

        Boolean result = sut.isVirtualSut();

        assertThat(result).isFalse();
    }

    @Test
    public void callToIsVirtualSutOnVirtualSutFieldShouldReturnFalse() throws
            NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("virtualSut");

        given(sut.getMember()).willReturn(field);

        Boolean result = sut.isVirtualSut();

        assertThat(result).isTrue();
    }

}
