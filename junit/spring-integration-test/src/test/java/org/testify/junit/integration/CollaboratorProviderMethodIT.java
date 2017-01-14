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

import org.testify.annotation.CollaboratorProvider;
import org.testify.annotation.Cut;
import org.testify.annotation.Module;
import org.testify.annotation.Real;
import org.testify.junit.fixture.common.DirectGreeter;
import org.testify.junit.fixture.common.GreeterConfig;
import org.testify.junit.fixture.common.impl.Hello;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

/**
 *
 * @author saden
 */
@Module(GreeterConfig.class)
@RunWith(SpringIntegrationTest.class)
public class CollaboratorProviderMethodIT {

    @Cut
    DirectGreeter cut;

    @Real
    Hello greeting;

    boolean configured = false;

    @CollaboratorProvider
    Object[] collaborators() {
        configured = true;
        return new Object[]{
            mock(Hello.class)
        };
    }

    @Test
    public void verifyInjection() {
        assertThat(cut).isNotNull();
        assertThat(cut.getGreeting())
                .isNotNull()
                .isInstanceOf(Hello.class);

        assertThat(Mockito.mockingDetails(cut.getGreeting()).isMock()).isTrue();
        assertThat(configured).isTrue();
    }
}
