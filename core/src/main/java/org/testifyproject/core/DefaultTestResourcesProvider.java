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
package org.testifyproject.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.testifyproject.ResourceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of the TestResourcesProvider SPI contract.
 *
 * @author saden
 */
@Discoverable
public class DefaultTestResourcesProvider implements TestResourcesProvider {

    private ServiceLocatorUtil serviceLocatorUtil;
    private Queue<ResourceProvider> resourceProviders;

    public DefaultTestResourcesProvider() {
        this(ServiceLocatorUtil.INSTANCE, new ConcurrentLinkedQueue<>());
    }

    DefaultTestResourcesProvider(ServiceLocatorUtil serviceLocatorUtil,
            Queue<ResourceProvider> resourceProviders) {
        this.resourceProviders = resourceProviders;
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext) {
        List<ResourceProvider> foundResourceProviders = serviceLocatorUtil.findAll(ResourceProvider.class);

        foundResourceProviders.parallelStream().forEach(resourceProvider -> {
            resourceProvider.start(testContext);
            resourceProviders.add(resourceProvider);
        });
    }

    @Override
    public void stop(TestContext testContext) {
        resourceProviders.forEach(resourceProvider -> resourceProvider.stop(testContext));
    }

}
