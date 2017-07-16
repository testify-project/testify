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

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import org.testifyproject.FieldDescriptor;
import org.testifyproject.Instance;
import org.testifyproject.ServerInstance;
import org.testifyproject.ServerProvider;
import org.testifyproject.ServiceInstance;
import org.testifyproject.SutDescriptor;
import org.testifyproject.TestConfigurer;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.TestResourcesProvider;
import org.testifyproject.annotation.Application;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.PreiVerifier;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.level.system.fixture.TestClientProvider;
import org.testifyproject.level.system.fixture.TestServerProvider;

/**
 *
 * @author saden
 */
public class SystemTestRunnerTest {

    SystemTestRunner sut;

    ServiceLocatorUtil serviceLocatorUtil;
    ReflectionUtil reflectionUtil;

    @Before
    public void init() {
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);
        reflectionUtil = mock(ReflectionUtil.class);

        sut = spy(new SystemTestRunner(serviceLocatorUtil, reflectionUtil));
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

        sut.start(testContext);

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

        CollaboratorReifier collaboratorReifier = mock(CollaboratorReifier.class);
        List<CollaboratorReifier> collaboratorReifiers = ImmutableList.of(collaboratorReifier);

        PreVerifier configurationVerifier = mock(PreVerifier.class);
        List<PreVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);
        Class serverProviderType = ServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);
        Object serverConfig = new Object();
        ServerInstance serverInstance = mock(ServerInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.empty();
        List<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class, SystemCategory.class))
                .willReturn(collaboratorReifiers);
        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(serviceLocatorUtil.findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class))
                .willReturn(configurationVerifiers);
        given(application.serverProvider()).willReturn(serverProviderType);
        given(serviceLocatorUtil.getOne(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(testContext, serverConfig)).willReturn(serverInstance);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);

        sut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getApplication();
        verify(serviceLocatorUtil).findAllWithFilter(CollaboratorReifier.class, SystemCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class);
        verify(application).serverProvider();
        verify(serviceLocatorUtil).getOne(serverProviderType);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(testContext, serverConfig);
        verify(testContext).<ServiceInstance>findProperty(SERVICE_INSTANCE);
    }

    @Test
    public void givenApplicationWithoutServiceInstanceAndCustomServerStartShouldStartApplication() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);

        CollaboratorReifier collaboratorReifier = mock(CollaboratorReifier.class);
        List<CollaboratorReifier> collaboratorReifiers = ImmutableList.of(collaboratorReifier);

        PreVerifier configurationVerifier = mock(PreVerifier.class);
        List<PreVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);
        Class serverProviderType = TestServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);
        Object serverConfig = new Object();
        ServerInstance serverInstance = mock(ServerInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.empty();
        List<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class, SystemCategory.class))
                .willReturn(collaboratorReifiers);
        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(serviceLocatorUtil.findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class))
                .willReturn(configurationVerifiers);
        given(application.serverProvider()).willReturn(serverProviderType);
        given(reflectionUtil.newInstance(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(testContext, serverConfig)).willReturn(serverInstance);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);

        sut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getApplication();
        verify(serviceLocatorUtil).findAllWithFilter(CollaboratorReifier.class, SystemCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class);
        verify(application).serverProvider();
        verify(reflectionUtil).newInstance(serverProviderType);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(testContext, serverConfig);
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

        CollaboratorReifier collaboratorReifier = mock(CollaboratorReifier.class);
        List<CollaboratorReifier> collaboratorReifiers = ImmutableList.of(collaboratorReifier);

        PreVerifier configurationVerifier = mock(PreVerifier.class);
        List<PreVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);
        Class serverProviderType = ServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);
        Object serverConfig = new Object();
        ServerInstance serverInstance = mock(ServerInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.of(serviceInstance);
        TestResourcesProvider testResourcesProvider = mock(TestResourcesProvider.class);

        FinalReifier testReifier = mock(FinalReifier.class);
        List<FinalReifier> testReifiers = ImmutableList.of(testReifier);
        String fqn = "fqn";
        Map<String, Object> properties = mock(Map.class);

        PreiVerifier wiringVerifier = mock(PreiVerifier.class);
        List<PreiVerifier> wiringVerifiers = ImmutableList.of(wiringVerifier);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        List<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getApplication()).willReturn(foundApplication);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(serviceLocatorUtil.findAllWithFilter(CollaboratorReifier.class, SystemCategory.class))
                .willReturn(collaboratorReifiers);
        given(serviceLocatorUtil.findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class))
                .willReturn(configurationVerifiers);
        given(application.serverProvider()).willReturn(serverProviderType);
        given(serviceLocatorUtil.getOne(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(testContext, serverConfig)).willReturn(serverInstance);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);
        willDoNothing().given(sut).addServer(serviceInstance, application, serverInstance);
        willDoNothing().given(sut).addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);
        given(serverInstance.getFqn()).willReturn(fqn);
        given(serverInstance.getProperties()).willReturn(properties);
        given(serviceLocatorUtil.getOne(TestResourcesProvider.class)).willReturn(testResourcesProvider);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        willDoNothing().given(sut).createClassUnderTest(sutDescriptor, application, serviceInstance, testInstance);
        given(serviceLocatorUtil.findAllWithFilter(FinalReifier.class, SystemCategory.class))
                .willReturn(testReifiers);
        given(serviceLocatorUtil.findAllWithFilter(PreiVerifier.class, guidelines, SystemCategory.class))
                .willReturn(wiringVerifiers);
        sut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testDescriptor).getApplication();
        verify(serviceLocatorUtil).findAllWithFilter(CollaboratorReifier.class, SystemCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class);
        verify(application).serverProvider();
        verify(serviceLocatorUtil).getOne(serverProviderType);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(testContext, serverConfig);
        verify(testContext).<ServiceInstance>findProperty(SERVICE_INSTANCE);
        verify(serviceInstance).addConstant(testContext, null, TestContext.class);
        verify(testContext).addProperty(fqn, properties);
        verify(sut).addServer(serviceInstance, application, serverInstance);
        verify(sut).addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);
        verify(serviceInstance).init();
        verify(serviceLocatorUtil).getOne(TestResourcesProvider.class);
        verify(testContext).getSutDescriptor();
        verify(sut).createClassUnderTest(sutDescriptor, application, serviceInstance, testInstance);
        verify(serviceLocatorUtil).findAllWithFilter(FinalReifier.class, SystemCategory.class);
        verify(serviceLocatorUtil).findAllWithFilter(PreiVerifier.class, guidelines, SystemCategory.class);
    }

    @Test
    public void callToStopShouldStopTest() {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();

        TestResourcesProvider testResourcesProvider = sut.testResourcesProvider = mock(TestResourcesProvider.class);
        ClientProvider clientProvider = sut.clientProvider = mock(ClientProvider.class);
        ServerProvider serverProvider = sut.serverProvider = mock(ServerProvider.class);
        ServerInstance serverInstance = sut.serverInstance = mock(ServerInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.of(serviceInstance);

        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);

        PostVerifier postVerifier = mock(PostVerifier.class);
        List<PostVerifier> postVerifiers = ImmutableList.of(postVerifier);
        List<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(serviceLocatorUtil.findAllWithFilter(PostVerifier.class, guidelines, SystemCategory.class))
                .willReturn(postVerifiers);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);

        sut.stop(testContext);

        verify(postVerifier).verify(testContext);
        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(fieldDescriptor).destroy(testInstance);
        verify(sutDescriptor).destroy(testInstance);
        verify(testResourcesProvider).stop(testContext, serviceInstance);
        verify(clientProvider).destroy();
        verify(serverProvider).stop(testContext, serverInstance);
    }

    @Test
    public void callToCreateClassUnderTestWithClientInstanceShouldGetClassUnderTest() {
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Application application = mock(Application.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Object testInstance = new Object();

        Class sutType = ClientInstance.class;
        Object sutValue = new Object();

        given(sutDescriptor.getType()).willReturn(sutType);
        given(serviceInstance.getService(sutType)).willReturn(sutValue);

        sut.createClassUnderTest(sutDescriptor, application, serviceInstance, testInstance);

        verify(sutDescriptor).getType();
        verify(serviceInstance).getService(sutType);
        verify(sutDescriptor).setValue(testInstance, sutValue);
    }

    @Test
    public void callToCreateClassUnderTestWithClientShouldGetClassUnderTest() {
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Application application = mock(Application.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Object testInstance = new Object();

        Class sutType = Object.class;
        Object sutValue = new Object();
        String name = "clientName";

        given(sutDescriptor.getType()).willReturn(sutType);
        given(application.clientName()).willReturn(name);
        given(serviceInstance.getService(sutType, name)).willReturn(sutValue);

        sut.createClassUnderTest(sutDescriptor, application, serviceInstance, testInstance);

        verify(sutDescriptor).getType();
        verify(application).clientName();
        verify(serviceInstance).getService(sutType, name);
        verify(sutDescriptor).setValue(testInstance, sutValue);
    }

    @Test
    public void callToAddServerShouldAddServer() {
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Application application = mock(Application.class);
        ServerInstance serverInstance = mock(ServerInstance.class);

        ServerProvider serverProvider = sut.serverProvider = mock(ServerProvider.class);
        String name = serverProvider.getClass().getSimpleName();
        Class contract = ServerInstance.class;
        Instance server = mock(Instance.class);

        given(serverInstance.getServer()).willReturn(server);
        given(application.serverName()).willReturn(name);
        given(application.serverContract()).willReturn(contract);

        sut.addServer(serviceInstance, application, serverInstance);

        verify(serviceInstance).addConstant(serverInstance, name, contract);
        verify(serviceInstance).replace(server, name, contract);
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
        ClientProvider clientProvider = sut.clientProvider = mock(ClientProvider.class);
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

        sut.addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);

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
        ClientProvider clientProvider = sut.clientProvider = mock(TestClientProvider.class);
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

        sut.addClient(serviceInstance, application, serverInstance, testContext, testConfigurer);

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
