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
import org.testifyproject.InstanceProvider;
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
import org.testifyproject.core.TestContextProperties;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
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

    TestResourcesProvider testResourcesProvider;
    ServerProvider serverProvider;
    ClientProvider clientProvider;

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

        testDescriptor.getApplication().ifPresent(application -> {
            Object testInstance = testContext.getTestInstance();

            //create and initalize mock fields. this is necessary so we can configure
            //expected interaction prior to making a call to the application
            //endpoints
            serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class, SystemCategory.class)
                    .forEach(p -> p.reify(testContext));

            serviceLocatorUtil.findAllWithFilter(PreVerifier.class, testDescriptor.getGuidelines(), SystemCategory.class)
                    .forEach(p -> p.verify(testContext));

            ServerInstance serverInstance = createServer(testContext, application, testConfigurer);
            testContext.addProperty(TestContextProperties.APP_SERVER_INSTANCE, serverInstance);
            testContext.addProperty(serverInstance.getFqn(), serverInstance.getProperties());

            ClientInstance clientInstance = createClient(testContext, application, testConfigurer, serverInstance.getBaseURI());
            testContext.addProperty(TestContextProperties.APP_CLIENT_INSTANCE, clientInstance);
            testContext.addProperty(clientInstance.getFqn(), clientInstance.getProperties());

            testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE).ifPresent(serviceInstance -> {
                //add constant instances
                serviceLocatorUtil.findAllWithFilter(InstanceProvider.class, SystemCategory.class)
                        .stream()
                        .flatMap(p -> p.get(testContext).stream())
                        .forEach(serviceInstance::replace);

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
                        -> createSut(sutDescriptor, clientInstance, serviceInstance, testInstance)
                );

                serviceLocatorUtil.findAllWithFilter(FinalReifier.class, SystemCategory.class)
                        .forEach(p -> p.reify(testContext));

                serviceLocatorUtil.findAllWithFilter(PreiVerifier.class, testDescriptor.getGuidelines(), SystemCategory.class)
                        .forEach(p -> p.verify(testContext));
            });
        });
    }

    @Override
    public void stop(TestContext testContext) {
        TestDescriptor testDescriptor = testContext.getTestDescriptor();
        Object testInstance = testContext.getTestInstance();
        Optional<SutDescriptor> sutDescriptor = testContext.getSutDescriptor();

        serviceLocatorUtil.findAllWithFilter(PostVerifier.class, testDescriptor.getGuidelines(), SystemCategory.class)
                .forEach(p -> p.verify(testContext));

        //invoke destroy method on fields annotated with Fixture
        testDescriptor.getFieldDescriptors()
                .forEach(p -> p.destroy(testInstance));

        //invoke destroy method on sut field annotated with Fixture
        sutDescriptor.ifPresent(p -> p.destroy(testInstance));

        testContext.<ClientInstance>findProperty(TestContextProperties.APP_CLIENT_INSTANCE)
                .ifPresent(clientProvider::destroy);

        testContext.<ServerInstance>findProperty(TestContextProperties.APP_SERVER_INSTANCE)
                .ifPresent(serverProvider::stop);

        if (testResourcesProvider != null) {
            ServiceInstance serviceInstance = testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)
                    .orElse(null);

            testResourcesProvider.stop(testContext, serviceInstance);
        }
    }

    void createSut(SutDescriptor sutDescriptor,
            ClientInstance<Object> clientInstance,
            ServiceInstance serviceInstance,
            Object testInstance) {
        Class sutType = sutDescriptor.getType();
        Instance<Object> client = clientInstance.getClient();
        Optional<String> foundName = client.getName();

        Object sutValue = null;

        if (ClientInstance.class.isAssignableFrom(sutType)) {
            sutValue = serviceInstance.getService(sutType);
        } else if (foundName.isPresent()) {
            sutValue = serviceInstance.getService(sutType, foundName.get());
        }

        sutDescriptor.setValue(testInstance, sutValue);
    }

    ServerInstance createServer(TestContext testContext, Application application, TestConfigurer testConfigurer) {
        //create server provider instance
        Class<? extends ServerProvider> serverProviderType = application.serverProvider();
        if (ServerProvider.class.equals(serverProviderType)) {
            serverProvider = serviceLocatorUtil.getOne(serverProviderType);
        } else {
            serverProvider = reflectionUtil.newInstance(serverProviderType);
        }

        //configure and start the server
        Object serverConfig = serverProvider.configure(testContext);
        serverConfig = testConfigurer.configure(testContext, serverConfig);

        return serverProvider.start(testContext, application, serverConfig);
    }

    ClientInstance createClient(TestContext testContext,
            Application application,
            TestConfigurer testConfigurer,
            URI baseURI) {
        //create client provider instance
        Class<? extends ClientProvider> clientProviderType = application.clientProvider();

        if (clientProviderType.equals(ClientProvider.class)) {
            clientProvider = serviceLocatorUtil.getOne(ClientProvider.class);
        } else {
            clientProvider = reflectionUtil.newInstance(clientProviderType);
        }

        //configure and create the client
        Object clientConfig = clientProvider.configure(testContext, application, baseURI);
        clientConfig = testConfigurer.configure(testContext, clientConfig);

        return clientProvider.create(testContext, application, baseURI, clientConfig);
    }
}
