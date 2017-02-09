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
package org.testify.level.system;

import org.testify.ClientInstance;
import org.testify.ClientProvider;
import org.testify.ReificationProvider;
import org.testify.ServerInstance;
import org.testify.ServerProvider;
import org.testify.ServiceInstance;
import org.testify.TestContext;
import org.testify.TestDescriptor;
import org.testify.TestReifier;
import org.testify.TestRunner;
import static org.testify.core.TestContextProperties.SERVICE_INSTANCE;
import org.testify.core.util.ServiceLocatorUtil;
import org.testify.tools.Discoverable;

/**
 * A class used to run a system test.
 *
 * @author saden
 */
@Discoverable
public class SystemTestRunner implements TestRunner {

    private TestContext testContext;

    private ServerProvider serverProvider;
    private ClientProvider clientProvider;
    private ReificationProvider reificationProvider;
    private ServiceInstance serviceInstance;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;
        TestReifier testReifier = testContext.getTestReifier();

        SystemTestVerifier verifier = new SystemTestVerifier(testContext);
        verifier.dependency();
        verifier.configuration();

        //configure and start the server
        serverProvider = ServiceLocatorUtil.INSTANCE.getOne(ServerProvider.class);
        Object serverConfig = serverProvider.configure(testContext);
        serverConfig = testReifier.configure(testContext, serverConfig);
        ServerInstance serverInstance = serverProvider.start(serverConfig);

        //create and initialize client
        clientProvider = ServiceLocatorUtil.INSTANCE.getOne(ClientProvider.class);
        Object clientConfig = clientProvider.configure(serverInstance);
        clientConfig = testReifier.configure(testContext, clientConfig);
        ClientInstance clientInstance = clientProvider.create(testContext, clientConfig);

        //add constants to the service instance provided by the server
        serviceInstance = testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE).get();
        serviceInstance.addConstant(testContext, null, TestContext.class);
        serviceInstance.addConstant(serverInstance, null, ServerInstance.class);
        serviceInstance.addConstant(clientInstance, null, ClientInstance.class);

        //reifiy the test class
        reificationProvider = ServiceLocatorUtil.INSTANCE.getOne(ReificationProvider.class);
        reificationProvider.start(testContext, serviceInstance);

        verifier.wiring();

        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();

        //invoke init method on test fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.init(testInstance));

        //invoke init method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.init(testInstance));
    }

    @Override
    public void stop() {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.destroy(testInstance));

        if (clientProvider != null) {
            clientProvider.destroy();
        }

        if (serverProvider != null) {
            serverProvider.stop();
        }

        if (reificationProvider != null) {
            reificationProvider.destroy(testContext, serviceInstance);
        }
    }

}
