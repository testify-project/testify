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

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

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
import org.testifyproject.annotation.Discoverable;
import org.testifyproject.annotation.Hint;
import org.testifyproject.core.DefaultServerProvider;
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
import org.testifyproject.extension.Verifier;
import org.testifyproject.extension.annotation.SystemCategory;

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
        Collection<Class<? extends Annotation>> guidelines = testDescriptor.getGuidelines();
        Optional<Application> foundApplication = testDescriptor.getApplication();

        foundApplication.ifPresent(application -> {
            try {
                //create and initalize mock fields. this is necessary so we can configure
                //expected interaction prior to making a call to the application
                //endpoints
                serviceLocatorUtil.findAllWithFilter(
                        CollaboratorReifier.class,
                        SystemCategory.class).forEach(p -> p.reify(testContext));

                serviceLocatorUtil
                        .findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class)
                        .forEach(p -> p.verify(testContext));

                testContext.verify();

                resourceController = serviceLocatorUtil.getOne(ResourceController.class);
                resourceController.start(testContext);

                ServerProvider serverProvider = createServerProvider(testContext, application);
                ClientProvider clientProvider = createClientProvider(testContext, application);

                createServer(testContext, testConfigurer, serverProvider, application);
                createClient(testContext, testConfigurer, clientProvider, application);
            } catch (Exception ex) {
                throw ExceptionUtil.INSTANCE.propagate(ex);
            }

            ServiceInstance serviceInstance =
                    createService(testContext, testDescriptor, testConfigurer);

            if (foundSutDescriptor.isPresent()) {
                SutDescriptor sutDescriptor = foundSutDescriptor.get();
                createSut(testContext, sutDescriptor, serviceInstance, testInstance);
            }

            serviceLocatorUtil
                    .findAllWithFilter(FinalReifier.class, SystemCategory.class)
                    .forEach(p -> p.reify(testContext));

            serviceLocatorUtil
                    .findAllWithFilter(Verifier.class, guidelines, SystemCategory.class)
                    .forEach(p -> p.verify(testContext));
            testContext.verify();
        });
    }

    @Override
    public void stop(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<SutDescriptor> sutDescriptor = testContext.getSutDescriptor();
        Collection<Class<? extends Annotation>> guidelines = testDescriptor.getGuidelines();

        serviceLocatorUtil.findAllWithFilter(
                PostVerifier.class,
                guidelines,
                SystemCategory.class
        ).forEach(p -> p.verify(testContext));
        testContext.verify();

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        sutDescriptor.ifPresent(p -> p.destroy(testInstance));

        Optional<ClientInstance> foundClientInstance =
                testContext.findProperty(TestContextProperties.CLIENT_INSTANCE);
        Optional<ClientProvider> foundClientProvider =
                testContext.findProperty(TestContextProperties.CLIENT_PROVIDER);

        if (foundClientInstance.isPresent() && foundClientProvider.isPresent()) {
            ClientInstance clientInstance = foundClientInstance.get();
            ClientProvider clientProvider = foundClientProvider.get();
            try {
                clientProvider.destroy(clientInstance);
            } catch (Exception e) {
                LoggingUtil.INSTANCE.error(
                        "Could not destroy client provider '{}'",
                        clientProvider.getClass().getSimpleName(), e
                );
            }
        }

        //XXX: because an exception is thrown by stop we have to catch it and propogate it
        //this way server prvoider impl don't have to worry about handling exceptions
        Optional<ServerInstance> foundServerInstance =
                testContext.<ServerInstance>findProperty(TestContextProperties.SERVER_INSTANCE);
        Optional<ServerProvider> foundServerProvider =
                testContext.findProperty(TestContextProperties.SERVER_PROVIDER);

        if (foundServerInstance.isPresent() && foundServerProvider.isPresent()) {
            ServerInstance serverInstance = foundServerInstance.get();
            ServerProvider serverProvider = foundServerProvider.get();

            try {
                serverProvider.stop(serverInstance);
            } catch (Exception e) {
                LoggingUtil.INSTANCE.error(
                        "Could not destroy server provider '{}'",
                        serverProvider.getClass().getSimpleName(), e
                );
            }
        }

        resourceController.stop(testContext);
    }

    ServerProvider createServerProvider(TestContext testContext, Application application) {
        Class<? extends ServerProvider> serverProviderType = application.serverProvider();
        ServerProvider serverProvider;

        if (ServerProvider.class.equals(serverProviderType)) {
            String start = application.start();
            String stop = application.stop();

            if (!start.isEmpty() && !stop.isEmpty()) {
                serverProvider = new DefaultServerProvider();
            } else {
                serverProvider = serviceLocatorUtil.getOne(serverProviderType);
            }
        } else {
            serverProvider = reflectionUtil.newInstance(serverProviderType);
        }

        testContext.addProperty(TestContextProperties.SERVER_PROVIDER, serverProvider);

        return serverProvider;
    }

    ClientProvider createClientProvider(TestContext testContext, Application application) {
        //create client supplier instance
        Class<? extends ClientProvider> clientProviderType = application.clientProvider();
        ClientProvider clientProvider;

        if (clientProviderType.equals(ClientProvider.class)) {
            clientProvider = serviceLocatorUtil.getOne(clientProviderType);
        } else {
            clientProvider = reflectionUtil.newInstance(clientProviderType);
        }

        testContext.addProperty(TestContextProperties.CLIENT_PROVIDER, clientProvider);

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
            ServiceProvider serviceProvider = serviceLocatorUtil.getFromHintOrDefault(
                    testContext,
                    ServiceProvider.class,
                    DefaultServiceProvider.class,
                    Hint::serviceProvider);

            Object serviceContext = serviceProvider.create(testContext);
            testConfigurer.configure(testContext, serviceContext);

            serviceInstance = serviceProvider.configure(testContext, serviceContext);
            testContext.addProperty(SERVICE_INSTANCE, serviceInstance);

            serviceProvider.postConfigure(testContext, serviceInstance);
        }

        return serviceInstance;
    }

    void createServer(TestContext testContext, TestConfigurer testConfigurer,
            ServerProvider serverProvider, Application application)
            throws Exception {
        //configure and start the server
        Object serverConfig = serverProvider.configure(testContext);
        serverConfig = testConfigurer.configure(testContext, serverConfig);

        ServerInstance serverInstance =
                serverProvider.start(testContext, application, serverConfig);

        testContext
                .addProperty(TestContextProperties.SERVER_INSTANCE, serverInstance)
                .addProperty(serverInstance.getFqn(), serverInstance.getProperties())
                .addProperty(TestContextProperties.SERVER_BASE_URI, serverInstance.getBaseURI())
                .addProperty(TestContextProperties.SERVER, serverInstance
                        .getServer().getValue());
    }

    void createClient(TestContext testContext, TestConfigurer testConfigurer,
            ClientProvider clientProvider, Application application) {
        URI baseURI = testContext.getProperty(TestContextProperties.SERVER_BASE_URI);
        //configure and create the client
        Object clientConfig = clientProvider.configure(testContext, application, baseURI);
        clientConfig = testConfigurer.configure(testContext, clientConfig);

        ClientInstance<Object, Object> clientInstance =
                clientProvider.create(testContext, application, baseURI, clientConfig);

        testContext
                .addProperty(TestContextProperties.CLIENT_INSTANCE, clientInstance)
                .addProperty(clientInstance.getFqn(), clientInstance.getProperties())
                .addProperty(TestContextProperties.CLIENT, clientInstance
                        .getClient().getValue());

        clientInstance.getClientSupplier()
                .map(Instance::getValue)
                .ifPresent(value -> {
                    testContext.addProperty(TestContextProperties.CLIENT_SUPPLIER, value);
                });
    }

    void createSut(TestContext testContext,
            SutDescriptor sutDescriptor,
            ServiceInstance serviceInstance,
            Object testInstance) {
        Class sutType = sutDescriptor.getType();
        Optional<ClientInstance> foundClientInstance =
                testContext.findProperty(TestContextProperties.CLIENT_INSTANCE);

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
                        LoggingUtil.INSTANCE.debug(
                                "could not find client supplier of type '{}'",
                                clientContract, e);
                    }
                }
            }
        }

        sutDescriptor.setValue(testInstance, sutValue);
    }

}
