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

import static org.testifyproject.core.TestContextProperties.SERVER;
import static org.testifyproject.core.TestContextProperties.SERVER_BASE_URI;

import java.lang.reflect.Method;

import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;

/**
 * A default implementation of {@link ServerProvider} contract. This implementation takes the
 * {@link Application#value()}, determine if it has a main function and if it does calls it to
 * start the application.
 *
 * @author saden
 */
public class DefaultServerProvider implements ServerProvider<String[], Object> {

    private Class<?> appType;
    private Object appInstance;

    @Override
    public String[] configure(TestContext testContext) {
        return new String[]{};
    }

    @Override
    public ServerInstance<Object> start(TestContext testContext,
            Application application,
            String[] args) throws Exception {
        try {
            ServerInstanceBuilder builder = ServerInstanceBuilder.builder();
            appType = application.value();
            String startMethodName = application.start();

            if (startMethodName.equals("main")) {
                //if we are dealing with a static main method then invoke
                Object[] startArgs = new Object[]{args};
                Method method = appType.getMethod(startMethodName, String[].class);

                ReflectionUtil.INSTANCE.invoke(method, null, startArgs);
            } else {
                //otherwise we are dealing with non static method therefore create an instance
                //of the application and invoke the start method
                appInstance = appType.newInstance();
                Method method = appType.getMethod(startMethodName);
                ReflectionUtil.INSTANCE.invoke(method, appInstance);
            }

            return builder
                    .baseURI(testContext.getProperty(SERVER_BASE_URI))
                    .server(testContext.getProperty(SERVER))
                    .build("genericServer", application);
        } catch (NoSuchMethodException | SecurityException e) {
            throw ExceptionUtil.INSTANCE.propagate(e);
        }
    }

    @Override
    public void stop(ServerInstance<Object> serverInstance) throws Exception {
        String stopMethodName = serverInstance.getApplication().stop();
        Method method = appType.getMethod(stopMethodName);

        if (appInstance == null) {
            ReflectionUtil.INSTANCE.invoke(method, null);
        } else {
            ReflectionUtil.INSTANCE.invoke(method, appInstance);
        }
    }

    @Override
    public Class<Object> getServerType() {
        return null;
    }

}
