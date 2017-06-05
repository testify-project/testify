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

import java.net.InetAddress;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testifyproject.Instance;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Real;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.junit4.fixture.web.GreetingServletApplication;

/**
 *
 * @author saden
 */
@VirtualResource("test")
@Application(GreetingServletApplication.class)
@RunWith(SpringBootSystemTest.class)
public class GreetingResourceVirtualResourceST {

    @Real
    VirtualResourceInstance<InetAddress> instance;

    @Real
    InetAddress resource;

    @Test
    public void verifyInjections() {
        assertThat(instance).isNotNull();
        assertThat(resource).isNotNull();
        assertThat(instance.getFqn()).isEqualTo("test");

        Instance<InetAddress> resourceInstance = instance.getResource();
        assertThat(resourceInstance).isNotNull();
        assertThat(resourceInstance.getValue()).isEqualTo(resource);
    }
}
