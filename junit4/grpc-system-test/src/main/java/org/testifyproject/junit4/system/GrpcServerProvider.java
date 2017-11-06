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

import static org.testifyproject.core.TestContextProperties.APP_BASE_URI;
import static org.testifyproject.core.TestContextProperties.APP_SERVER;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.ServerInstanceBuilder;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.tools.Discoverable;

import io.grpc.Server;

/**
 * A default implementation of {@link ServerProvider} contract. This implementation takes the
 * {@link Application#value()}, determine if it has a main function and if it does calls it to
 * start the application.
 *
 * @author saden
 */
@Discoverable
public class GrpcServerProvider implements ServerProvider<String[], Server> {

    @Override
    public String[] configure(TestContext testContext) {
        return new String[]{};
    }

    @Override
    public ServerInstance<Server> start(TestContext testContext, Application application,
            String[] args) throws Exception {
        ServerInstanceBuilder builder = ServerInstanceBuilder.builder();

        try {
            Class[] methodArgsType = new Class[]{String[].class};
            Object[] methodArgs = new Object[]{args};
            Method method = application.value().getDeclaredMethod("main",
                    methodArgsType);
            method.invoke(null, (Object[]) methodArgs);

            builder.baseURI(testContext.getProperty(APP_BASE_URI))
                    .server(testContext.getProperty(APP_SERVER));
        } catch (IllegalAccessException |
                IllegalArgumentException |
                NoSuchMethodException |
                SecurityException |
                InvocationTargetException e) {
            throw ExceptionUtil.INSTANCE.propagate(e);
        }

        return builder.build("grpc", application);

    }

    @Override
    public void stop(ServerInstance<Server> serverInstance) throws Exception {
        serverInstance.execute((server, baseURI) -> {
            server.shutdownNow();
        });
    }

    @Override
    public Class<Server> getServerType() {
        return Server.class;
    }

}
