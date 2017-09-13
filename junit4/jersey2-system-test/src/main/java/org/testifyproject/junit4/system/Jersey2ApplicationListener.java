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

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import static org.glassfish.jersey.server.monitoring.ApplicationEvent.Type.INITIALIZATION_FINISHED;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.testifyproject.ServiceInstance;
import org.testifyproject.StartStrategy;
import org.testifyproject.core.TestContextHolder;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.InstanceProvider;
import org.testifyproject.extension.PreInstanceProvider;

/**
 * A Jersey 2 application event listeners that listens for application events to
 * initialize, start and stop test resources.
 *
 * @author saden
 */
public class Jersey2ApplicationListener implements ApplicationEventListener {

    private final TestContextHolder testContextHolder;

    Jersey2ApplicationListener(TestContextHolder testContextHolder) {
        this.testContextHolder = testContextHolder;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        testContextHolder.execute(testContext -> {
            ApplicationEvent.Type eventType = event.getType();

            switch (eventType) {
                case INITIALIZATION_FINISHED:
                    LoggingUtil.INSTANCE.setTextContext(testContext);
                    if (testContext.getResourceStartStrategy() == StartStrategy.LAZY) {
                        testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)
                                .ifPresent(serviceInstance -> {
                                    //add constant instances
                                    ServiceLocatorUtil.INSTANCE.findAllWithFilter(PreInstanceProvider.class)
                                            .stream()
                                            .flatMap(p -> p.get(testContext).stream())
                                            .forEach(serviceInstance::replace);

                                    ServiceLocatorUtil.INSTANCE.findAllWithFilter(InstanceProvider.class)
                                            .stream()
                                            .flatMap(p -> p.get(testContext).stream())
                                            .forEach(serviceInstance::replace);
                                });
                    }
                    break;
            }
        });
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }

}
