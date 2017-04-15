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
package org.testifyproject.di.hk2;

import java.lang.annotation.Annotation;
import javax.inject.Provider;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.testifyproject.TestContext;
import org.testifyproject.TestDescriptor;
import org.testifyproject.core.annotation.DefaultModule;
import org.testifyproject.core.annotation.DefaultScan;
import org.testifyproject.di.fixture.autowired.Greeting;
import org.testifyproject.di.fixture.autowired.impl.Haye;
import org.testifyproject.di.fixture.autowired.impl.Hello;
import org.testifyproject.di.fixture.common.ConstantContract;
import org.testifyproject.di.fixture.common.ConstantService;
import org.testifyproject.di.fixture.common.GenericContract;
import org.testifyproject.di.fixture.common.GenericService;
import org.testifyproject.di.fixture.common.InjectedService;
import org.testifyproject.di.fixture.common.WiredContract;
import org.testifyproject.di.fixture.common.WiredService;
import org.testifyproject.di.fixture.module.TestModule;
import org.testifyproject.guava.common.reflect.TypeToken;

/**
 *
 * @author saden
 */
public class HK2ServiceInstanceTest {

    HK2ServiceInstance cut;
    TestContext testContext;
    ServiceLocator serviceLocator;

    @Before
    public void init() {
        testContext = mock(TestContext.class);
        serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        cut = new HK2ServiceInstance(testContext, serviceLocator);
    }

    @After
    public void destroy() {
        cut.destroy();
    }

    @Test
    public void callToIsRunningShouldReturnTrue() {
        assertThat(cut.isRunning()).isTrue();
    }

    @Test
    public void callToInjectShouldInjectService() {
        InjectedService injectedService = new InjectedService();

        assertThat(injectedService.getField()).isNull();
        assertThat(injectedService.getMethod()).isNull();

        cut.inject(injectedService);

        assertThat(injectedService.getField()).isNotNull();
        assertThat(injectedService.getMethod()).isNotNull();
    }

    @Test
    public void callToGetContextShouldReturnServiceLocator() {
        assertThat(cut.getContext())
                .isNotNull()
                .isSameAs(serviceLocator);
    }

    @Test
    public void givenTypeGetServiceShouldReturnService() {
        Hello result = cut.getService(Hello.class);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenContractTypeGetServiceShouldReturnService() {
        Greeting result = cut.getService(Greeting.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenTypeAndNameGetServiceShouldReturnService() {
        Haye result = cut.getService(Haye.class, "Haye");
        assertThat(result).isNotNull();
    }

    @Test
    public void givenGenericTypeGetServiceShouldReturnService() {
        GenericContract<String> constant = new GenericService();
        String name = "genericConstant";
        Class contract = GenericContract.class;

        cut.addConstant(constant, name, contract);

        TypeToken<GenericContract<String>> type = new TypeToken<GenericContract<String>>() {
        };

        GenericContract<String> result = cut.getService(type.getType());

        assertThat(result).isNotNull();
    }

    @Test
    public void givenGenericTypeWithQualifierGetServiceShouldReturnService() {
        GenericContract<String> constant = new GenericService();
        String name = "genericConstant";
        Class contract = GenericContract.class;

        cut.addConstant(constant, name, contract);

        TypeToken<GenericContract<String>> type = new TypeToken<GenericContract<String>>() {
        };

        Annotation[] qualifiers = new Annotation[]{new NamedImpl(name)};

        GenericContract<String> result = cut.getService(type.getType(), qualifiers);

        assertThat(result).isNotNull();
    }

    @Test
    public void givenTypeAndAnnotationGetServiceShouldReturnService() {
        Annotation[] qualifiers = new Annotation[]{new NamedImpl("Haye")};
        Haye result = cut.getService(Haye.class, qualifiers);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenConstantAddConstantShouldAddTheInstanceToServiceLocator() {
        ConstantService service = new ConstantService("greeting");

        cut.addConstant(service, null, null);

        ConstantService result = serviceLocator.getService(ConstantService.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenConstantWithNameAddConstantShouldAddTheInstanceToServiceLocator() {
        ConstantService service = new ConstantService("greeting");
        String name = "testConstant";

        cut.addConstant(service, name, null);

        ConstantService result = serviceLocator.getService(ConstantService.class, name);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenConstantWithContractAddConstantShouldAddTheInstanceToServiceLocator() {
        ConstantService service = new ConstantService("greeting");
        String name = null;
        Class<ConstantContract> contract = ConstantContract.class;

        cut.addConstant(service, name, contract);

        ConstantContract result = serviceLocator.getService(ConstantContract.class, name);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenConstantWithNameAndContractAddConstantShouldAddTheInstanceToServiceLocator() {
        ConstantService service = new ConstantService("greeting");
        String name = "testConstant";
        Class<ConstantContract> contract = ConstantContract.class;

        cut.addConstant(service, name, contract);

        ConstantContract result = serviceLocator.getService(ConstantContract.class, name);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenConstantWithNameAndContractReplaceShouldReplaceInstanceInServiceLocator() {
        String name = "hello";
        Hello constant = new Hello();
        Class<Greeting> contract = Greeting.class;

        cut.replace(constant, name, contract);

        Greeting result = serviceLocator.getService(contract, name);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenConstantWithNameReplaceShouldReplaceInstanceInServiceLocator() {
        String name = "hello";
        Hello constant = new Hello();
        Class<Greeting> contract = null;

        cut.replace(constant, name, contract);

        Greeting result = serviceLocator.getService(Hello.class, name);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenConstantWithContractReplaceShouldReplaceInstanceInServiceLocator() {
        String name = null;
        Hello constant = new Hello();
        Class<Greeting> contract = Greeting.class;

        cut.replace(constant, name, contract);

        Greeting result = serviceLocator.getService(contract);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenModuleAddModuleShouldAddModule() {
        DefaultModule module = new DefaultModule(TestModule.class);
        cut.addModules(module);

        WiredContract contract = serviceLocator.getService(WiredContract.class);
        WiredService service = serviceLocator.getService(WiredService.class);

        assertThat(contract).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    public void givenDescriptorResourceScanShouldAddServices() {
        serviceLocator = ServiceLocatorFactory.getInstance().create(null);
        cut = new HK2ServiceInstance(testContext, serviceLocator);
        TestDescriptor testDescriptor = mock(TestDescriptor.class);
        ClassLoader classLoader = this.getClass().getClassLoader();

        given(testContext.getTestDescriptor()).willReturn(testDescriptor);
        given(testDescriptor.getTestClassLoader()).willReturn(classLoader);

        cut.addScans(new DefaultScan("META-INF/hk2-locator/default"));

        Haye greeting = cut.getService(Haye.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenProviderGetServiceShouldReturnProvider() {
        TypeToken<Provider<Greeting>> type = new TypeToken<Provider<Greeting>>() {
        };
        Provider<Greeting> result = cut.getService(type.getType());

        assertThat(result).isNotNull();
        assertThat(result.get()).isNotNull();
    }

    @Test
    public void givenMapGetServiceShouldReturnMap() {
        TypeToken<IterableProvider<Greeting>> type = new TypeToken<IterableProvider<Greeting>>() {
        };
        IterableProvider<Greeting> result = cut.getService(type.getType());
        assertThat(result).isNotEmpty();
    }

    @Test
    public void callToGetNameQualifersShouldReturnAnnotaitons() {
        assertThat(cut.getNameQualifers()).hasSize(1);
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotaitons() {
        assertThat(cut.getCustomQualifiers()).hasSize(1);
    }

}
