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
package org.testifyproject.junit4.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Module;
import org.testifyproject.junit4.fixture.common.DirectGreeter;
import org.testifyproject.junit4.fixture.common.DirectGreeterCollaboratorProvider;
import org.testifyproject.junit4.fixture.common.GreeterConfig;
import org.testifyproject.junit4.fixture.common.impl.Hello;

/**
 *
 * @author saden
 */
@Module(GreeterConfig.class)
@RunWith(SpringIntegrationTest.class)
@CollaboratorProvider(DirectGreeterCollaboratorProvider.class)
public class CollaboratorProviderClassIT {

    @Cut
    DirectGreeter cut;

    @Test
    public void verifyInjection() {
        //XXX: the collaborator is provided by the @CollaboratorProvider annotation
        //on the test class and it is a mock instance.

        assertThat(cut).isNotNull();
        assertThat(cut.getGreeting())
                .isNotNull()
                .isInstanceOf(Hello.class);

        assertThat(Mockito.mockingDetails(cut.getGreeting()).isMock()).isTrue();
    }
}
