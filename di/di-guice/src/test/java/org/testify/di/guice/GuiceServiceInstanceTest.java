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
package org.testify.di.guice;

import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.testify.core.util.ModuleImpl;
import org.testify.di.fixture.common.Greeting;
import org.testify.di.fixture.common.GreetingModule;
import org.testify.di.fixture.common.GreetingQualfier;
import org.testify.di.fixture.common.impl.Caio;
import org.testify.di.fixture.common.impl.Haye;
import org.testify.di.fixture.common.impl.Hello;
import org.testify.di.fixture.dynamic.DynamicConstant;
import org.testify.di.fixture.dynamic.DynamicContract;
import org.testify.di.fixture.dynamic.DynamicModule;

/**
 *
 * @author saden
 */
public class GuiceServiceInstanceTest {

    private Injector injector;
    private GuiceServiceInstance cut;

    @Before
    public void init() {
        cut = new GuiceServiceInstance(injector);
        ModuleImpl module = new ModuleImpl(GreetingModule.class, Boolean.FALSE);
        cut.addModules(module);
    }

    @Test
    public void callToGetContextShouldReturnInjector() {
        Object result = cut.getContext();
        assertThat(result).isSameAs(injector);
    }

    @Test
    public void givenContractTypeGetServiceShouldReturnService() {
        Greeting result = cut.getService(Greeting.class);
        assertThat(result).isNotNull().isInstanceOf(Hello.class);
    }

    @Test
    public void givenImplementationTypeGetServiceShouldReturnService() {
        Hello result = cut.getService(Hello.class);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenContractTypeAndNameGetServiceShouldReturnService() {
        Greeting result = cut.getService(Greeting.class, Names.named("Haye"));
        assertThat(result).isNotNull().isInstanceOf(Haye.class);
    }

    @Test
    public void givenContractTypeAndQualifierGetServiceShouldReturnService() {
        GreetingQualfier qualifier = new GreetingQualfier() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return GreetingQualfier.class;
            }
        };
        Greeting result = cut.getService(Greeting.class, qualifier);
        assertThat(result).isNotNull().isInstanceOf(Caio.class);
    }

    @Test
    public void givenInstanceAddConstantShouldAddConstant() {
        String value = "test";
        DynamicConstant dynamicConstant = new DynamicConstant(value);
        cut.addConstant(dynamicConstant, null, null);

        DynamicConstant result = cut.getService(DynamicConstant.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceAndNameAddConstantShouldAddConstant() {
        String value = "test";
        DynamicConstant dynamicConstant = new DynamicConstant(value);
        cut.addConstant(dynamicConstant, "constant", null);

        DynamicConstant result = cut.getService(DynamicConstant.class, Names.named("constant"));

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceAndNameAndContractAddConstantShouldAddConstant() {
        String value = "test";
        DynamicConstant dynamicConstant = new DynamicConstant(value);
        cut.addConstant(dynamicConstant, "constant", DynamicContract.class);

        DynamicContract result = cut.getService(DynamicContract.class, Names.named("constant"));

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    public void givenInstanceAndContractShouldRepleaceService() {
        Greeting instance = mock(Greeting.class);
        cut.replace(instance, null, Greeting.class);

        Greeting result = cut.getService(Greeting.class);

        assertThat(result).isSameAs(instance);
    }

    @Test
    public void givenInstanceAndImplementationShouldRepleaceService() {
        Hello instance = mock(Hello.class);
        cut.replace(instance, null, Hello.class);

        Hello result = cut.getService(Hello.class);

        assertThat(result).isSameAs(instance);
    }

    @Test
    public void givenInstanceAndNameAndContranctShouldRepleaceService() {
        String name = "Haye";
        Greeting instance = mock(Greeting.class);
        cut.replace(instance, name, Greeting.class);

        Greeting result = cut.getService(Greeting.class, Names.named(name));

        assertThat(result).isSameAs(instance);
    }

    @Test
    public void givenModuleAddModuleShouldAddModule() {
        ModuleImpl module = new ModuleImpl(DynamicModule.class, Boolean.FALSE);
        cut.addModules(module);

        DynamicContract result = cut.getService(DynamicContract.class);
        assertThat(result).isNotNull();

        Greeting greeting = cut.getService(Greeting.class);
        assertThat(greeting).isNotNull().isInstanceOf(Hello.class);
    }

    @Test
    public void callToGetInjectionAnnotationsShouldReturnAnnotaitons() {
        assertThat(cut.getInjectionAnnotations()).hasSize(3);
    }

    @Test
    public void callToGetNameQualifersShouldReturnAnnotaitons() {
        assertThat(cut.getNameQualifers()).hasSize(2);
    }

    @Test
    public void callToGetCustomQualifiersShouldReturnAnnotaitons() {
        assertThat(cut.getCustomQualifiers()).hasSize(2);
    }
}
