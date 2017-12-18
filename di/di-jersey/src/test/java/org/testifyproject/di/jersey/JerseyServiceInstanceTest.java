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
package org.testifyproject.di.jersey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;

import javax.inject.Provider;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testifyproject.TestContext;
import org.testifyproject.di.fixture.autowired.Greeting;
import org.testifyproject.di.fixture.autowired.impl.Haye;
import org.testifyproject.di.fixture.autowired.impl.Hello;
import org.testifyproject.di.fixture.common.InjectedService;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 *
 * @author saden
 */
public class JerseyServiceInstanceTest {

    JerseyServiceInstance sut;
    TestContext testContext;
    InjectionManager injectionManager;

    @Before
    public void init() throws Exception {
        testContext = mock(TestContext.class);
        injectionManager = Injections.createInjectionManager();

        //peform classpath descriptor scanning
        DynamicConfigurationService dcs =
                injectionManager.getInstance(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        Populator populator = dcs.getPopulator();

        JerseyDescriptorPopulator descriptorPopulator =
                new JerseyDescriptorPopulator(JerseyProperties.DEFAULT_DESCRIPTOR);
        populator.populate(descriptorPopulator);

        dc.commit();

        sut = new JerseyServiceInstance(testContext, injectionManager);
    }

    @After
    public void destroy() {
        sut.destroy();
    }

    @Test
    public void callToIsRunningShouldReturnTrue() {
        assertThat(sut.isRunning()).isTrue();
    }

    @Test
    public void callToInjectShouldInjectService() {
        InjectedService injectedService = new InjectedService();

        assertThat(injectedService.getField()).isNull();
        assertThat(injectedService.getMethod()).isNull();

        sut.inject(injectedService);

        assertThat(injectedService.getField()).isNotNull();
        assertThat(injectedService.getMethod()).isNotNull();
    }

    @Test
    public void callToGetContextShouldReturnServiceLocator() {
        assertThat(sut.getContext())
                .isNotNull()
                .isSameAs(injectionManager);
    }

    @Test
    public void givenTypeGetServiceShouldReturnService() {
        Hello result = sut.getService(Hello.class);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenContractTypeGetServiceShouldReturnService() {
        Greeting result = sut.getService(Greeting.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenTypeAndNameGetServiceShouldReturnService() {
        Haye result = sut.getService(Haye.class, "Haye");
        assertThat(result).isNotNull();
    }

    @Test
    public void givenTypeAndAnnotationGetServiceShouldReturnService() {
        Annotation[] qualifiers = new Annotation[]{new NamedImpl("Haye")};
        Haye result = sut.getService(Haye.class, qualifiers);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenProviderGetServiceShouldReturnProvider() {
        TypeToken<Provider<Greeting>> type = new TypeToken<Provider<Greeting>>() {
        };
        Provider<Greeting> result = sut.getService(type.getType());

        assertThat(result).isNotNull();
        assertThat(result.get()).isNotNull();
    }

    @Test
    public void givenMapGetServiceShouldReturnMap() {
        TypeToken<IterableProvider<Greeting>> type =
                new TypeToken<IterableProvider<Greeting>>() {
        };
        IterableProvider<Greeting> result = sut.getService(type.getType());
        assertThat(result).isNotEmpty();
    }

    @Test
    public void callToGetNameQualifersShouldReturnAnnotaitons() {
        assertThat(sut.getNameQualifers()).hasSize(1);
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotaitons() {
        assertThat(sut.getCustomQualifiers()).hasSize(1);
    }

}
