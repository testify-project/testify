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
package org.testifyproject.core.instance;

import java.util.List;
import java.util.function.Supplier;

import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.extension.instrument.DefaultProxyInstance;
import org.testifyproject.extension.ProxyInstance;
import org.testifyproject.extension.ProxyInstanceProvider;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.tools.Discoverable;

/**
 * An implementation of PreInstanceProvider that provides the test context.
 *
 * @author saden
 */
@SystemCategory
@Discoverable
public class ServerProxyInstanceProvider implements ProxyInstanceProvider {

    @Override
    public List<ProxyInstance> get(TestContext testContext) {
        ImmutableList.Builder<ProxyInstance> builder = ImmutableList.builder();

        testContext.<ServerProvider>findProperty(TestContextProperties.APP_SERVER_PROVIDER)
                .ifPresent(serverProvider -> {
                    builder.add(createServerInstance(testContext));
                    builder.add(createServer(testContext, serverProvider.getServerType()));
                });

        return builder.build();
    }

    ProxyInstance createServerInstance(TestContext testContext) {
        Supplier<ServerInstance> supplier =
                () -> testContext.getProperty(TestContextProperties.APP_SERVER_INSTANCE);

        return DefaultProxyInstance.of(ServerInstance.class, supplier);
    }

    ProxyInstance createServer(TestContext testContext, Class serverType) {
        Supplier<?> supplier = () ->
                testContext.getProperty(TestContextProperties.APP_SERVER);

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        String name = testDescriptor.getApplication()
                .map(Application::serverName)
                .orElse(null);

        return DefaultProxyInstance.of(serverType, name, supplier);
    }

}
