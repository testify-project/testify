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
package org.testify.junit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testify.annotation.Cut;
import org.testify.annotation.Real;
import org.testify.junit.fixture.DirectGreeter;
import org.testify.junit.fixture.common.impl.Hello;

/**
 *
 * @author saden
 */
@RunWith(HK2IntegrationTest.class)
public class IgnoreTestMethodIT {

    @Cut
    DirectGreeter cut;

    @Real
    Hello greeting;

    @Ignore
    @Test
    public void verifyInjection() {
        assertThat(cut).isNotNull();
        assertThat(greeting).isNotNull();
        assertThat(cut.getGreeting()).isNotNull()
                .isSameAs(greeting)
                .isInstanceOf(Hello.class);

        assertThat(Mockito.mockingDetails(greeting).isMock()).isFalse();
    }

}
