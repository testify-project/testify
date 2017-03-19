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
package org.testifyproject.fixture.container;

import static org.mockito.Mockito.mock;
import org.testifyproject.ContainerInstance;
import org.testifyproject.ContainerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.RequiresContainer;
import org.testifyproject.tools.Discoverable;

/**
 *
 * @author saden
 */
@Discoverable
public class TestContainerProvider implements ContainerProvider<RequiresContainer, Void> {

    @Override
    public Void configure(TestContext testContext) {
        return null;
    }

    @Override
    public ContainerInstance start(TestContext testContext, RequiresContainer requiredContainer, Void configuration) {
        return mock(ContainerInstance.class);
    }

    @Override
    public void stop() {
    }

}
