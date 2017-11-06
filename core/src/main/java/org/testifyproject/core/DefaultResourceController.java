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

import org.testifyproject.ResourceController;
import org.testifyproject.ResourceProvider;
import org.testifyproject.TestContext;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of the ResourceController SPI contract.
 *
 * @author saden
 */
@Discoverable
public class DefaultResourceController implements ResourceController {

    private ServiceLocatorUtil serviceLocatorUtil;

    public DefaultResourceController() {
        this(ServiceLocatorUtil.INSTANCE);
    }

    DefaultResourceController(ServiceLocatorUtil serviceLocatorUtil) {
        this.serviceLocatorUtil = serviceLocatorUtil;
    }

    @Override
    public void start(TestContext testContext) {
        List<ResourceProvider> foundResourceProviders =
                serviceLocatorUtil.findAll(ResourceProvider.class);

        foundResourceProviders.parallelStream().forEach(resourceProvider -> {
            resourceProvider.start(testContext);
            testContext.addCollectionElement(TestContextProperties.RESOURCE_PROVIDERS,
                    resourceProvider);
        });
    }

    @Override
    public void stop(TestContext testContext) {
        testContext.<ResourceProvider>findCollection(TestContextProperties.RESOURCE_PROVIDERS)
                .forEach(resourceProvider -> resourceProvider.stop(testContext));
    }

}
