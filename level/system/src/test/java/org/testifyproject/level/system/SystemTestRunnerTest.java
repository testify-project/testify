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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.annotation.Application;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.ConfigurationVerifier;
import org.testifyproject.extension.FieldReifier;
import org.testifyproject.extension.TestReifier;
import org.testifyproject.extension.WiringVerifier;
import org.testifyproject.extension.annotation.SystemTest;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.level.system.fixture.TestClientProvider;
import org.testifyproject.level.system.fixture.TestServerProvider;

/**
 *
 * @author saden
 */
public class SystemTestRunnerTest {

    SystemTestRunner cut;

    ServiceLocatorUtil serviceLocatorUtil;
    ReflectionUtil reflectionUtil;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        reflectionUtil = mock(ReflectionUtil.class);

        cut = spy(new SystemTestRunner(serviceLocatorUtil, reflectionUtil));
    }

    @Test
    public void givenNoApplicationStartShouldDoNothing() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Optional<Application> foundApplication = Optional.empty();

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);

        cut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getApplication();
    }

    @Test
    public void givenApplicationWithoutServiceInstanceStartShouldStartApplication() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);

        FieldReifier fieldReifier = mock(FieldReifier.class);
        List<FieldReifier> fieldReifiers = ImmutableList.of(fieldReifier);

        ConfigurationVerifier configurationVerifier = mock(ConfigurationVerifier.class);
        List<ConfigurationVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);
        Class serverProviderType = ServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);
        Object serverConfig = new Object();
        ServerInstance serverInstance = mock(ServerInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.empty();

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(serviceLocatorUtil.findAllWithFilter(FieldReifier.class, SystemTest.class))
                .willReturn(fieldReifiers);
        given(serviceLocatorUtil.findAllWithFilter(ConfigurationVerifier.class, SystemTest.class))
                .willReturn(configurationVerifiers);
        given(application.serverProvider()).willReturn(serverProviderType);
        given(serviceLocatorUtil.getOne(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(serverConfig)).willReturn(serverInstance);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);

        cut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getApplication();
        verify(serviceLocatorUtil).findAllWithFilter(FieldReifier.class, SystemTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(ConfigurationVerifier.class, SystemTest.class);
        verify(application).serverProvider();
        verify(serviceLocatorUtil).getOne(serverProviderType);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(serverConfig);
        verify(testContext).<ServiceInstance>findProperty(SERVICE_INSTANCE);
    }

    @Test
    public void givenApplicationWithoutServiceInstanceAndCustomServerStartShouldStartApplication() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);

        FieldReifier fieldReifier = mock(FieldReifier.class);
        List<FieldReifier> fieldReifiers = ImmutableList.of(fieldReifier);

        ConfigurationVerifier configurationVerifier = mock(ConfigurationVerifier.class);
        List<ConfigurationVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);
        Class serverProviderType = TestServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);
        Object serverConfig = new Object();
        ServerInstance serverInstance = mock(ServerInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.empty();

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(serviceLocatorUtil.findAllWithFilter(FieldReifier.class, SystemTest.class))
                .willReturn(fieldReifiers);
        given(serviceLocatorUtil.findAllWithFilter(ConfigurationVerifier.class, SystemTest.class))
                .willReturn(configurationVerifiers);
        given(application.serverProvider()).willReturn(serverProviderType);
        given(reflectionUtil.newInstance(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(serverConfig)).willReturn(serverInstance);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);

        cut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getApplication();
        verify(serviceLocatorUtil).findAllWithFilter(FieldReifier.class, SystemTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(ConfigurationVerifier.class, SystemTest.class);
        verify(application).serverProvider();
        verify(reflectionUtil).newInstance(serverProviderType);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(serverConfig);
        verify(testContext).<ServiceInstance>findProperty(SERVICE_INSTANCE);
    }

    @Test
    public void givenApplicationWithServiceInstanceStartShouldStartApplicationAndAddConstants() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);
        Object testInstance = new Object();

        FieldReifier fieldReifier = mock(FieldReifier.class);
        List<FieldReifier> fieldReifiers = ImmutableList.of(fieldReifier);

        ConfigurationVerifier configurationVerifier = mock(ConfigurationVerifier.class);
        List<ConfigurationVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);
        Class serverProviderType = ServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);
        Object serverConfig = new Object();
        ServerInstance serverInstance = mock(ServerInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.of(serviceInstance);
        TestResourcesProvider testResourcesProvider = mock(TestResourcesProvider.class);

        TestReifier testReifier = mock(TestReifier.class);
        List<TestReifier> testReifiers = ImmutableList.of(testReifier);

        WiringVerifier wiringVerifier = mock(WiringVerifier.class);
        List<WiringVerifier> wiringVerifiers = ImmutableList.of(wiringVerifier);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(serviceLocatorUtil.findAllWithFilter(FieldReifier.class, SystemTest.class))
                .willReturn(fieldReifiers);
        given(serviceLocatorUtil.findAllWithFilter(ConfigurationVerifier.class, SystemTest.class))
                .willReturn(configurationVerifiers);
        given(application.serverProvider()).willReturn(serverProviderType);
        given(serviceLocatorUtil.getOne(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(serverConfig)).willReturn(serverInstance);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);
        willDoNothing().given(cut).addServer(serviceInstance, application, serverInstance);
        willDoNothing().given(cut).addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);
        given(serviceLocatorUtil.getOne(TestResourcesProvider.class)).willReturn(testResourcesProvider);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);
        willDoNothing().given(cut).createClassUnderTest(cutDescriptor, application, serviceInstance, testInstance);
        given(serviceLocatorUtil.findAllWithFilter(TestReifier.class, SystemTest.class))
                .willReturn(testReifiers);
        given(serviceLocatorUtil.findAllWithFilter(WiringVerifier.class, SystemTest.class))
                .willReturn(wiringVerifiers);
        cut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getApplication();
        verify(serviceLocatorUtil).findAllWithFilter(FieldReifier.class, SystemTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(ConfigurationVerifier.class, SystemTest.class);
        verify(application).serverProvider();
        verify(serviceLocatorUtil).getOne(serverProviderType);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(serverConfig);
        verify(testContext).<ServiceInstance>findProperty(SERVICE_INSTANCE);
        verify(serviceInstance).addConstant(testContext, null, TestContext.class);
        verify(cut).addServer(serviceInstance, application, serverInstance);
        verify(cut).addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);
        verify(serviceInstance).init();
        verify(serviceLocatorUtil).getOne(TestResourcesProvider.class);
        verify(testContext).getCutDescriptor();
        verify(cut).createClassUnderTest(cutDescriptor, application, serviceInstance, testInstance);
        verify(serviceLocatorUtil).findAllWithFilter(TestReifier.class, SystemTest.class);
        verify(serviceLocatorUtil).findAllWithFilter(WiringVerifier.class, SystemTest.class);
    }

    @Test
    public void callToStopShouldStopTest() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();

        TestResourcesProvider testResourcesProvider = cut.testResourcesProvider = mock(TestResourcesProvider.class);
        ClientProvider clientProvider = cut.clientProvider = mock(ClientProvider.class);
        ServerProvider serverProvider = cut.serverProvider = mock(ServerProvider.class);

        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Optional<CutDescriptor> foundCutDescriptor = Optional.of(cutDescriptor);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testContext.getCutDescriptor()).willReturn(foundCutDescriptor);

        cut.stop(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(fieldDescriptor).destroy(testInstance);
        verify(cutDescriptor).destroy(testInstance);
        verify(testResourcesProvider).stop(testContext);
        verify(clientProvider).destroy();
        verify(serverProvider).stop();
    }

    @Test
    public void callToCreateClassUnderTestWithClientInstanceShouldGetClassUnderTest() {
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Application application = mock(Application.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Object testInstance = new Object();

        Class cutType = ClientInstance.class;
        Object cutValue = new Object();

        given(cutDescriptor.getType()).willReturn(cutType);
        given(serviceInstance.getService(cutType)).willReturn(cutValue);

        cut.createClassUnderTest(cutDescriptor, application, serviceInstance, testInstance);

        verify(cutDescriptor).getType();
        verify(serviceInstance).getService(cutType);
        verify(cutDescriptor).setValue(testInstance, cutValue);
    }

    @Test
    public void callToCreateClassUnderTestWithClientShouldGetClassUnderTest() {
        CutDescriptor cutDescriptor = mock(CutDescriptor.class);
        Application application = mock(Application.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Object testInstance = new Object();

        Class cutType = Object.class;
        Object cutValue = new Object();
        String name = "clientName";

        given(cutDescriptor.getType()).willReturn(cutType);
        given(application.clientName()).willReturn(name);
        given(serviceInstance.getService(cutType, name)).willReturn(cutValue);

        cut.createClassUnderTest(cutDescriptor, application, serviceInstance, testInstance);

        verify(cutDescriptor).getType();
        verify(application).clientName();
        verify(serviceInstance).getService(cutType, name);
        verify(cutDescriptor).setValue(testInstance, cutValue);
    }

    @Test
    public void callToAddServerShouldAddServer() {
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Application application = mock(Application.class);
        ServerInstance serverInstance = mock(ServerInstance.class);

        ServerProvider serverProvider = cut.serverProvider = mock(ServerProvider.class);
        String name = serverProvider.getClass().getSimpleName();
        Class contract = ServerInstance.class;

        given(application.serverName()).willReturn(name);
        given(application.serverContract()).willReturn(contract);

        cut.addServer(serviceInstance, application, serverInstance);

        verify(serviceInstance).addConstant(serverInstance, name, contract);
        verify(serviceInstance).replace(serverInstance, name, contract);
        verify(application).serverName();
        verify(application).serverContract();
    }

    @Test
    public void callToAddClientWithoutClientProviderShouldAddClient() {
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Application application = mock(Application.class);
        ServerInstance serverInstance = mock(ServerInstance.class);
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);

        Class clientProviderType = ClientProvider.class;
        ClientProvider clientProvider = cut.clientProvider = mock(ClientProvider.class);
        URI baseURI = URI.create("http://test");
        Object clientConfig = new Object();
        ClientInstance clientInstance = mock(ClientInstance.class);

        String name = clientProvider.getClass().getSimpleName();
        Class contract = ClientInstance.class;

        given(application.clientProvider()).willReturn(clientProviderType);
        given(serviceLocatorUtil.getOne(clientProviderType)).willReturn(clientProvider);
        given(serverInstance.getBaseURI()).willReturn(baseURI);
        given(clientProvider.configure(testContext, baseURI)).willReturn(clientConfig);
        given(testConfigurer.configure(testContext, clientConfig)).willReturn(clientConfig);
        given(clientProvider.create(testContext, baseURI, clientConfig)).willReturn(clientInstance);
        given(application.clientName()).willReturn(name);
        given(application.clientContract()).willReturn(contract);

        cut.addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);

        verify(application).clientProvider();
        verify(serviceLocatorUtil).getOne(clientProviderType);
        verify(serverInstance).getBaseURI();
        verify(clientProvider).configure(testContext, baseURI);
        verify(testConfigurer).configure(testContext, clientConfig);
        verify(clientProvider).create(testContext, baseURI, clientConfig);

        verify(serviceInstance).addConstant(clientInstance, name, contract);
        verify(serviceInstance).replace(clientInstance, name, contract);
        verify(application).clientName();
        verify(application).clientContract();
    }

    @Test
    public void callToAddClientWithClientProviderShouldAddClient() {
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Application application = mock(Application.class);
        ServerInstance serverInstance = mock(ServerInstance.class);
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);

        Class clientProviderType = TestClientProvider.class;
        ClientProvider clientProvider = cut.clientProvider = mock(TestClientProvider.class);
        URI baseURI = URI.create("http://test");
        Object clientConfig = new Object();
        ClientInstance clientInstance = mock(ClientInstance.class);

        String name = clientProvider.getClass().getSimpleName();
        Class contract = ClientInstance.class;

        given(application.clientProvider()).willReturn(clientProviderType);
        given(reflectionUtil.newInstance(clientProviderType)).willReturn(clientProvider);
        given(serverInstance.getBaseURI()).willReturn(baseURI);
        given(clientProvider.configure(testContext, baseURI)).willReturn(clientConfig);
        given(testConfigurer.configure(testContext, clientConfig)).willReturn(clientConfig);
        given(clientProvider.create(testContext, baseURI, clientConfig)).willReturn(clientInstance);
        given(application.clientName()).willReturn(name);
        given(application.clientContract()).willReturn(contract);

        cut.addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);

        verify(application).clientProvider();
        verify(reflectionUtil).newInstance(clientProviderType);
        verify(serverInstance).getBaseURI();
        verify(clientProvider).configure(testContext, baseURI);
        verify(testConfigurer).configure(testContext, clientConfig);
        verify(clientProvider).create(testContext, baseURI, clientConfig);

        verify(serviceInstance).addConstant(clientInstance, name, contract);
        verify(serviceInstance).replace(clientInstance, name, contract);
        verify(application).clientName();
        verify(application).clientContract();
    }

}
