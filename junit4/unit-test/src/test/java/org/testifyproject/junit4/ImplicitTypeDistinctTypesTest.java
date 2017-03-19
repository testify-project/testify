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
package org.testifyproject.junit4;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Fake;
import org.testifyproject.junit4.fixture.ImplicitTypeDistinctTypes;
import org.testifyproject.junit4.fixture.collaborator.Hello;
import org.testifyproject.junit4.fixture.collaborator.World;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class ImplicitTypeDistinctTypesTest {

    @Cut
    ImplicitTypeDistinctTypes cut;

    @Fake
    Hello hello;

    @Fake
    World world;

    @Before
    public void verifyInjections() {
        assertThat(cut).isNotNull();
        assertThat(hello).isNotNull();
        assertThat(world).isNotNull();
        assertThat(cut.getHello()).isSameAs(hello);
        assertThat(cut.getWorld()).isSameAs(world);
    }

    @Test
    public void givenNothingClassToExecuteShouldReturnHello() {
        String helloGreeting = "Hello";
        String worldGreeting = "World!";
        given(hello.greet()).willReturn(helloGreeting);
        given(world.greet()).willReturn(worldGreeting);

        String result = cut.execute();

        assertThat(result).isEqualTo(helloGreeting + " " + worldGreeting);
        verify(hello).greet();
        verify(world).greet();
        verifyNoMoreInteractions(hello, world);
    }

}
