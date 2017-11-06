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

import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.net.URI;
import java.util.Optional;
import java.util.function.Predicate;

import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.Instance;
import org.testifyproject.ResourceController;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.ServiceProvider;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestRunner;
import org.testifyproject.annotation.Application;
import org.testifyproject.core.DefaultServiceProvider;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.LoggingUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.extension.annotation.Hint;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.tools.Discoverable;

/**
 * A class used to run a system test.
 *
 * @author saden
 */
@SystemCategory
@Discoverable
public class SystemTestRunner implements TestRunner {

    ResourceController resourceController;

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
        Optional<SutDescriptor> foundSutDescriptor = testContext.getSutDescriptor();
        Object testInstance = testContext.getTestInstance();

        testDescriptor.getApplication().ifPresent(application -> {
            try {
                //create and initalize mock fields. this is necessary so we can configure
                //expected interaction prior to making a call to the application
                //endpoints
                serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class,
                        SystemCategory.class)
                        .forEach(p -> p.reify(testContext));

                serviceLocatorUtil.findAllWithFilter(PreVerifier.class, testDescriptor
                        .getGuidelines(), SystemCategory.class)
                        .forEach(p -> p.verify(testContext));

                resourceController = serviceLocatorUtil.getOne(ResourceController.class);
                resourceController.start(testContext);

                ClientProvider clientProvider = createClientProvider(testContext, application);
                ServerProvider serverProvider = createServerProvider(testContext, application);

                createServer(testContext, testConfigurer, serverProvider, application);
                createClient(testContext, testConfigurer, clientProvider, application);
            } catch (Exception ex) {
                throw ExceptionUtil.INSTANCE.propagate(ex);
            }

            ServiceInstance serviceInstance = createService(testContext, testDescriptor,
                    testConfigurer);

            if (serviceInstance != null) {
                if (foundSutDescriptor.isPresent()) {
                    SutDescriptor sutDescriptor = foundSutDescriptor.get();
                    createSut(testContext, sutDescriptor, serviceInstance, testInstance);
                }

                serviceLocatorUtil.findAllWithFilter(
                        FinalReifier.class,
                        SystemCategory.class
                ).forEach(p -> p.reify(testContext));

                serviceLocatorUtil.findAllWithFilter(
                        PreiVerifier.class,
                        testDescriptor.getGuidelines(),
                        SystemCategory.class
                ).forEach(p -> p.verify(testContext));
            }
        });
    }

    @Override
    public void stop(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<SutDescriptor> sutDescriptor = testContext.getSutDescriptor();

        serviceLocatorUtil.findAllWithFilter(PostVerifier.class, testDescriptor
                .getGuidelines(), SystemCategory.class)
                .forEach(p -> p.verify(testContext));

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        sutDescriptor.ifPresent(p -> p.destroy(testInstance));

        try {
            Optional<ClientInstance> foundClientInstance =
                    testContext.findProperty(TestContextProperties.APP_CLIENT_INSTANCE);

            if (foundClientInstance.isPresent()) {
                ClientInstance clientInstance = foundClientInstance.get();

                Optional<ClientProvider> foundClientProvider =
                        testContext.findProperty(TestContextProperties.APP_CLIENT_PROVIDER);

                if (foundClientProvider.isPresent()) {
                    ClientProvider clientProvider = foundClientProvider.get();
                    clientProvider.destroy(clientInstance);
                }
            }

            //XXX: because an exception is thrown by stop we have to catch it and propogate it
            //this way server prvoider impl don't have to worry about handling exceptions
            Optional<ServerInstance> foundServerInstance =
                    testContext.<ServerInstance>findProperty(
                            TestContextProperties.APP_SERVER_INSTANCE);

            if (foundServerInstance.isPresent()) {
                ServerInstance serverInstance = foundServerInstance.get();
                Optional<ServerProvider> foundServerProvider =
                        testContext.findProperty(TestContextProperties.APP_SERVER_PROVIDER);

                if (foundServerProvider.isPresent()) {
                    ServerProvider serverProvider = foundServerProvider.get();
                    serverProvider.stop(serverInstance);
                }
            }

            resourceController.stop(testContext);
        } catch (Exception ex) {
            throw ExceptionUtil.INSTANCE.propagate(ex);
        }
    }

    ServerProvider createServerProvider(TestContext testContext, Application application) {
        Class<? extends ServerProvider> type = application.serverProvider();
        ServerProvider serverProvider;
        if (ServerProvider.class.equals(type)) {
            serverProvider = serviceLocatorUtil.getOne(type);
        } else {
            serverProvider = reflectionUtil.newInstance(type);
        }
        testContext.addProperty(TestContextProperties.APP_SERVER_PROVIDER,
                serverProvider);
        return serverProvider;
    }

    ClientProvider createClientProvider(TestContext testContext, Application application) {
        //create client supplier instance
        Class<? extends ClientProvider> clientProviderType =
                application.clientProvider();
        ClientProvider clientProvider;
        if (clientProviderType.equals(ClientProvider.class)) {
            clientProvider = serviceLocatorUtil.getOne(ClientProvider.class);
        } else {
            clientProvider = reflectionUtil.newInstance(clientProviderType);
        }
        testContext.addProperty(TestContextProperties.APP_CLIENT_PROVIDER,
                clientProvider);
        return clientProvider;
    }

    ServiceInstance createService(TestContext testContext, TestDescriptor testDescriptor,
            TestConfigurer testConfigurer) {
        ServiceInstance serviceInstance;
        Optional<ServiceInstance> foundServiceInstance =
                testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE);
        if (foundServiceInstance.isPresent()) {
            serviceInstance = foundServiceInstance.get();
        } else {
            ServiceProvider serviceProvider;

            Optional<Class<? extends ServiceProvider>> foundServiceProvider =
                    testDescriptor
                            .getHint()
                            .map(Hint::serviceProvider)
                            .filter(((Predicate) ServiceProvider.class::equals)
                                    .negate());

            if (foundServiceProvider.isPresent()) {
                serviceProvider = serviceLocatorUtil.getOne(
                        ServiceProvider.class,
                        foundServiceProvider.get());
            } else {
                serviceProvider = serviceLocatorUtil.getOne(
                        ServiceProvider.class,
                        DefaultServiceProvider.class);
            }

            Object serviceContext = serviceProvider.create(testContext);

            serviceInstance = serviceProvider.configure(testContext, serviceContext);
            testContext.addProperty(SERVICE_INSTANCE, serviceInstance);

            serviceProvider.postConfigure(testContext, serviceInstance);
            testConfigurer.configure(testContext, serviceContext);
        }
        return serviceInstance;
    }

    void createServer(TestContext testContext, TestConfigurer testConfigurer,
            ServerProvider serverProvider, Application application) throws
            Exception {
        //configure and start the server
        Object serverConfig = serverProvider.configure(testContext);
        serverConfig = testConfigurer.configure(testContext, serverConfig);

        ServerInstance serverInstance =
                serverProvider.start(testContext, application, serverConfig);

        testContext
                .addProperty(TestContextProperties.APP_SERVER_INSTANCE, serverInstance)
                .addProperty(serverInstance.getFqn(), serverInstance.getProperties())
                .addProperty(TestContextProperties.APP_BASE_URI, serverInstance
                        .getBaseURI())
                .addProperty(TestContextProperties.APP_SERVER, serverInstance
                        .getServer().getValue());
    }

    void createClient(TestContext testContext, TestConfigurer testConfigurer,
            ClientProvider clientProvider, Application application) {
        URI baseURI = testContext.getProperty(TestContextProperties.APP_BASE_URI);
        //configure and create the client
        Object clientConfig =
                clientProvider.configure(testContext, application, baseURI);
        clientConfig = testConfigurer.configure(testContext, clientConfig);

        ClientInstance<Object, Object> clientInstance =
                clientProvider.create(testContext, application, baseURI, clientConfig);
        testContext
                .addProperty(TestContextProperties.APP_CLIENT_INSTANCE, clientInstance)
                .addProperty(TestContextProperties.APP_CLIENT, clientInstance
                        .getClient().getValue())
                .addProperty(clientInstance.getFqn(), clientInstance.getProperties());

        clientInstance.getClientSupplier()
                .map(Instance::getValue)
                .ifPresent(value -> {
                    testContext.addProperty(
                            TestContextProperties.APP_CLIENT_SUPPLIER,
                            value
                    );
                });
    }

    void createSut(TestContext testContext,
            SutDescriptor sutDescriptor,
            ServiceInstance serviceInstance,
            Object testInstance) {
        Class sutType = sutDescriptor.getType();
        Optional<ClientInstance> foundClientInstance =
                testContext.findProperty(TestContextProperties.APP_CLIENT_INSTANCE);

        Object sutValue = null;

        if (ClientInstance.class.isAssignableFrom(sutType)) {
            sutValue = serviceInstance.getService(sutType);
        } else if (foundClientInstance.isPresent()) {
            ClientInstance clientInstance = foundClientInstance.get();
            Instance<Object> client = clientInstance.getClient();
            String clientName = client.getName();
            Class<?> clientContract = client.getContract();

            if (sutType.isAssignableFrom(clientContract)) {
                try {
                    sutValue = serviceInstance.getService(sutType, clientName);
                } catch (Exception e) {
                    LoggingUtil.INSTANCE.debug("could not find client of type '{}'",
                            clientContract);
                }

                //if the client is not in the service instance then simply use the client value.
                //we do this because client can be final and therefore not proxiable
                if (sutValue == null) {
                    sutValue = client.getValue();
                }

            }

            Optional<Instance<Object>> foundClientSupplier = clientInstance.getClientSupplier();

            if (sutValue == null && foundClientSupplier.isPresent()) {
                Instance<Object> clientSupplier = foundClientSupplier.get();
                String clientSupplierName = clientSupplier.getName();
                Class<?> clientSupplierContract = clientSupplier.getContract();

                if (sutType.isAssignableFrom(clientSupplierContract)) {
                    try {
                        sutValue = serviceInstance.getService(sutType, clientSupplierName);
                    } catch (Exception e) {
                        LoggingUtil.INSTANCE
                                .debug("could not find client supplier of type '{}'",
                                        clientContract);
                    }
                }
            }
        }

        sutDescriptor.setValue(testInstance, sutValue);
    }

}
