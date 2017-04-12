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
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.annotation.Application;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.WiringVerifier;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.TestConfigurer;

/**
 * A class used to run a system test.
 *
 * @author saden
 */
@SystemTest
@Discoverable
public class SystemTestRunner implements TestRunner {

    private TestContext testContext;

    private ServerProvider serverProvider;
    private TestResourcesProvider testResourcesProvider;
    private ServiceInstance serviceInstance;
    private ClientProvider clientProvider;

    @Override
    public void start(TestContext testContext) {
        this.testContext = testContext;
        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        Optional<Application> foundApplication = testDescriptor.getApplication();
        Optional<CutDescriptor> foundCutDescriptor = testContext.getCutDescriptor();

        if (foundApplication.isPresent()) {
            Application application = foundApplication.get();
            Object testInstance = testContext.getTestInstance();

            //create and initalize mock fields. this is necessary so we can configure
            //expected interaction prior to making a call to the application
            //endpoints
            ServiceLocatorUtil.INSTANCE.findAllWithFilter(FieldReifier.class, SystemTest.class)
                    .forEach(p -> p.reify(testContext));

            ServiceLocatorUtil.INSTANCE.findAllWithFilter(ConfigurationVerifier.class, SystemTest.class)
                    .forEach(p -> p.verify(testContext));

            //create server provider instance
            Class<? extends ServerProvider> serverProviderType = application.serverProvider();
            if (serverProviderType.equals(ServerProvider.class)) {
                serverProvider = ServiceLocatorUtil.INSTANCE.getOne(ServerProvider.class);
            } else {
                serverProvider = ReflectionUtil.INSTANCE.newInstance(serverProviderType);
            }

            //configure and start the server
            Object serverConfig = serverProvider.configure(testContext);
            serverConfig = testConfigurer.configure(testContext, serverConfig);
            ServerInstance serverInstance = serverProvider.start(serverConfig);

            Optional<ServiceInstance> foundServiceInstance = testContext.findProperty(SERVICE_INSTANCE);

            if (foundServiceInstance.isPresent()) {
                //get the service instance that became available after the server started
                serviceInstance = foundServiceInstance.get();

                //add constants to the service instance to make them available for injection
                serviceInstance.addConstant(testContext, null, TestContext.class);

                createApplicationServer(serverInstance, application);
                createApplicationClient(application, serverInstance, testContext, testConfigurer);

                //reifiy the test class
                testResourcesProvider = ServiceLocatorUtil.INSTANCE.getOne(TestResourcesProvider.class);
                testResourcesProvider.start(testContext, serviceInstance);

                createClassUnderTest(foundCutDescriptor, application, testInstance);

                ServiceLocatorUtil.INSTANCE.findAllWithFilter(org.testifyproject.extension.TestReifier.class, SystemTest.class)
                        .forEach(p -> p.reify(testContext));

                ServiceLocatorUtil.INSTANCE.findAllWithFilter(WiringVerifier.class, SystemTest.class)
                        .forEach(p -> p.verify(testContext));

            }
        }
    }

    void createClassUnderTest(Optional<CutDescriptor> foundCutDescriptor, Application application, Object testInstance) {
        foundCutDescriptor.ifPresent(cutDescriptor -> {
            Class cutType = cutDescriptor.getType();
            Object cutInstance;

            if (ClientInstance.class.isAssignableFrom(cutType)) {
                cutInstance = serviceInstance.getService(cutType);
            } else {
                cutInstance = serviceInstance.getService(cutType, application.clientName());
            }

            cutDescriptor.setValue(testInstance, cutInstance);
        });
    }

    void createApplicationServer(ServerInstance serverInstance, Application application) {
        //add the client instance itself to the dependency injection service
        String serverInstanceName = serverProvider.getClass().getSimpleName();
        Class serverInstanceContract = ServerInstance.class;
        serviceInstance.addConstant(serverInstance, serverInstanceName, serverInstanceContract);

        //add the underlying server instance to the dependency injection service
        serviceInstance.replace(serverInstance, application.serverName(), application.serverContract());
    }

    void createApplicationClient(Application application,
            ServerInstance serverInstance,
            TestContext testContext,
            TestConfigurer testConfigurer) {
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
        clientConfig = testConfigurer.configure(testContext, clientConfig);
        ClientInstance clientInstance = clientProvider.create(testContext, baseURI, clientConfig);
        //add the client instance itself to the dependency injection service
        String clientInstanceName = clientProvider.getClass().getSimpleName();
        Class clientInstanceContract = ClientInstance.class;
        serviceInstance.addConstant(clientInstance, clientInstanceName, clientInstanceContract);
        //add the underlying client instance to the dependency injection service
        serviceInstance.replace(clientInstance, application.clientName(), application.clientContract());
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

        if (testResourcesProvider != null) {
            testResourcesProvider.destroy(testContext, serviceInstance);
        }
    }

}
