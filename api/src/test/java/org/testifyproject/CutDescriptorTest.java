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

import java.lang.reflect.Field;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.annotation.Cut;
import org.testifyproject.fixture.InjectableFieldService;

/**
 *
 * @author saden
 */
public class CutDescriptorTest {

    CutDescriptor cut;

    @Before
    public void init() {
        cut = mock(CutDescriptor.class, Answers.CALLS_REAL_METHODS);
    }

    @Test
    public void givenCutFieldGetCutShouldReturnEmptyOptional() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("cut");

        given(cut.getMember()).willReturn(field);

        Cut result = cut.getCut();

        assertThat(result).isNotNull();
    }

    @Test
    public void callToIsVirtualCutOnNonVirtualCutFieldShouldReturnFalse() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("cut");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isVirtualCut();

        assertThat(result).isFalse();
    }

    @Test
    public void callToIsVirtualCutOnVirtualCutFieldShouldReturnFalse() throws NoSuchFieldException {
        Field field = InjectableFieldService.class.getDeclaredField("virtualCut");

        given(cut.getMember()).willReturn(field);

        Boolean result = cut.isVirtualCut();

        assertThat(result).isTrue();
    }

}
