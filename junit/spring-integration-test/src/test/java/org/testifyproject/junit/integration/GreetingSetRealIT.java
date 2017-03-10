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
package org.testifyproject.junit.integration;

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.testifyproject.annotation.Cut;
import org.testifyproject.annotation.Module;
import org.testifyproject.annotation.Real;
import org.testifyproject.junit.fixture.common.GreeterConfig;
import org.testifyproject.junit.fixture.common.Greeting;
import org.testifyproject.junit.fixture.common.GreetingSet;

/**
 *
 * @author saden
 */
@Module(GreeterConfig.class)
@RunWith(SpringIntegrationTest.class)
public class GreetingSetRealIT {

    @Cut
    GreetingSet cut;

    @Real
    Set<Greeting> greetings;

    @Test
    public void verifyInjection() {
        assertThat(cut).isNotNull();
        assertThat(greetings).isNotEmpty().isSameAs(cut.getGreetings());
        assertThat(Mockito.mockingDetails(greetings).isMock()).isFalse();
    }
}
