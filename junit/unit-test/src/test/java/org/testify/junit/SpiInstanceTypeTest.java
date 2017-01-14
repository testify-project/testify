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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testify.annotation.Cut;
import org.testify.annotation.Fake;
import org.testify.junit.fixture.SpiInstanceType;
import org.testify.junit.fixture.collaborator.Hello;
import org.testify.junit.fixture.collaborator.World;

/**
 *
 * @author saden
 */
@RunWith(UnitTest.class)
public class SpiInstanceTypeTest {

    @Cut
    SpiInstanceType cut;

    @Fake
    Hello hello;

    @Fake
    Hello HELLO;

    @Fake
    World world;

    @Test
    public void verifyInjections() {
        assertThat(cut).isNotNull();
        assertThat(cut.getHello()).isNotNull().isSameAs(hello);
        assertThat(cut.getHELLO()).isNotNull().isSameAs(HELLO);
        assertThat(cut.getWorld()).isNotNull().isSameAs(world);

        assertThat(Mockito.mockingDetails(hello).isMock()).isTrue();
        assertThat(Mockito.mockingDetails(HELLO).isMock()).isTrue();
        assertThat(Mockito.mockingDetails(world).isMock()).isTrue();
    }

}
