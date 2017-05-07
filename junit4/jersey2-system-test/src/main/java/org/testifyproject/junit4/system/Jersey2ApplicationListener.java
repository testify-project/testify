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

import java.util.List;
import java.util.Optional;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import static org.glassfish.jersey.server.monitoring.ApplicationEvent.Type.DESTROY_FINISHED;
import static org.glassfish.jersey.server.monitoring.ApplicationEvent.Type.INITIALIZATION_FINISHED;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.testifyproject.ResourceProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;

/**
 * A Jersey 2 application event listeners that listens for application events to
 * initialize, start and stop test resources.
 *
 * @author saden
 */
public class Jersey2ApplicationListener implements ApplicationEventListener {

    private List<ResourceProvider> resourceProviders;
    private final TestContextHolder testContextHolder;

    Jersey2ApplicationListener(TestContextHolder testContextHolder) {
        this.testContextHolder = testContextHolder;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        testContextHolder.exesute(testContext -> {
            ApplicationEvent.Type eventType = event.getType();

            if (eventType == INITIALIZATION_FINISHED) {
                init(testContext);
            } else if (eventType == DESTROY_FINISHED) {
                destroy(testContext);
            }
        });
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }

    void init(TestContext testContext) {
        LoggingUtil.INSTANCE.setTextContext(testContext);

        Optional<ServiceInstance> foundInstance = testContext.findProperty(SERVICE_INSTANCE);
        foundInstance.ifPresent(serviceInstance -> {
            if (testContext.getResourceStartStrategy() == StartStrategy.LAZY) {
                resourceProviders = ServiceLocatorUtil.INSTANCE.findAll(ResourceProvider.class);
                resourceProviders.forEach(p -> p.start(testContext, serviceInstance));
            }
        });
    }

    void destroy(TestContext testContext) {
        if (testContext.getResourceStartStrategy() == StartStrategy.LAZY) {
            resourceProviders.forEach(resourceProvider -> resourceProvider.stop(testContext));
        }
    }

}
