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
package org.testify.junit;

import javax.inject.Provider;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testify.annotation.Cut;
import org.testify.annotation.Fake;
import org.testify.junit.fixture.ImplicitGenericType;
import org.testify.junit.fixture.collaborator.Hello;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class ImplicitGenericTypeTest {

    @Cut
    ImplicitGenericType cut;

    @Fake
    Provider<Hello> collaborator;

    @Before
    public void verifyInjections() {
        assertThat(cut).isNotNull();
        assertThat(collaborator).isNotNull();
        assertThat(cut.getHello()).isSameAs(collaborator);
    }

    @Test
    public void givenNothingClassToExecuteShouldReturnHello() {
        String greeting = "Hello!";
        Hello hello = mock(Hello.class);
        given(collaborator.get()).willReturn(hello);
        given(hello.greet()).willReturn(greeting);

        String result = cut.execute();

        assertThat(result).isEqualTo(greeting);
        verify(collaborator).get();
        verify(hello).greet();
        verifyNoMoreInteractions(collaborator);
    }

}
