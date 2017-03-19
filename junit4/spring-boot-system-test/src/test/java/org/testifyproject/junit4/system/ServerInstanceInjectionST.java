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
package org.testifyproject.junit4.system;

import javax.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.ServerInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Real;
import org.testifyproject.junit4.fixture.servlet.GreeterServletApplication;

/**
 *
 * @author saden
 */
@RunWith(SpringBootSystemTest.class)
@Application(GreeterServletApplication.class)
public class ServerInstanceInjectionST {

    @Inject
    ServerInstance instance1;

    @Real
    ServerInstance instance2;

    @Test
    public void verifyInjections() {
        assertThat(instance1).isNotNull();
        assertThat(instance2).isNotNull();
    }

}
