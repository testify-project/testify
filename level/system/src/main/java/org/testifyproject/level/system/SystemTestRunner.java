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
import org.testifyproject.Instance;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.TestRunner;
import org.testifyproject.annotation.Application;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.tools.Discoverable;
import org.testifyproject.extension.annotation.SystemCategory;

/**
 * A class used to run a system test.
 *
 * @author saden
 */
@SystemCategory
@Discoverable
public class SystemTestRunner implements TestRunner {

    TestResourcesProvider testResourcesProvider;
    ServerProvider serverProvider;
    ClientProvider clientProvider;
    ServerInstance serverInstance;

    private final ServiceLocatorUtil serviceLocatorUtil;
    private final ReflectionUtil reflectionUtil;

    public SystemTestRunner() {
        this(ServiceLocatorUtil.INSTANCE, ReflectionUtil.INSTANCE);
    }

    SystemTestRunner(ServiceLocatorUtil serviceLocatorUtil, ReflectionUtil reflectionUtil) {
        this.serviceLocatorUtil = serviceLocatorUtil;
        this.reflectionUtil = reflectionUtil;
    }

    @Override
    public void start(TestContext testContext) {
        TestConfigurer testConfigurer = testContext.getTestConfigurer();
        TestDescriptor testDescriptor = testContext.getTestDescriptor();

        Optional<Application> foundApplication = testDescriptor.getApplication();

        if (foundApplication.isPresent()) {
            Application application = foundApplication.get();
            Object testInstance = testContext.getTestInstance();

            //create and initalize mock fields. this is necessary so we can configure
            //expected interaction prior to making a call to the application
            //endpoints
            serviceLocatorUtil.findAllWithFilter(FieldReifier.class, SystemCategory.class)
                    .forEach(p -> p.reify(testContext));

            serviceLocatorUtil.findAllWithFilter(PreVerifier.class, SystemCategory.class)
                    .forEach(p -> p.verify(testContext));

            //create server provider instance
            Class<? extends ServerProvider> serverProviderType = application.serverProvider();
            if (serverProviderType.equals(ServerProvider.class)) {
                serverProvider = serviceLocatorUtil.getOne(ServerProvider.class);
            } else {
                serverProvider = reflectionUtil.newInstance(serverProviderType);
            }

            //configure and start the server
            Object serverConfig = serverProvider.configure(testContext);
            serverConfig = testConfigurer.configure(testContext, serverConfig);
            serverInstance = serverProvider.start(testContext, serverConfig);

            Optional<ServiceInstance> foundServiceInstance = testContext.findProperty(SERVICE_INSTANCE);

            foundServiceInstance.ifPresent(serviceInstance -> {
                //add constants to the service instance to make them available for injection
                serviceInstance.addConstant(testContext, null, TestContext.class);

                addServer(serviceInstance, application, serverInstance);
                addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);
                //add server instance properties to the test context
                testContext.addProperty(serverInstance.getFqn(), serverInstance.getProperties());
                //reifiy the test class
                testResourcesProvider = serviceLocatorUtil.getOne(TestResourcesProvider.class);
                testResourcesProvider.start(testContext, serviceInstance);

                //XXX: Some DI framework (i.e. Spring) require that the service instance
                //context be initialized. We need to do the initialization after the
                //required resources have started so that resources can dynamically
                //added to the service instance and eligiable for injection into the
                //test class and test fixtures.
                serviceInstance.init();

                testContext.getSutDescriptor().ifPresent(sutDescriptor
                        -> createClassUnderTest(sutDescriptor, application, serviceInstance, testInstance)
                );

                serviceLocatorUtil.findAllWithFilter(FinalReifier.class, SystemCategory.class)
                        .forEach(p -> p.reify(testContext));

                serviceLocatorUtil.findAllWithFilter(PreiVerifier.class, SystemCategory.class)
                        .forEach(p -> p.verify(testContext));
            });
        }
    }

    @Override
    public void stop(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<SutDescriptor> sutDescriptor = testContext.getSutDescriptor();

        serviceLocatorUtil.findAllWithFilter(PostVerifier.class, SystemCategory.class)
                .forEach(p -> p.verify(testContext));

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        sutDescriptor.ifPresent(p -> p.destroy(testInstance));

        if (clientProvider != null) {
            clientProvider.destroy();
        }

        if (serverProvider != null) {
            serverProvider.stop(testContext, serverInstance);
        }

        if (testResourcesProvider != null) {
            ServiceInstance serviceInstance = testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)
                    .orElse(null);

            testResourcesProvider.stop(testContext, serviceInstance);
        }
    }

    void createClassUnderTest(SutDescriptor sutDescriptor,
            Application application,
            ServiceInstance serviceInstance,
            Object testInstance) {
        Class sutType = sutDescriptor.getType();
        Object sutValue;

        if (ClientInstance.class.isAssignableFrom(sutType)) {
            sutValue = serviceInstance.getService(sutType);
        } else {
            sutValue = serviceInstance.getService(sutType, application.clientName());
        }

        sutDescriptor.setValue(testInstance, sutValue);
    }

    void addServer(ServiceInstance serviceInstance,
            Application application,
            ServerInstance serverInstance) {
        //add the client instance itself to the dependency injection service
        String serverInstanceName = serverProvider.getClass().getSimpleName();
        Class serverInstanceContract = ServerInstance.class;
        Instance server = serverInstance.getServer();

        //add the underlying server instance to the dependency injection service
        serviceInstance.addConstant(serverInstance, serverInstanceName, serverInstanceContract);
        serviceInstance.replace(server, application.serverName(), application.serverContract());
    }

    void addClient(ServiceInstance serviceInstance,
            Application application,
            ServerInstance serverInstance,
            TestContext testContext,
            TestConfigurer testConfigurer) {
        //create client provider instance
        Class<? extends ClientProvider> clientProviderType = application.clientProvider();

        if (clientProviderType.equals(ClientProvider.class)) {
            clientProvider = serviceLocatorUtil.getOne(ClientProvider.class);
        } else {
            clientProvider = reflectionUtil.newInstance(clientProviderType);
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
}
