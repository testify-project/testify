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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import static org.testifyproject.core.TestContextProperties.APP_CLIENT_INSTANCE;
import static org.testifyproject.core.TestContextProperties.APP_SERVER_INSTANCE;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreInstanceProvider;
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
    public void callToDefaultConstructorShouldReturnNewInstance() {
        sut = new SystemTestRunner();

        assertThat(sut).isNotNull();
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

        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor);
    }

    @Test
    public void givenApplicationWithServiceInstanceStartShouldStartApplication() {
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
        ServerInstance serverInstance = mock(ServerInstance.class);
        ClientInstance clientInstance = mock(ClientInstance.class);
        URI baseURI = URI.create("uri://test");

        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.of(serviceInstance);
        PreInstanceProvider instanceProvider = mock(PreInstanceProvider.class);
        List<PreInstanceProvider> instanceProviders = ImmutableList.of(instanceProvider);
        TestResourcesProvider testResourcesProvider = mock(TestResourcesProvider.class);

        FinalReifier testReifier = mock(FinalReifier.class);
        List<FinalReifier> testReifiers = ImmutableList.of(testReifier);

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
        willReturn(serverInstance).given(sut).createServer(testContext, application, testConfigurer);
        given(serverInstance.getBaseURI()).willReturn(baseURI);
        willReturn(clientInstance).given(sut).createClient(testContext, application, testConfigurer, baseURI);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);
        given(serviceLocatorUtil.findAllWithFilter(PreInstanceProvider.class, SystemCategory.class))
                .willReturn(instanceProviders);
        given(serviceLocatorUtil.getOne(TestResourcesProvider.class)).willReturn(testResourcesProvider);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        willDoNothing().given(sut).createSut(sutDescriptor, clientInstance, serviceInstance, testInstance);
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
        verify(sut).createServer(testContext, application, testConfigurer);
        verify(sut).createClient(testContext, application, testConfigurer, baseURI);
        //verify(serviceLocatorUtil).findAllWithFilter(PreInstanceProvider.class, SystemCategory.class);
        verify(serviceLocatorUtil).getOne(TestResourcesProvider.class);
        verify(testResourcesProvider).start(testContext);
        verify(serviceInstance).init();
        verify(testContext).getSutDescriptor();
        verify(sut).createSut(sutDescriptor, clientInstance, serviceInstance, testInstance);
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
        ServerInstance serverInstance = mock(ServerInstance.class);
        ClientInstance clientInstance = mock(ClientInstance.class);
        Optional<ClientInstance> foundClientInstance = Optional.ofNullable(clientInstance);
        Optional<ServerInstance> foundServerInstance = Optional.of(serverInstance);

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
        given(testContext.<ClientInstance>findProperty(APP_CLIENT_INSTANCE)).willReturn(foundClientInstance);
        given(testContext.<ServerInstance>findProperty(APP_SERVER_INSTANCE)).willReturn(foundServerInstance);
        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE)).willReturn(foundServiceInstance);

        sut.stop(testContext);

        verify(postVerifier).verify(testContext);
        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(fieldDescriptor).destroy(testInstance);
        verify(sutDescriptor).destroy(testInstance);
        verify(testResourcesProvider).stop(testContext);
        verify(clientProvider).destroy(clientInstance);
        verify(serverProvider).stop(serverInstance);
        verify(testResourcesProvider).stop(testContext);
    }

    @Test
    public void givenClientInstanceCreateSUTShouldCreateAndSet() {
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        ClientInstance clientInstance = mock(ClientInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Object testInstance = new Object();

        Class sutType = ClientInstance.class;
        String name = "test";
        Optional<String> foundName = Optional.of(name);
        Instance<Object> client = mock(Instance.class);

        Object sutValue = new Object();

        given(sutDescriptor.getType()).willReturn(sutType);
        given(clientInstance.getClient()).willReturn(client);
        given(client.getName()).willReturn(foundName);
        given(serviceInstance.getService(sutType)).willReturn(sutValue);

        sut.createSut(sutDescriptor, clientInstance, serviceInstance, testInstance);

        verify(sutDescriptor).getType();
        verify(serviceInstance).getService(sutType);
        verify(sutDescriptor).setValue(testInstance, sutValue);
    }

    @Test
    public void givenClientCreateSUTShouldCreateAndSet() {
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        ClientInstance clientInstance = mock(ClientInstance.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Object testInstance = new Object();

        Class sutType = Object.class;
        String name = "test";
        Optional<String> foundName = Optional.of(name);
        Instance<Object> client = mock(Instance.class);

        Object sutValue = new Object();

        given(sutDescriptor.getType()).willReturn(sutType);
        given(clientInstance.getClient()).willReturn(client);
        given(client.getName()).willReturn(foundName);
        given(serviceInstance.getService(sutType, name)).willReturn(sutValue);

        sut.createSut(sutDescriptor, clientInstance, serviceInstance, testInstance);

        verify(sutDescriptor).getType();
        verify(serviceInstance).getService(sutType, name);
        verify(sutDescriptor).setValue(testInstance, sutValue);
    }

    @Test
    public void givenDefaultCustomServerProviderAddServerShouldAddServer() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        Class serverProviderType = ServerProvider.class;
        ServerProvider serverProvider = sut.serverProvider = mock(ServerProvider.class);
        Object serverConfig = mock(Object.class);
        ServerInstance serverInstance = mock(ServerInstance.class);

        given(application.serverProvider()).willReturn(serverProviderType);
        given(serviceLocatorUtil.getOne(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(testContext, application, serverConfig)).willReturn(serverInstance);

        ServerInstance result = sut.createServer(testContext, application, testConfigurer);

        assertThat(result).isEqualTo(serverInstance);
        verify(application).serverProvider();
        verify(serviceLocatorUtil).getOne(ServerProvider.class);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(testContext, application, serverConfig);
    }

    @Test
    public void givenCustomServerProviderAddServerShouldAddServer() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        Class serverProviderType = TestServerProvider.class;
        ServerProvider serverProvider = sut.serverProvider = mock(ServerProvider.class);
        Object serverConfig = mock(Object.class);
        ServerInstance serverInstance = mock(ServerInstance.class);

        given(application.serverProvider()).willReturn(serverProviderType);
        given(reflectionUtil.newInstance(serverProviderType)).willReturn(serverProvider);
        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(testContext, application, serverConfig)).willReturn(serverInstance);

        ServerInstance result = sut.createServer(testContext, application, testConfigurer);

        assertThat(result).isEqualTo(serverInstance);
        verify(application).serverProvider();
        verify(reflectionUtil).newInstance(serverProviderType);
        verify(serverProvider).configure(testContext);
        verify(testConfigurer).configure(testContext, serverConfig);
        verify(serverProvider).start(testContext, application, serverConfig);
    }

    @Test
    public void givenDefaultCustomClientProviderAddClientShouldAddClient() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        URI baseURI = URI.create("uri://test");
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        Class clientProviderType = ClientProvider.class;
        ClientProvider clientProvider = sut.clientProvider = mock(ClientProvider.class);
        Object clientConfig = mock(Object.class);

        ClientInstance clientInstance = mock(ClientInstance.class);

        given(application.clientProvider()).willReturn(clientProviderType);
        given(serviceLocatorUtil.getOne(clientProviderType)).willReturn(clientProvider);
        given(clientProvider.configure(testContext, application, baseURI)).willReturn(clientConfig);
        given(testConfigurer.configure(testContext, clientConfig)).willReturn(clientConfig);
        given(clientProvider.create(testContext, application, baseURI, clientConfig)).willReturn(clientInstance);

        ClientInstance result = sut.createClient(testContext, application, testConfigurer, baseURI);

        assertThat(result).isEqualTo(clientInstance);
        verify(application).clientProvider();
        verify(serviceLocatorUtil).getOne(clientProviderType);
        verify(clientProvider).configure(testContext, application, baseURI);
        verify(testConfigurer).configure(testContext, clientConfig);
        verify(clientProvider).create(testContext, application, baseURI, clientConfig);
    }

    @Test
    public void givenCustomClientProviderAddClientShouldAddClient() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        URI baseURI = URI.create("uri://test");
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        Class clientProviderType = TestClientProvider.class;
        ClientProvider clientProvider = sut.clientProvider = mock(ClientProvider.class);
        Object clientConfig = mock(Object.class);

        ClientInstance clientInstance = mock(ClientInstance.class);

        given(application.clientProvider()).willReturn(clientProviderType);
        given(reflectionUtil.newInstance(clientProviderType)).willReturn(clientProvider);
        given(clientProvider.configure(testContext, application, baseURI)).willReturn(clientConfig);
        given(testConfigurer.configure(testContext, clientConfig)).willReturn(clientConfig);
        given(clientProvider.create(testContext, application, baseURI, clientConfig)).willReturn(clientInstance);

        ClientInstance result = sut.createClient(testContext, application, testConfigurer, baseURI);

        assertThat(result).isEqualTo(clientInstance);
        verify(application).clientProvider();
        verify(reflectionUtil).newInstance(clientProviderType);
        verify(clientProvider).configure(testContext, application, baseURI);
        verify(testConfigurer).configure(testContext, clientConfig);
        verify(clientProvider).create(testContext, application, baseURI, clientConfig);
    }

}
