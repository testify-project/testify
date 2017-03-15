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
package org.testifyproject.level.system;

import java.net.URI;
import java.util.Optional;
import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.CutDescriptor;
import org.testifyproject.ReificationProvider;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestReifier;
import org.testifyproject.TestRunner;
import org.testifyproject.annotation.Application;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.trait.LoggingTrait;
import org.testifyproject.MockProvider;

/**
 * A class used to run a system test.
 *
 * @author saden
 */
@Discoverable
public class SystemTestRunner implements TestRunner, LoggingTrait {

    private TestContext testContext;

    private ServerProvider serverProvider;
    private ReificationProvider reificationProvider;
    private ServiceInstance serviceInstance;
    private ClientProvider clientProvider;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;
        TestReifier testReifier = testContext.getTestReifier();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        MockProvider mockProvider = testContext.getMockProvider();
        Object testInstance = testContext.getTestInstance();
        Application application = testDescriptor.getApplication().get();

        SystemTestVerifier verifier = new SystemTestVerifier(testContext);
        verifier.dependency();

        //create and initalize mock fields. this is necessary so we can configure
        //expected interaction prior to making a call to the application
        //endpoints
        testDescriptor.getFieldDescriptors()
                .parallelStream()
                .filter(p -> p.getFake().isPresent())
                .forEach(fieldDescriptor -> {
                    Optional<Object> result = fieldDescriptor.getValue(testInstance);
                    Object value;

                    if (result.isPresent()) {
                        value = result.get();

                        //if a value is aready present and is not a mock instance
                        //then create a virtual mock.
                        if (!mockProvider.isMock(value)) {
                            value = mockProvider.createVirtual(fieldDescriptor.getType(), value);
                        }
                    } else {
                        value = mockProvider.createFake(fieldDescriptor.getType());
                    }

                    fieldDescriptor.setValue(testInstance, value);
                });

        verifier.configuration();

        //create server provider instance
        Class<? extends ServerProvider> serverProviderType = application.serverProvider();
        if (serverProviderType.equals(ServerProvider.class)) {
            serverProvider = ServiceLocatorUtil.INSTANCE.getOne(ServerProvider.class);
        } else {
            serverProvider = ReflectionUtil.INSTANCE.newInstance(serverProviderType);
        }

        //configure and start the server
        Object serverConfig = serverProvider.configure(testContext);
        serverConfig = testReifier.configure(testContext, serverConfig);
        ServerInstance serverInstance = serverProvider.start(serverConfig);

        //get the service instance that became available after the server started
        serviceInstance = testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE).get();
        //add constants to the service instance to make them available for injection
        serviceInstance.addConstant(testContext, null, TestContext.class);

        //add the client instance itself to the dependency injection service
        String serverInstanceName = serverProvider.getClass().getSimpleName();
        Class serverInstanceContract = ServerInstance.class;
        serviceInstance.addConstant(serverInstance, serverInstanceName, serverInstanceContract);

        //add the underlying server instance to the dependency injection service
        serviceInstance.replace(serverInstance, application.serverName(), application.serverContract());

        //create client provider instance
        Class<? extends ClientProvider> clientProviderType = application.clientProvider();
        if (clientProviderType.equals(ClientProvider.class)) {
            clientProvider = ServiceLocatorUtil.INSTANCE.getOne(ClientProvider.class);
        } else {
            clientProvider = ReflectionUtil.INSTANCE.newInstance(clientProviderType);
        }

        //inject the client provider with services
        serviceInstance.inject(clientProvider);

        //configure and create the client
        URI baseURI = serverInstance.getBaseURI();
        Object clientConfig = clientProvider.configure(testContext, baseURI);
        clientConfig = testReifier.configure(testContext, clientConfig);
        ClientInstance clientInstance = clientProvider.create(testContext, baseURI, clientConfig);

        //add the client instance itself to the dependency injection service
        String clientInstanceName = clientProvider.getClass().getSimpleName();
        Class clientInstanceContract = ClientInstance.class;
        serviceInstance.addConstant(clientInstance, clientInstanceName, clientInstanceContract);

        //add the underlying client instance to the dependency injection service
        serviceInstance.replace(clientInstance, application.clientName(), application.clientContract());

        //reifiy the test class
        reificationProvider = ServiceLocatorUtil.INSTANCE.getOne(ReificationProvider.class);
        reificationProvider.start(testContext, serviceInstance);

        verifier.wiring();

        //invoke init method on test fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.init(testInstance));

        //invoke init method on cut field annotated with Fixture
        testContext.getCutDescriptor()
                .ifPresent(p -> p.init(testInstance));
    }

    void addConstant(ServiceInstance serviceInstance,
            Object constant,
            Optional<String> name,
            Optional<Class> contract) {

        if (name.isPresent() && contract.isPresent()) {
            serviceInstance.addConstant(
                    constant,
                    name.get(),
                    contract.get());
        } else if (contract.isPresent()) {
            serviceInstance.addConstant(
                    constant,
                    null,
                    contract.get());
        } else if (name.isPresent()) {
            serviceInstance.addConstant(
                    constant,
                    name.get(),
                    null);
        }
    }

    @Override
    public void stop() {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<CutDescriptor> cutDescriptor = testContext.getCutDescriptor();

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on cut field annotated with Fixture
        cutDescriptor.ifPresent(p -> p.destroy(testInstance));

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
