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
package org.testifyproject.fixture.resource;

import static org.mockito.Mockito.mock;
import org.testifyproject.TestContext;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.tools.Discoverable;

/**
 *
 * @author saden
 */
@Discoverable
public class ValidVirtualResourceProvider implements VirtualResourceProvider<Void> {

    @Override
    public Void configure(TestContext testContext, VirtualResource virtualResource) {
        return null;
    }

    @Override
    public VirtualResourceInstance start(TestContext testContext, VirtualResource virtualResource, Void configuration) {
        return mock(VirtualResourceInstance.class);
    }

    @Override
    public void stop(TestContext testContext, VirtualResource virtualResource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
