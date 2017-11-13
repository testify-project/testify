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
package org.testifyproject.server.generic;

import static org.testifyproject.core.TestContextProperties.SERVER;
import static org.testifyproject.core.TestContextProperties.SERVER_BASE_URI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testifyproject.CleanupProvider;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.core.ServerInstanceBuilder;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;

/**
 * A default implementation of {@link ServerProvider} contract. This implementation takes the
 * {@link Application#value()}, determine if it has a main function and if it does calls it to
 * start the application.
 *
 * @author saden
 */
@Discoverable
public class GenericServerProvider implements ServerProvider<String[], Object> {

    @Override
    public String[] configure(TestContext testContext) {
        return new String[]{};
    }

    @Override
    public ServerInstance<Object> start(TestContext testContext, Application application,
            String[] args) throws Exception {
        ServerInstanceBuilder builder = ServerInstanceBuilder.builder();

        try {
            Class[] methodArgsType = new Class[]{String[].class};
            Object[] methodArgs = new Object[]{args};
            Method method = application.value().getDeclaredMethod("main",
                    methodArgsType);
            method.invoke(null, (Object[]) methodArgs);

            return builder
                    .baseURI(testContext.getProperty(SERVER_BASE_URI))
                    .server(testContext.getProperty(SERVER))
                    .build("genericServer", application);
        } catch (IllegalAccessException |
                IllegalArgumentException |
                NoSuchMethodException |
                SecurityException |
                InvocationTargetException e) {
            throw ExceptionUtil.INSTANCE.propagate(e);
        }
    }

    @Override
    public void stop(ServerInstance<Object> serverInstance) throws Exception {
        Class<? extends CleanupProvider> cleanupProviderType =
                serverInstance.getApplication().cleanupProvider();

        CleanupProvider cleanupProvider;

        if (CleanupProvider.class.equals(cleanupProviderType)) {
            cleanupProvider = ServiceLocatorUtil.INSTANCE.findOne(CleanupProvider.class)
                    .orElse(null);
        } else {
            cleanupProvider = ReflectionUtil.INSTANCE.newInstance(cleanupProviderType);
        }

        if (cleanupProvider == null) {
            LoggingUtil.INSTANCE.warn(
                    "Application cleanupProvider not specified. Please provide an "
                    + "implementation of CleanupProvider that can be used to stop and cleanup "
                    + "after the generic server.");
        } else {
            cleanupProvider.cleanup(serverInstance);
        }
    }

    @Override
    public Class<Object> getServerType() {
        return null;
    }

}
