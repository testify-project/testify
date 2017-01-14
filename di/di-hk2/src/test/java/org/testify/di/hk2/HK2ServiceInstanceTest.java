/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.di.hk2;

import org.testify.guava.common.reflect.TypeToken;
import org.testify.core.util.ModuleImpl;
import org.testify.core.util.ScanImpl;
import org.testify.di.fixture.autowired.Greeting;
import org.testify.di.fixture.autowired.impl.Haye;
import org.testify.di.fixture.autowired.impl.Hello;
import org.testify.di.fixture.common.ConstantService;
import org.testify.di.fixture.common.WiredContract;
import org.testify.di.fixture.common.WiredService;
import org.testify.di.fixture.module.TestModule;
import java.lang.annotation.Annotation;
import javax.inject.Provider;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author saden
 */
public class HK2ServiceInstanceTest {

    HK2ServiceInstance cut;
    ServiceLocator context;

    @Before
    public void init() {
        context = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        cut = new HK2ServiceInstance(context);
    }

    @Test
    public void callToDestroyShouldDestroyServiceLocator() {
        cut.destroy();
    }

    @Test
    public void callToGetContextShouldReturnServiceLocator() {
        assertThat(cut.getContext())
                .isNotNull()
                .isSameAs(context);
    }

    @Test
    public void givenTypeGetServiceShouldReturnService() {
        Hello greeting = cut.getService(Hello.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenContractTypeGetServiceShouldReturnService() {
        Greeting greeting = cut.getService(Greeting.class);
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenTypeAndAnnotationGetServiceShouldReturnService() {
        Haye greeting = cut.getService(Haye.class, new Annotation[]{new NamedImpl("Haye")});
        assertThat(greeting).isNotNull();
    }

    @Test
    public void givenServiceInstanceAddConstantShouldAddTheService() {
        ConstantService service = new ConstantService("greeting");
        cut.addConstant(service, null, null);

        ConstantService result = context.getService(ConstantService.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenValidParamsReplaceWithConstantShouldRepalceService() {
        String name = "newgreeting";
        Hello constant = new Hello();
        cut.replace(constant, name, Greeting.class);
        Greeting result = context.getService(Greeting.class, name);
        assertThat(result).isSameAs(constant);
    }

    @Test
    public void givenModuleAddModuleShouldAddModule() {
        ModuleImpl module = new ModuleImpl(TestModule.class, Boolean.FALSE);
        cut.addModules(module);

        WiredContract contract = context.getService(WiredContract.class);
        WiredService service = context.getService(WiredService.class);

        assertThat(contract).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    public void givenDescriptorResourceScanShouldAddServices() {
        context = ServiceLocatorFactory.getInstance().create(null);
        cut = new HK2ServiceInstance(context);

        cut.addScans(new ScanImpl("META-INF/hk2-locator/default"));

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
    public void callToGetInjectionAnnotationsShouldReturnAnnotaitons() {
        assertThat(cut.getInjectionAnnotations()).hasSize(2);
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
