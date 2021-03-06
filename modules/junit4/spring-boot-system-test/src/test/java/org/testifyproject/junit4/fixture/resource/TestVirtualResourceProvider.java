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
package org.testifyproject.junit4.fixture.resource;

import java.net.InetAddress;

import org.testifyproject.TestContext;
import org.testifyproject.VirtualResourceInstance;
import org.testifyproject.VirtualResourceProvider;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.VirtualResource;
import org.testifyproject.core.VirtualResourceInstanceBuilder;
import org.testifyproject.guava.common.net.InetAddresses;
import org.testifyproject.trait.PropertiesReader;

/**
 * An implementation of VirtualResourceProvider that provides a test virtual resource.
 *
 * @author saden
 */
@Discoverable
public class TestVirtualResourceProvider implements VirtualResourceProvider<Void> {

    @Override
    public Void configure(TestContext testContext, VirtualResource virtualResource,
            PropertiesReader configReader) {
        return null;
    }

    @Override
    public VirtualResourceInstance start(TestContext testContext,
            VirtualResource virtualResource, Void configuration) {
        return VirtualResourceInstanceBuilder.builder()
                .resource(InetAddresses.forString("127.0.0.1"), InetAddress.class)
                .build("virtual.test.resource", virtualResource);
    }

    @Override
    public void stop(TestContext testContext, VirtualResource virtualResource,
            VirtualResourceInstance instance) {
    }

}
