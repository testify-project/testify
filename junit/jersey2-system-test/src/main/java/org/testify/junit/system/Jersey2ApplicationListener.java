/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.junit.system;

import java.util.List;
import java.util.Optional;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.testify.RequiresProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import static org.testify.core.impl.TestContextProperties.SERVICE_INSTANCE;
import org.testify.core.util.ServiceLocatorUtil;

/**
 * A Jersey 2 application event listeners that listens for application events to
 * initialize, start and stop test requires.
 *
 * @author saden
 */
public class Jersey2ApplicationListener implements ApplicationEventListener {

    private final InheritableThreadLocal<TestContext> localTestContext;
    private List<RequiresProvider> requiresProviders;

    Jersey2ApplicationListener(InheritableThreadLocal<TestContext> localTestContext) {
        this.localTestContext = localTestContext;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        TestContext testContext = localTestContext.get();

        switch (event.getType()) {
            case INITIALIZATION_FINISHED:
                Optional<ServiceInstance> optServiceInstance = testContext.getProperty(SERVICE_INSTANCE);
                ServiceInstance serviceInstance = optServiceInstance.get();

                if (!testContext.getStartResources()) {
                    requiresProviders = ServiceLocatorUtil.INSTANCE.findAll(RequiresProvider.class);
                    requiresProviders.forEach(p -> p.start(testContext, serviceInstance));
                }
                break;
            case DESTROY_FINISHED:
                if (!testContext.getStartResources()) {
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
