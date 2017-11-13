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
package org.testifyproject.core.extension.instrument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.testifyproject.Instance;
import org.testifyproject.TestContext;
import org.testifyproject.core.DefaultInstance;
import org.testifyproject.core.util.InstrumentUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.ProxyInstance;
import org.testifyproject.extension.ProxyInstanceProvider;
import org.testifyproject.fixture.instrument.Greeter;
import org.testifyproject.guava.common.collect.ImmutableList;

/**
 *
 * @author saden
 */
public class DefaultProxyInstanceControllerTest {

    DefaultProxyInstanceController sut;
    ServiceLocatorUtil serviceLocatorUtil;
    InstrumentUtil instrumentUtil;

    @Before
    public void init() {
        instrumentUtil = mock(InstrumentUtil.class);
        serviceLocatorUtil = mock(ServiceLocatorUtil.class);

        sut = new DefaultProxyInstanceController(serviceLocatorUtil, instrumentUtil);
    }

    @Test
    public void callToDefaultConstructorShouldCreateInstance() {
        sut = new DefaultProxyInstanceController();
    }

    @Test
    public void givenNoProxyInstanceProvidersCreateShouldReturnEmptyList() {
        TestContext testContext = mock(TestContext.class);
        List<ProxyInstanceProvider> proxyInstanceProviders = ImmutableList.of();

        given(serviceLocatorUtil.findAll(ProxyInstanceProvider.class))
                .willReturn(proxyInstanceProviders);

        List<Instance> result = sut.create(testContext);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenProxyInstanceProvidersCreateShouldCreateAndReturnProxyInstances() {
        TestContext testContext = mock(TestContext.class);
        ProxyInstanceProvider proxyInstanceProvider = mock(ProxyInstanceProvider.class);
        List<ProxyInstanceProvider> proxyInstanceProviders =
                ImmutableList.of(proxyInstanceProvider);
        ProxyInstance proxyInstance = mock(ProxyInstance.class);
        List<ProxyInstance> proxyInstances = ImmutableList.of(proxyInstance);
        Class<Greeter> proxyType = Greeter.class;
        String proxyName = "proxyName";
        Supplier proxyDelegateSupplier = () -> new Greeter();
        ClassLoader classLoader = DefaultProxyInstanceControllerTest.class.getClassLoader();
        Greeter proxy = new Greeter();

        given(serviceLocatorUtil.findAll(ProxyInstanceProvider.class))
                .willReturn(proxyInstanceProviders);
        given(proxyInstanceProvider.get(testContext)).willReturn(proxyInstances);
        given(proxyInstance.getType()).willReturn(proxyType);
        given(proxyInstance.getName()).willReturn(proxyName);
        given(proxyInstance.getDelegate()).willReturn(proxyDelegateSupplier);
        given(testContext.getTestClassLoader()).willReturn(classLoader);
        given(instrumentUtil.createProxy(proxyType, classLoader, proxyDelegateSupplier))
                .willReturn(proxy);

        List<Instance> result = sut.create(testContext);

        assertThat(result).contains(DefaultInstance.of(proxy, proxyName, proxyType));
    }

}
