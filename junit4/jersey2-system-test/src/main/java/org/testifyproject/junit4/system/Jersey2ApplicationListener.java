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
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.MDC;
import org.testifyproject.RequiresProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.TestContext;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ServiceLocatorUtil;

/**
 * A Jersey 2 application event listeners that listens for application events to
 * initialize, start and stop test requires.
 *
 * @author saden
 */
public class Jersey2ApplicationListener implements ApplicationEventListener {

    private List<RequiresProvider> requiresProviders;
    private final TestContextHolder testContextHolder;

    Jersey2ApplicationListener(TestContextHolder testContextHolder) {
        this.testContextHolder = testContextHolder;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        TestContext testContext = testContextHolder.get().get();

        switch (event.getType()) {
            case INITIALIZATION_FINISHED:
                MDC.put("test", testContext.getTestName());
                MDC.put("method", testContext.getMethodName());

                Optional<ServiceInstance> optServiceInstance = testContext.findProperty(SERVICE_INSTANCE);
                ServiceInstance serviceInstance = optServiceInstance.get();

                if (testContext.getResourceStartStrategy() == StartStrategy.Lazy) {
                    requiresProviders = ServiceLocatorUtil.INSTANCE.findAll(RequiresProvider.class);
                    requiresProviders.forEach(p -> p.start(testContext, serviceInstance));
                }

                break;
            case DESTROY_FINISHED:
                if (testContext.getResourceStartStrategy() == StartStrategy.Lazy) {
                    requiresProviders.forEach(RequiresProvider::stop);
                }

                break;
            default:
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }

}
