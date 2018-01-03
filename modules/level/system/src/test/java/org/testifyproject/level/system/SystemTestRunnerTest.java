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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testifyproject.core.TestContextProperties.CLIENT_INSTANCE;
import static org.testifyproject.core.TestContextProperties.CLIENT_PROVIDER;
import static org.testifyproject.core.TestContextProperties.SERVER_INSTANCE;
import static org.testifyproject.core.TestContextProperties.SERVER_PROVIDER;
import static org.testifyproject.core.TestContextProperties.SERVICE_INSTANCE;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.ClientInstance;
import org.testifyproject.ClientProvider;
import org.testifyproject.FieldDescriptor;
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
import org.testifyproject.annotation.Application;
import org.testifyproject.core.DefaultServiceProvider;
import org.testifyproject.core.TestContextProperties;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.CollaboratorReifier;
import org.testifyproject.extension.FinalReifier;
import org.testifyproject.extension.PostVerifier;
import org.testifyproject.extension.PreVerifier;
import org.testifyproject.extension.Verifier;
import org.testifyproject.extension.annotation.Strict;
import org.testifyproject.extension.annotation.SystemCategory;
import org.testifyproject.guava.common.collect.ImmutableList;
import org.testifyproject.guava.common.collect.ImmutableMap;
import org.testifyproject.level.system.fixture.TestClientProvider;
import org.testifyproject.level.system.fixture.TestServerProvider;
import org.testifyproject.trait.PropertiesWriter;

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
        sut.resourceController = mock(ResourceController.class);
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
        Optional<SutDescriptor> foundSutDescriptor = Optional.empty();
        Object testInstance = new Object();
        Collection<Class<? extends Annotation>> guidelines = ImmutableList.of();
        Optional<Application> foundApplication = Optional.empty();

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(testDescriptor.getApplication()).willReturn(foundApplication);

        sut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testContext).getSutDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getGuidelines();
        verify(testDescriptor).getApplication();

        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor);
    }

    @Test
    public void givenApplicationStartShouldStart() throws Exception {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Object testInstance = new Object();
        Application application = mock(Application.class);
        Optional<Application> foundApplication = Optional.of(application);
        List<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        CollaboratorReifier collaboratorReifier = mock(CollaboratorReifier.class);
        List<CollaboratorReifier> collaboratorReifiers = ImmutableList.of(collaboratorReifier);
        PreVerifier configurationVerifier = mock(PreVerifier.class);
        List<PreVerifier> configurationVerifiers = ImmutableList.of(configurationVerifier);
        ResourceController resourceController = mock(ResourceController.class);
        ClientProvider clientProvider = mock(ClientProvider.class);
        ServerProvider serverProvider = mock(ServerProvider.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);

        FinalReifier finalReifier = mock(FinalReifier.class);
        List<FinalReifier> finalReifiers = ImmutableList.of(finalReifier);

        Verifier preiVerifier = mock(Verifier.class);
        List<Verifier> preiVerifiers = ImmutableList.of(preiVerifier);

        given(testContext.getTestConfigurer()).willReturn(testConfigurer);
        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(testDescriptor.getApplication()).willReturn(foundApplication);

        given(serviceLocatorUtil.findAllWithFilter(
                CollaboratorReifier.class,
                SystemCategory.class)
        ).willReturn(collaboratorReifiers);
        given(serviceLocatorUtil.findAllWithFilter(
                PreVerifier.class,
                guidelines,
                SystemCategory.class)
        ).willReturn(configurationVerifiers);
        given(serviceLocatorUtil.getOne(ResourceController.class))
                .willReturn(resourceController);

        willReturn(clientProvider).given(sut).createClientProvider(testContext, application);
        willReturn(serverProvider).given(sut).createServerProvider(testContext, application);
        willDoNothing().given(sut)
                .createServer(testContext, testConfigurer, serverProvider, application);
        willDoNothing().given(sut)
                .createClient(testContext, testConfigurer, clientProvider, application);
        willReturn(serviceInstance).given(sut)
                .createService(testContext, testDescriptor, testConfigurer);
        willDoNothing().given(sut)
                .createSut(testContext, sutDescriptor, serviceInstance, testInstance);

        given(serviceLocatorUtil.findAllWithFilter(
                FinalReifier.class,
                SystemCategory.class)
        ).willReturn(finalReifiers);

        given(serviceLocatorUtil.findAllWithFilter(Verifier.class,
                guidelines,
                SystemCategory.class)
        ).willReturn(preiVerifiers);

        sut.start(testContext);

        verify(testContext).getTestConfigurer();
        verify(testContext).getTestDescriptor();
        verify(testContext).getSutDescriptor();
        verify(testContext).getTestInstance();
        verify(testDescriptor).getGuidelines();
        verify(testDescriptor).getApplication();
        verify(serviceLocatorUtil)
                .findAllWithFilter(CollaboratorReifier.class, SystemCategory.class);
        verify(serviceLocatorUtil)
                .findAllWithFilter(PreVerifier.class, guidelines, SystemCategory.class);
        verify(serviceLocatorUtil)
                .getOne(ResourceController.class);
        verify(resourceController).start(testContext);
        verify(serviceLocatorUtil)
                .findAllWithFilter(FinalReifier.class, SystemCategory.class);
        verify(serviceLocatorUtil)
                .findAllWithFilter(Verifier.class, guidelines, SystemCategory.class);
        verify(testContext, times(2)).verify();

        verifyNoMoreInteractions(testContext, testConfigurer, testDescriptor);
    }

    @Test
    public void callToStopShouldStopTest() throws Exception {
        TestContext testContext = mock(TestContext.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        Object testInstance = new Object();
        SutDescriptor sutDescriptor = mock(SutDescriptor.class);
        Optional<SutDescriptor> foundSutDescriptor = Optional.of(sutDescriptor);
        Collection<Class<? extends Annotation>> guidelines = ImmutableList.of(Strict.class);

        PostVerifier postVerifier = mock(PostVerifier.class);
        List<PostVerifier> postVerifiers = ImmutableList.of(postVerifier);

        FieldDescriptor fieldDescriptor = mock(FieldDescriptor.class);
        Collection<FieldDescriptor> fieldDescriptors = ImmutableList.of(fieldDescriptor);

        ClientInstance clientInstance = mock(ClientInstance.class);
        Optional<ClientInstance> foundClientInstance = Optional.of(clientInstance);
        ClientProvider clientProvider = mock(ClientProvider.class);
        Optional<ClientProvider> foundClientProvider = Optional.of(clientProvider);

        ServerInstance serverInstance = mock(ServerInstance.class);
        Optional<ServerInstance> foundServerInstance = Optional.of(serverInstance);
        ServerProvider serverProvider = mock(ServerProvider.class);
        Optional<ServerProvider> foundServerProvider = Optional.of(serverProvider);

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testContext.getTestInstance()).willReturn(testInstance);
        given(testContext.getSutDescriptor()).willReturn(foundSutDescriptor);
        given(testDescriptor.getGuidelines()).willReturn(guidelines);
        given(serviceLocatorUtil.findAllWithFilter(
                PostVerifier.class,
                guidelines,
                SystemCategory.class)
        ).willReturn(postVerifiers);
        given(testDescriptor.getFieldDescriptors()).willReturn(fieldDescriptors);

        given(testContext.<ClientInstance>findProperty(CLIENT_INSTANCE))
                .willReturn(foundClientInstance);
        given(testContext.<ClientProvider>findProperty(CLIENT_PROVIDER))
                .willReturn(foundClientProvider);

        given(testContext.<ServerInstance>findProperty(SERVER_INSTANCE))
                .willReturn(foundServerInstance);
        given(testContext.<ServerProvider>findProperty(SERVER_PROVIDER))
                .willReturn(foundServerProvider);

        sut.stop(testContext);

        verify(testContext).getTestDescriptor();
        verify(testContext).getTestInstance();
        verify(testContext).getSutDescriptor();
        verify(testDescriptor).getGuidelines();
        verify(serviceLocatorUtil)
                .findAllWithFilter(PostVerifier.class, guidelines, SystemCategory.class);
        verify(testContext).verify();
        verify(postVerifier).verify(testContext);
        verify(fieldDescriptor).destroy(testInstance);
        verify(sutDescriptor).destroy(testInstance);
        verify(clientProvider).destroy(clientInstance);
        verify(serverProvider).stop(serverInstance);
        verify(sut.resourceController).stop(testContext);
    }

    @Test
    public void callToCreateServerProviderWithoutConfigurationShouldDiscoverProvider() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        Class serverProviderType = ServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);

        given(application.serverProvider()).willReturn(serverProviderType);
        given(application.start()).willReturn("");
        given(application.stop()).willReturn("");
        given(serviceLocatorUtil.getOne(serverProviderType)).willReturn(serverProvider);

        ServerProvider result = sut.createServerProvider(testContext, application);

        assertThat(result).isEqualTo(serverProvider);
        verify(application).serverProvider();
        verify(serviceLocatorUtil).getOne(serverProviderType);
        verify(testContext).addProperty(TestContextProperties.SERVER_PROVIDER, serverProvider);
    }

    @Test
    public void callToCreateServerProviderWithoutConfigurationShouldCreateProvider() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        Class serverProviderType = TestServerProvider.class;
        ServerProvider serverProvider = mock(ServerProvider.class);

        given(application.serverProvider()).willReturn(serverProviderType);
        given(reflectionUtil.newInstance(serverProviderType)).willReturn(serverProvider);

        ServerProvider result = sut.createServerProvider(testContext, application);

        assertThat(result).isEqualTo(serverProvider);
        verify(application).serverProvider();
        verify(reflectionUtil).newInstance(serverProviderType);
        verify(testContext).addProperty(TestContextProperties.SERVER_PROVIDER, serverProvider);
    }

    @Test
    public void callToCreateClientProviderWithoutConfigurationShouldDiscoverProvider() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        Class clientProviderType = ClientProvider.class;
        ClientProvider clientProvider = mock(ClientProvider.class);

        given(application.clientProvider()).willReturn(clientProviderType);
        given(serviceLocatorUtil.getOne(clientProviderType)).willReturn(clientProvider);

        ClientProvider result = sut.createClientProvider(testContext, application);

        assertThat(result).isEqualTo(clientProvider);
        verify(application).clientProvider();
        verify(serviceLocatorUtil).getOne(clientProviderType);
        verify(testContext).addProperty(TestContextProperties.CLIENT_PROVIDER, clientProvider);
    }

    @Test
    public void callToCreateClientProviderWithoutConfigurationShouldCreateProvider() {
        TestContext testContext = mock(TestContext.class);
        Application application = mock(Application.class);
        Class clientProviderType = TestClientProvider.class;
        ClientProvider clientProvider = mock(ClientProvider.class);

        given(application.clientProvider()).willReturn(clientProviderType);
        given(reflectionUtil.newInstance(clientProviderType)).willReturn(clientProvider);

        ClientProvider result = sut.createClientProvider(testContext, application);

        assertThat(result).isEqualTo(clientProvider);
        verify(application).clientProvider();
        verify(reflectionUtil).newInstance(clientProviderType);
        verify(testContext).addProperty(TestContextProperties.CLIENT_PROVIDER, clientProvider);
    }

    @Test
    public void callToCreateServiceWithExistingServiceInstanceShouldReturnExistingInstance() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);

        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Optional<ServiceInstance> foundServiceInstance = Optional.of(serviceInstance);

        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE))
                .willReturn(foundServiceInstance);

        ServiceInstance result = sut.createService(testContext, testDescriptor, testConfigurer);

        assertThat(result).isEqualTo(serviceInstance);
        verify(testContext).findProperty(SERVICE_INSTANCE);
    }

    @Test
    public void callToCreateServiceWithoutExistingServiceInstanceShouldReturnNewInstance() {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);

        Optional<ServiceInstance> foundServiceInstance = Optional.empty();
        ServiceProvider serviceProvider = mock(ServiceProvider.class);
        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        Object serviceContext = new Object();

        given(testContext.<ServiceInstance>findProperty(SERVICE_INSTANCE))
                .willReturn(foundServiceInstance);

        given(serviceLocatorUtil.getFromHintOrDefault(
                eq(testContext),
                eq(ServiceProvider.class),
                eq(DefaultServiceProvider.class),
                any())
        ).willReturn(serviceProvider);

        given(serviceProvider.create(testContext)).willReturn(serviceContext);
        given(serviceProvider.configure(testContext, serviceContext))
                .willReturn(serviceInstance);

        ServiceInstance result = sut.createService(testContext, testDescriptor, testConfigurer);

        assertThat(result).isEqualTo(serviceInstance);
        verify(testContext).findProperty(SERVICE_INSTANCE);
        verify(serviceLocatorUtil).getFromHintOrDefault(
                eq(testContext),
                eq(ServiceProvider.class),
                eq(DefaultServiceProvider.class),
                any());
        verify(serviceProvider).create(testContext);
        verify(testConfigurer).configure(testContext, serviceContext);
        verify(serviceProvider).configure(testContext, serviceContext);
        verify(serviceProvider).postConfigure(testContext, serviceInstance);
    }

    @Test
    public void callToCreateServerShouldConfigureAndStartServer() throws Exception {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        ServerProvider serverProvider = mock(ServerProvider.class);
        Application application = mock(Application.class);

        Object serverConfig = new Object();
        ServerInstance serverInstance = mock(ServerInstance.class);
        String fqn = "faq";
        Map<String, Object> properties = ImmutableMap.of();
        URI baseURI = URI.create("uri://test");
        Instance instance = mock(Instance.class);
        Object server = new Object();
        PropertiesWriter propertiesWriter = mock(PropertiesWriter.class);

        given(serverProvider.configure(testContext)).willReturn(serverConfig);
        given(testConfigurer.configure(testContext, serverConfig)).willReturn(serverConfig);
        given(serverProvider.start(testContext, application, serverConfig))
                .willReturn(serverInstance);
        given(serverInstance.getFqn()).willReturn(fqn);
        given(serverInstance.getProperties()).willReturn(properties);
        given(serverInstance.getBaseURI()).willReturn(baseURI);
        given(serverInstance.getServer()).willReturn(instance);
        given(instance.getValue()).willReturn(server);
        given(testContext.addProperty(any(), any())).willReturn(propertiesWriter);
        given(propertiesWriter.addProperty(any(), any())).willReturn(propertiesWriter);

        sut.createServer(testContext, testConfigurer, serverProvider, application);

        verify(serverInstance).getFqn();
        verify(serverInstance).getProperties();
        verify(serverInstance).getBaseURI();
        verify(serverInstance).getServer();
        verify(instance).getValue();
        verify(testContext).addProperty(TestContextProperties.SERVER_INSTANCE, serverInstance);
        verify(propertiesWriter).addProperty(fqn, properties);
        verify(propertiesWriter).addProperty(TestContextProperties.SERVER_BASE_URI, baseURI);
        verify(propertiesWriter).addProperty(TestContextProperties.SERVER, server);

    }

    @Test
    public void callToCreateClientShouldConfigureAndCreateClient() throws Exception {
        TestContext testContext = mock(TestContext.class);
        TestConfigurer testConfigurer = mock(TestConfigurer.class);
        ClientProvider clientProvider = mock(ClientProvider.class);
        Application application = mock(Application.class);

        Object clientConfig = new Object();
        ClientInstance<Object, Object> clientInstance = mock(ClientInstance.class);
        String fqn = "faq";
        Map<String, Object> properties = ImmutableMap.of();
        URI baseURI = URI.create("uri://test");
        Instance instance = mock(Instance.class);
        Object client = new Object();
        PropertiesWriter propertiesWriter = mock(PropertiesWriter.class);
        Instance<Object> clientSupplierInstance = mock(Instance.class);
        Optional<Instance<Object>> foundClientSupplier = Optional.of(clientSupplierInstance);
        Object clientSupplier = new Object();

        given(testContext.getProperty(TestContextProperties.SERVER_BASE_URI))
                .willReturn(baseURI);

        given(clientProvider.configure(testContext, application, baseURI))
                .willReturn(clientConfig);
        given(testConfigurer.configure(testContext, clientConfig)).willReturn(clientConfig);
        given(clientProvider.create(testContext, application, baseURI, clientConfig))
                .willReturn(clientInstance);
        given(clientInstance.getFqn()).willReturn(fqn);
        given(clientInstance.getProperties()).willReturn(properties);
        given(clientInstance.getClient()).willReturn(instance);
        given(instance.getValue()).willReturn(client);
        given(testContext.addProperty(any(), any())).willReturn(propertiesWriter);
        given(propertiesWriter.addProperty(any(), any())).willReturn(propertiesWriter);
        given(clientInstance.getClientSupplier()).willReturn(foundClientSupplier);
        given(clientSupplierInstance.getValue()).willReturn(clientSupplier);

        sut.createClient(testContext, testConfigurer, clientProvider, application);

        verify(clientInstance).getFqn();
        verify(clientInstance).getProperties();
        verify(clientInstance).getClient();
        verify(instance).getValue();
        verify(testContext).addProperty(TestContextProperties.CLIENT_INSTANCE, clientInstance);
        verify(propertiesWriter).addProperty(fqn, properties);
        verify(propertiesWriter).addProperty(TestContextProperties.CLIENT, client);
        verify(testContext).addProperty(TestContextProperties.CLIENT_SUPPLIER, clientSupplier);

    }

}
