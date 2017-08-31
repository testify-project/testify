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
package org.testifyproject.di.spring;

import java.util.List;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.testifyproject.ResourceProvider;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;

/**
 * A class that is called after the application context is refreshed to
 * initialize the test as well as start and stop test resources.
 *
 * @author saden
 */
public class SpringContextClosedListener implements ApplicationListener<ContextClosedEvent> {

    private final TestContext testContext;
    private final List<ResourceProvider> resourceProviders;

    public SpringContextClosedListener(TestContext testContext, List<ResourceProvider> resourceProviders) {
        this.testContext = testContext;
        this.resourceProviders = resourceProviders;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent e) {
        if (testContext.getResourceStartStrategy() == StartStrategy.LAZY) {
            resourceProviders.forEach(resourceProvider -> resourceProvider.stop(testContext));
        }
    }

}
